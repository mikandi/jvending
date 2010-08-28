/**
 *    Copyright 2003-2010 Shane Isbell
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.jvending.provisioning.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.AdapterInfo;
import javax.provisioning.BundleDescriptor;
import javax.provisioning.BundleRepository;
import javax.provisioning.Capabilities;
import javax.provisioning.Constants;
import javax.provisioning.MatchPolicy;
import javax.provisioning.ProvisioningContext;
import javax.provisioning.ProvisioningException;
import javax.provisioning.matcher.AttributeMatcher;
import javax.provisioning.matcher.MatcherException;
import javax.servlet.ServletContext;

import org.hibernate.Session;
import org.jvending.provisioning.config.AttributeMatcherRepository;
import org.jvending.provisioning.config.DeliverableRepository;
import org.jvending.provisioning.config.MimeTypeRepository;
import org.jvending.provisioning.dao.BundleDescriptorDAO;
import org.jvending.provisioning.dao.ClientBundleDAO;
import org.jvending.provisioning.dao.impl.ParDAOImpl;
import org.jvending.provisioning.model.clientbundle.ClientBundle;
import org.jvending.provisioning.stocking.DataSink;
import org.jvending.provisioning.stocking.filter.FormatFilter;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.provisioning.stocking.par.CatalogProperty;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;
import org.jvending.registry.hibernate.HibernateDAORegistry;


/**
 * Implementation of the BundleRepository and DataSink.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

//TODO: Add back in BundleRepositoryUpdater

public final class BundleRepositoryImpl implements BundleRepository, DataSink {

    private static Logger logger = Logger.getLogger("org.jvending.provisioning.impl.BundleRepositoryImpl");//20

    private AttributeMatcherRepository attributeRepository;

    private BundleDescriptorDAO bundleDescriptorDAO;

    private ClientBundleDAO clientBundleDAO;

    private ProvisioningContext provisioningContext;

    private Session session;

    private MimeTypeRepository mimeTypeRepository;

    private DeliverableRepository deliverableRepository;

    private StockingHandlerConfig stockingHandlerConfig;

    private String contentDeliveryUri;

    /*
    * This empty constructor is here for the instantiation of a DataSink, which only exposes the addParFile method of this
    * class. Do not try to cast a DataSink to a BundleRepository because it will not be properly initialized for other
    * methods.
    */
    public BundleRepositoryImpl() {
    }

    public BundleRepositoryImpl(ClientBundleDAO clientBundleDAO,
                                BundleDescriptorDAO bundleDescriptorDAO,
                                ParDAOImpl parDAO,
                                AttributeMatcherRepository attributeMatcherRepository,
                                MimeTypeRepository mimeTypeRepository,
                                DeliverableRepository deliverableRepository,
                                ProvisioningContext provisioningContext) {

        this.provisioningContext = provisioningContext;

        this.attributeRepository = attributeMatcherRepository;
        this.mimeTypeRepository = mimeTypeRepository;
        this.deliverableRepository = deliverableRepository;

  //      this.parDAO = parDAO;
        this.bundleDescriptorDAO = bundleDescriptorDAO;
        this.clientBundleDAO = clientBundleDAO;

        if (attributeRepository == null) {
            logger.severe("JV-1501-001: No AttributeMatcherRepository found.");
        }
        if (bundleDescriptorDAO == null) {
            logger.severe("JV-1501-002: No BundleDescriptorDAO found");
        }
        if (clientBundleDAO == null) {
            logger.severe("JV-1501-003: No ClientBundleDAO found");
        }
        if (provisioningContext == null) {
            logger.severe("JV-1501-012: No ProvisioningContext found");
        }
        if (mimeTypeRepository == null) {
            logger.severe("JV-1501-013: No MimeTypeRepository found");
        }
        if (deliverableRepository == null) {
            logger.severe("JV-1501-013: No DeliverableRepository found");
        }
        /*
        if (parDAO == null) {
            logger.severe("JV-1501-014: No ParDAO found");
        }
        */
        ServletContext sc = provisioningContext.getServletContext();
        if(sc != null)
        {
            contentDeliveryUri = provisioningContext.getServletContext().getInitParameter("CONTENT_DELIVERY_URI");

            if (contentDeliveryUri == null) {
                logger.severe("JV-1501-020: Can not find the CONTENT_DELIVERY_URI attribute. Can not create a valid BundleDescriptor");
            }       	
        }
      
    }
    
    public void close() {
    	if(session != null) {
    		session.close();
    	}
    }


    public BundleDescriptor getBundleByID(String bundleID) {
        //We could set the repos within the BundleDescriptorDAO, but that would create a cyclical package dependency
        if (bundleID == null) {
            logger.info("JV-1501-016: BundleID is null.");
            return null;
        }
        if (bundleDescriptorDAO == null) {
            logger.severe("JV-1501-017: No BundleDescriptorDAO found");
            return null;
        }

        BundleDescriptorImpl bundleDescriptor =
                (BundleDescriptorImpl) bundleDescriptorDAO.getBundleDescriptorFor(bundleID);

        if (bundleDescriptor == null) {
            logger.finest("JV-1501-018: BundleDescriptor not found in repo: Bundle ID = " + bundleID);
            return null;
        }

        if (deliverableRepository == null || mimeTypeRepository == null) {
            logger.severe("JV-1501-019: DeliverableRepository or MimeTypeRepository not found. Can not create a valid BundleDescriptor");
            return null;
        }

        bundleDescriptor.setDeliverableRepository(deliverableRepository);
        bundleDescriptor.setMimeTypeRepository(mimeTypeRepository);
        bundleDescriptor.setContentDeliveryUri(contentDeliveryUri);
        session = bundleDescriptor.getSession();
        return bundleDescriptor;
    }

    public Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies)
            throws IOException {
        return getBundlesFor(deviceCapabilities, matchPolicies, false, false);
    }

    public Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies, long eventId) throws IOException {
    	return getBundlesFor(deviceCapabilities, matchPolicies, false, false, eventId);
    }
    
    public Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies,
            boolean allVersions, boolean allVariants, long eventId) throws IOException {
        if (bundleDescriptorDAO == null) throw new IOException("JV-1501-015: No BundleDescriptorDAO found");

        List<BundleDescriptor> descriptors = (eventId == -1) ? bundleDescriptorDAO.getBundleDescriptors() : bundleDescriptorDAO.getBundleDescriptorsByEventId(eventId);

        if (descriptors == null) return new ArrayList<BundleDescriptor>();
        Collections.sort(descriptors, Collections.reverseOrder());//ascending: version or upload time

        Stack<MatchedBundle> matchedBundles = new Stack<MatchedBundle>();
        Stack<BundleDescriptor> bundles = new Stack<BundleDescriptor>();

        for (ListIterator<BundleDescriptor> bundleIterator = descriptors.listIterator(); bundleIterator.hasNext();) {
            BundleDescriptorImpl bd = (BundleDescriptorImpl) bundleIterator.next();
            if(session == null){
            	session = bd.getSession();
            }
            bd.setDeliverableRepository(deliverableRepository);
            bd.setMimeTypeRepository(mimeTypeRepository);
            bd.setContentDeliveryUri(contentDeliveryUri);
            float match = 1f;
            try {//using try-catch here is goofy but is required as part of the spec
                //may cause problems if user sees content that should have been removed
                if (deviceCapabilities != null) match = matchCapabilities(bd, deviceCapabilities, match);
            } catch (Exception e) {
                logger.log(Level.WARNING, "JV-1501-004: Unexpected exception in match: Match = " + match, e);
                return descriptors;
            }
            
            if (match == 0f) {
            	continue;
            }
            try {//spec
                if (matchPolicies != null) match = matchPolicies(bd, matchPolicies, match);
            } catch (Exception e) {
                logger.log(Level.WARNING, "JV-1501-005: Unexpected exception in match: Match = " + match, e);
                return descriptors;
            }   
            if (match == 0f) {
            	continue;
            }

            matchedBundles.push(new MatchedBundle(bd, match));//descending: version or upload time
        }
        
        if (!allVariants) {
            do {
                BundleDescriptor nextBd = nextMatchedVariant(matchedBundles);
                if (nextBd != null) {
                	bundles.push(nextBd);
                }
            } while (matchedBundles.size() != 0);
        } else {
            while (matchedBundles.size() != 0) {
                //ascending: version or upload time
                bundles.push(((MatchedBundle) matchedBundles.pop()).getDescriptor());
            }

        }

        if (!allVersions) {
            Stack<BundleDescriptor> tmpVersionStack = new Stack<BundleDescriptor>();
            do {
                BundleDescriptor nextBd = nextMatchedVersion(bundles);
                if (nextBd != null) tmpVersionStack.push(nextBd);
            } while (bundles.size() != 0);
            bundles = tmpVersionStack;
        }
        if (deviceCapabilities != null) {
            Stack<BundleDescriptor> tmpDeviceStack = new Stack<BundleDescriptor>();

            List<String> deviceIds = deviceCapabilities.getCapability(Constants.HardwarePlatform_DeviceIdentifier);
            if (deviceIds == null) {
            	return Collections.list(bundles.elements());
            }
            String deviceId = (String) deviceIds.get(0);
            //Is device id unique? NO: TODO
            while (bundles.size() != 0) {
                BundleDescriptor bundle = (BundleDescriptor) bundles.pop();
                List<AdapterInfo> infos = provisioningContext.getAdapterInfos(bundle, deviceId);
                if (infos.size() == 0)
                    logger.info("JV-1501-006: Removing bundle: Content Id = " + bundle.getContentID() +
                            ", Device Id = " + deviceId);
                else {
                    logger.info("JV-1501-007: Added Bundle = " + bundle);
                    tmpDeviceStack.push(bundle);
                }
            }
            bundles = tmpDeviceStack;
        }

        return Collections.list(bundles.elements());   	
    }
    
    public Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies,
                                    boolean allVersions, boolean allVariants) throws IOException {
    	return getBundlesFor(deviceCapabilities, matchPolicies, allVersions, allVariants, -1);
    }

    public float matchAttribute(String attributeName,
                                List<String> requirementValues,
                                List<String> capabilityValues) {
        if (requirementValues == null) requirementValues = new ArrayList<String>();
        if (capabilityValues == null) capabilityValues = new ArrayList<String>();

        if (attributeRepository == null) {
            logger.severe("JV-1501-008: No attribute repository found");
            return 1.0f;
        }

        try {
            AttributeMatcher matcher = attributeRepository.getMatcherFor(attributeName);
            if (matcher == null) {
            	return 1.0f;
            }
            else {
            	float match = matcher.match(requirementValues, capabilityValues);
            	return match ;
            }
        } catch (MatcherException e) {
            logger.log(Level.INFO, "JV-1501-009: Matcher Exception", e);
        }
        return 0.0f;
    }

    public void removeParFile(long parFileID) throws IOException {//TODO: Implement
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(parFileID));
        //  parDAO.delete(list);
    }

    /**
     * Adds PAR file. Under the spec, with this method, we are required to either stock the PAR file or fail it.
     * We can not manipulate the PAR file. Since we do not have JNLP support, a adding a PAR file with JNLP will
     * fail. Hence, use the StockingHandler to remove JNLP files until such a time as JNLP support is added.
     *
     * @param inputStream
     * @return parId
     * @throws IOException
     */
	public long addParFile(InputStream inputStream) throws IOException {
        if (stockingHandlerConfig == null)
            throw new IOException("JV-1501-011: StockingHandlerConfig has not been set.");

        if (inputStream == null) {
            logger.info("JV-1501-010: Stocking failed: PAR is null.");
            throw new IOException("JV-1501-010: Stocking failed: PAR is null.");
        }

        Map<String, byte[]> contentMap =
                StockingFactory.createContentMap(inputStream, stockingHandlerConfig.getInitParameter("par-file-output"));
        byte[] content = contentMap.get("META-INF/provisioning.xml");
        if(content == null) {
        	logger.info(contentMap.toString());
            logger.info("JV-1501-010: Stocking failed: provisioning.xml file not found:" + stockingHandlerConfig.getInitParameter("par-file-output"));
            throw new IOException("JV-1501-010a: Stocking failed: provisioning.xml file not found..");       	
        }
        ProvisioningArchiveType archive = StockingFactory.createProvisioningArchive(new ByteArrayInputStream(content));

        //We are using this outside the context of the handler framework, so  filterTaskkeep a close eye on the implementation.
        new FormatFilter().doFilter(StockingFactory.createFilterTask(null, null, null, null, archive));

        List<ClientBundleType> clientBundleTypes = archive.getClientBundle();
        List<ClientBundle> clientBundles = new ArrayList<ClientBundle>();
        long parId = UUID.randomUUID().hashCode();
        logger.info("JV-000-xxx: Number of bundles = " + clientBundleTypes);
        for (ClientBundleType clientBundleType : clientBundleTypes) {
            ClientBundle clientBundle;
            
            try {
                clientBundle = ClientBundleTranslator.translate(clientBundleType, contentMap,
                        stockingHandlerConfig, mimeTypeRepository);
            } catch (ProvisioningException e) {
                logger.info("JV-1501-020: Can not process ClientBundle: Message = " + e.getMessage());
                throw new IOException("JV-1501-020: Can not process ClientBundle: Message = " + e.getMessage());
            }
            
            clientBundle.setParId(parId);
            String bundleId = UUID.randomUUID().toString();
            String check = new String(bundleId);
            if(clientBundleType.getCatalogProperty() != null) {
            	for(CatalogProperty cp : clientBundleType.getCatalogProperty()) {
            		if("JVending.Internal.BundleId".equals(cp.getPropertyName())) {
            			bundleId = cp.getPropertyValue();
            			break;
            		}
            	}
            }
                 
            clientBundle.setBundleId(bundleId);
            clientBundles.add(clientBundle);
        }

        //Store in datasource
        if (clientBundleDAO == null) {//this is a DataSink request so let's set it now
            HibernateDAORegistry hibernateDAORegistry = (HibernateDAORegistry) stockingHandlerConfig.getStockingContext()
                    .getServletContext().getAttribute("org.jvending.registry.hibernate.HibernateDAORegistry");
            clientBundleDAO = (ClientBundleDAO) hibernateDAORegistry.find("dao:client-bundle");
        }

        clientBundleDAO.store(clientBundles);

        return parId;
    }

    public void emptyRepository() throws IOException {//TODO: Add this method back
        //parDAO.deleteAll();
    }


    public void init(StockingHandlerConfig config) {
        this.stockingHandlerConfig = config;
    }

    public String getDataSinkName() {
        return "org.jvending.provisioning.impl.BundleRepositoryImpl";
    }

    private float matchCapabilities(BundleDescriptor bundleDescriptor,
                                    Capabilities deviceCapabilities, float initMatchValue) throws Exception {
        float match = initMatchValue;
        for (String requirementName : bundleDescriptor.getRequirementNames() ) {
            List<String> deviceRequirements = bundleDescriptor.getRequirement(requirementName);
            List<String> deviceCapabilitiesList = (deviceCapabilities != null) ?
                    deviceCapabilities.getCapability(requirementName) :
                    new ArrayList<String>();
            match = match * matchAttribute(requirementName, deviceRequirements,
                    deviceCapabilitiesList);
            if (match == 0f) break;
        }
        return match;
    }

    private float matchPolicies(BundleDescriptor bundleDescriptor,
                                List<MatchPolicy> matchPolicies, float initMatchValue) throws Exception {
        float match = initMatchValue;
        for (MatchPolicy matchPolicy : matchPolicies) {
            match = match * (matchPolicy).doMatch(bundleDescriptor);
            if (match == 0f) break;
        }
        return match;
    }

    private BundleDescriptor nextMatchedVersion(Stack<BundleDescriptor> bundles) {
        //descending
        if (bundles.size() == 0) return null;
        if (bundles.size() == 1) return (BundleDescriptor) bundles.pop();

        BundleDescriptor firstBundle = (BundleDescriptor) bundles.pop();
        BundleDescriptor secondBundle = (BundleDescriptor) bundles.pop();
/*
        logger.finest("-1st: " + 
                      "Par Id = " + firstBundle.getParFileID() +
                      ", Bundle Id = " + firstBundle.getBundleID() + 
                      ", Content Id =" + firstBundle.getContentID() +
                      ", Version = " +  firstBundle.getVersion() +
                      ", Upload Time = " +  firstBundle.getUploadTime() + 
                      ":" + " -2nd:" + 
                      "Par Id2 = " + secondBundle.getParFileID() +
                      ", Bundle Id2 = " + secondBundle.getBundleID() + 
                      ", Content Id2 =" + secondBundle.getContentID() +
                      ", Version2 = " +  secondBundle.getVersion() +
                      ", Upload Time2 = " +  secondBundle.getUploadTime());
*/
        if (firstBundle.getContentID().equals(secondBundle.getContentID())) {
            String firstVersion = firstBundle.getVersion();
            String secondVersion = secondBundle.getVersion();
            if (firstVersion == null && secondVersion == null) {
                if (firstBundle.getUploadTime() > secondBundle.getUploadTime()) {
                    bundles.push(firstBundle);
                    return nextMatchedVersion(bundles);
                } else {
                    bundles.push(secondBundle);
                    return nextMatchedVersion(bundles);
                }
            } else if (firstVersion == null && secondVersion != null) {
                bundles.push(secondBundle);
                return nextMatchedVersion(bundles);
            } else if (firstVersion != null && secondVersion == null) {
                bundles.push(firstBundle);
                return nextMatchedVersion(bundles);
            } else if (firstVersion.equals(secondVersion)) {
                bundles.push(secondBundle);
                return firstBundle;
            } else {//decending order: take 1st bundle
                bundles.push(firstBundle);
                return nextMatchedVersion(bundles);
            }
        } else {
            bundles.push(secondBundle);
            return firstBundle;
        }
    }

    private BundleDescriptor nextMatchedVariant(Stack<MatchedBundle> matchedValues) {
        int matchSize = matchedValues.size();
        if (matchSize == 0) return null;
        MatchedBundle firstBundle = (MatchedBundle) matchedValues.pop();
        if (matchSize == 1) return firstBundle.getDescriptor();
        MatchedBundle secondBundle = (MatchedBundle) matchedValues.pop();

        BundleDescriptor firstDescriptor = firstBundle.getDescriptor();
        BundleDescriptor secondDescriptor = secondBundle.getDescriptor();

        if (firstDescriptor.equals(secondDescriptor)) {
            float firstMatch = firstBundle.getMatchValue();
            float secondMatch = secondBundle.getMatchValue();

            if (firstMatch > secondMatch) {
                matchedValues.push(firstBundle);
                return nextMatchedVariant(matchedValues);
            } else if (firstMatch < secondMatch) {
                matchedValues.push(secondBundle);
                return nextMatchedVariant(matchedValues);
            } else {
                if (firstDescriptor.getUploadTime() > secondDescriptor.getUploadTime()) {
                    matchedValues.push(firstBundle);
                    return nextMatchedVariant(matchedValues);
                } else {
                    matchedValues.push(secondBundle);
                    return nextMatchedVariant(matchedValues);
                }
            }
        } else {//bundles not equal
            if (firstDescriptor.getContentID().equals(secondDescriptor.getContentID())) {
                String firstVersion = firstDescriptor.getVersion();
                String secondVersion = secondDescriptor.getVersion();
                if (firstVersion == null && secondVersion == null) {
                    if (firstDescriptor.getUploadTime() > secondDescriptor.getUploadTime()) {
                        matchedValues.push(firstBundle);
                        return nextMatchedVariant(matchedValues);
                    } else {
                        matchedValues.push(secondBundle);
                        return nextMatchedVariant(matchedValues);
                    }
                } else if (firstVersion == null && secondVersion != null) {
                    matchedValues.push(secondBundle);
                    return nextMatchedVariant(matchedValues);
                } else if (firstVersion != null && secondVersion == null) {
                    matchedValues.push(firstBundle);
                    return nextMatchedVariant(matchedValues);
                } else if (firstVersion.equals(secondVersion)) {
                    if (firstDescriptor.getUploadTime() > secondDescriptor.getUploadTime()) {
                        matchedValues.push(firstBundle);
                        return nextMatchedVariant(matchedValues);
                    } else {
                        matchedValues.push(secondBundle);
                        return nextMatchedVariant(matchedValues);
                    }
                } else {
                    matchedValues.push(secondBundle);
                    return firstDescriptor;
                }
            } else {
                matchedValues.push(secondBundle);
                return firstDescriptor;
            }
        }
    }

    private static class MatchedBundle {

        private final float matchValue;

        private final BundleDescriptor descriptor;

        MatchedBundle(BundleDescriptor descriptor, float matchValue) {
            this.matchValue = matchValue;
            this.descriptor = descriptor;
        }

        float getMatchValue() {
            return matchValue;
        }

        BundleDescriptor getDescriptor() {
            return descriptor;
        }

        public String toString() {
            return "Par Id = " + descriptor.getParFileID() +
                    ", Bundle Id = " + descriptor.getBundleID() +
                    ", Content Id = " + descriptor.getContentID() +
                    ", Version = " + descriptor.getVersion() +
                    ", Upload Time = " + descriptor.getUploadTime() +
                    ", Match Value = " + matchValue +
                    "\r\n";
        }
    }
}