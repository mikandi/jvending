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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.AdapterInfo;
import javax.provisioning.BundleDescriptor;
import javax.provisioning.BundleRepository;
import javax.provisioning.Capabilities;
import javax.provisioning.Constants;
import javax.provisioning.Deliverable;
import javax.provisioning.DeliveryComponent;
import javax.provisioning.DeliveryContext;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.ProvisioningContext;
import javax.provisioning.ProvisioningException;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterConfig;
import javax.provisioning.adapter.AdapterContext;
import javax.provisioning.adapter.AdapterException;
import javax.provisioning.adapter.InvalidFulfillmentIDException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jvending.provisioning.config.AdapterRepository;
import org.jvending.provisioning.config.AttributeMatcherRepository;
import org.jvending.provisioning.config.DeliverableRepository;
import org.jvending.provisioning.config.DeviceRepository;
import org.jvending.provisioning.config.JVendingRepository;
import org.jvending.provisioning.config.MimeTypeRepository;
import org.jvending.provisioning.dao.BundleDescriptorDAO;
import org.jvending.provisioning.dao.ClientBundleDAO;
import org.jvending.provisioning.dao.DeliveryEventDAO;
import org.jvending.provisioning.dao.FulfillmentTaskDAO;
import org.jvending.provisioning.model.fulfillmenttask.FulfillmentTaskObject;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.ServletRepositoryLoader;
import org.jvending.registry.hibernate.HibernateDAORegistry;

/**
 * Provides an implementation of the <code>ProvisioningContext</code> and the <code>AdapterContext</code>.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

//TODO: Optimize
public final class ProvisioningContextImpl implements ProvisioningContext, AdapterContext, ServletContextListener {

    private static Logger logger = Logger.getLogger("ProvisioningContextImpl");//Last log: 44

    private ServletContext servletContext;

    protected DeviceRepository deviceRepository;

    protected AdapterRepositoryWrapper adapterRepositoryWrapper;

    protected RepositoryRegistry repositoryRegistry;

    protected HibernateDAORegistry hibernateDaoRegistry;

    protected DeliveryComponent deliveryComponent;

    private JVendingRepository jvendingRepository;

    /**
     * Constructor for the servlet container
     */
    public ProvisioningContextImpl() {
    }

    /**
     * Constructor for unit tests
     *
     * @param deviceRepository
     * @param adapterRepositoryWrapper
     * @param repositoryRegistry
     * @param hibernateDaoRegistry
     * @param jvendingRepository
     */
    public ProvisioningContextImpl(DeviceRepository deviceRepository,
                                   AdapterRepositoryWrapper adapterRepositoryWrapper,
                                   RepositoryRegistry repositoryRegistry,
                                   HibernateDAORegistry hibernateDaoRegistry,
                                   JVendingRepository jvendingRepository) {
        this.deviceRepository = deviceRepository;
        this.adapterRepositoryWrapper = adapterRepositoryWrapper;
        this.repositoryRegistry = repositoryRegistry;
        this.hibernateDaoRegistry = hibernateDaoRegistry;
        this.jvendingRepository = jvendingRepository;
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("JV-1500-037: Starting the Provisioning Server Context");
        servletContext = servletContextEvent.getServletContext();
        String provisioningConfig = servletContext.getInitParameter("PROVISIONING_CONFIG");
        if (provisioningConfig == null) {
            logger.severe("JV-1500-001: Could not find the PROVISIONING_CONFIG init parameter from the servlet context: Value = "
                    + provisioningConfig);
            return;
        }
        try {
            InputStream inputStream = servletContext.getResourceAsStream(provisioningConfig);
            if (inputStream == null) {
                logger.severe("JV-1500-002: Could not find PROVISIONING_CONFIG from resource: Value = "
                        + provisioningConfig);
                return;
            }
            logger.info("JV-1500-003: Loaded PROVISIONING_CONFIG = " + provisioningConfig);
            repositoryRegistry = RepositoryRegistry.Factory.create();
            hibernateDaoRegistry = HibernateDAORegistry.Factory.create();
            repositoryRegistry.setServletContext(this.getServletContext());
            repositoryRegistry.setRepositoryLoader(new ServletRepositoryLoader(servletContext));
            repositoryRegistry.loadFromInputStream(inputStream);
           
            logger.info("JV-1500-003a:Loaded repository registry");
            
            hibernateDaoRegistry.setRepositoryRegistry(repositoryRegistry);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("JV-1500-004: Failed to load registries");
            return;
        }
        logger.info("JV-1500-004a: Loaded registries");
        
        deviceRepository = (DeviceRepository) repositoryRegistry.find("devices");
        AdapterRepository adapterRepository = (AdapterRepository) repositoryRegistry.find("adapters");
        jvendingRepository = (JVendingRepository) repositoryRegistry.find("jvending");
        if (deviceRepository == null) {
            logger.severe("JV-1500-005: Device repository is null within provisioning context!");
        }
        logger.info("JV-1500-005a: Device Repository loaded");
        if (adapterRepository == null) {
            logger.severe("JV-1500-006: Adapter repository is null within provisioning context!");
        }
        logger.info("JV-1500-006a: Adpater repository loaded.");
        
        adapterRepositoryWrapper = new AdapterRepositoryWrapper(adapterRepository, this);
        DeliveryEventDAO deliveryEventDAO = (DeliveryEventDAO) hibernateDaoRegistry.find("dao:delivery-event");

        try {
            deliveryComponent = new DeliveryComponentImpl(deliveryEventDAO);
            deliveryComponent.init(this);
        } catch (ServletException e) {
            e.printStackTrace();
            logger.severe("JV-1500-43: Could not initialize the DeliveryComponent");
            throw new Error("JV-1500-43: Could not initialize the DeliveryComponent");
        }
        logger.info("JV-1500-44a: Initialized the DeliveryComponent");
 
        servletContext.setAttribute("javax.provisioning.ProvisioningContext", this);
        servletContext.setAttribute("org.jvending.registry.RepositoryRegistry", repositoryRegistry);
        servletContext.setAttribute("org.jvending.registry.hibernate.HibernateDAORegistry", hibernateDaoRegistry);
      //  servletContext.setAttribute("org.jets3t.service.S3Service", service);
        
        logger.info("JV-1500-038: Sucessfully started the Provisioning Server Context");
    }


    public void contextDestroyed(ServletContextEvent event) {
        logger.info("JV-1500-039: Destroying the Provisioning Server Context");

        //Destroy the Adapters:
        List<AdapterInfo> adapterInfos = getAdapterInfos();
        if(adapterInfos != null)
        {
            for (AdapterInfo adapterInfo : adapterInfos) {
                String adapterName = adapterInfo.getAdapterName();
                Adapter adapter = getAdapter(adapterName);
                if (adapter != null) {
                    try {
                        adapter.destroy();
                        logger.info("JV-1500-41: Destroyed adapter: Name = " + adapterName);
                    } catch (AdapterException e) {
                        e.printStackTrace();
                        logger.info("JV-1500-40: Unable to cleanup resources from an adapter: Name = " + adapterName);
                    }
                }
            }        	
        }
        
        if(deliveryComponent != null) deliveryComponent.destroy();
        logger.info("JV-1500-42: Destroyed delivery component");

        servletContext.removeAttribute("javax.provisioning.ProvisioningContext");
        servletContext.removeAttribute("org.jvending.registry.RepositoryRegistry");
        servletContext.removeAttribute("org.jvending.registry.hibernate.HibernateDAORegistry");
    }

    public FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
                                                 DeliveryContext deliveryContext) throws ProvisioningException {
        if (bundleDescriptor == null) throw new ProvisioningException("JV-1500-007: Bundle Descriptor is null.");

        return createFulfillmentTask(bundleDescriptor,
                deliveryContext,
                getDefaultAdapterName(bundleDescriptor, deliveryContext));
    }

    public FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
                                                 DeliveryContext deliveryContext, String adapterName)
            throws ProvisioningException {
        if (bundleDescriptor == null)
            throw new ProvisioningException("JV-1500-008: Bundle Descriptor is null: AdapterName = " + adapterName);

        if (adapterName == null) adapterName = getDefaultAdapterName(bundleDescriptor, deliveryContext);
        long duration = Long.parseLong(this.getServletContext().getInitParameter("expiryDuration"));
        return createFulfillmentTask(bundleDescriptor, deliveryContext, adapterName, duration);

    }

	public FulfillmentTask createFulfillmentTask(
			BundleDescriptor bundleDescriptor, DeliveryContext deliveryContext,
			String adapterName, long duration, String fid) throws ProvisioningException {
		if (bundleDescriptor == null)
			throw new ProvisioningException(
					"JV-1500-009: Bundle Descriptor is null. AdapterName = "
							+ adapterName + ", Duration = " + duration);

		if (adapterName == null)
			adapterName = getDefaultAdapterName(bundleDescriptor,
					deliveryContext);
		if (adapterRepositoryWrapper.getAdapterFor(adapterName) == null) {
			logger.warning("JV-1500-010: Could not find adapter: Name = "
					+ adapterName);
			throw new ProvisioningException(
					"JV-1500-010: Could not find adapter: Name = "
							+ adapterName);
		}

		FulfillmentTask task = ProvisioningFactory.createFulfillmentTask(
				bundleDescriptor, deliveryContext, adapterName, duration, this, fid);

		
			final FulfillmentTaskObject taskObject = new FulfillmentTaskObject();
			taskObject.setFulfillmentID(task.getFulfillmentID());
			taskObject.setExpiryTime(task.getExpiryTime());
			taskObject.setBundleDescriptorId(task.getBundleDescriptor()
					.getBundleID());
			final FulfillmentTaskDAO fulfillmentTaskDAO = (FulfillmentTaskDAO) hibernateDaoRegistry
					.find("dao:fulfillment-task");
			if (fulfillmentTaskDAO == null)
				throw new ProvisioningException(
						"JV-1500-011: Could not find DAO: DAO = dao:fulfillment-task"
								+ ", AdapterName = " + adapterName);
            Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						fulfillmentTaskDAO.store(taskObject);
					} catch (IOException e) {
						try {
							fulfillmentTaskDAO.store(taskObject);
						} catch (IOException e1) {
							e1.printStackTrace();
							logger.log(Level.INFO, "JV-1500-011: Could not create fulfillment task: ", e1);
						}
			            
			         //   throw new ProvisioningException("JV-1500-012: Could not create fulfillment task: AdapterName = "
			          //          + adapterName);
			        }
				}
            });
            t.start();

		return task;
	}
    
    public FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
                                                 DeliveryContext deliveryContext, String adapterName, long duration)
            throws ProvisioningException {
        if (bundleDescriptor == null)
            throw new ProvisioningException("JV-1500-009: Bundle Descriptor is null. AdapterName = "
                    + adapterName + ", Duration = " + duration);

        if (adapterName == null) adapterName = getDefaultAdapterName(bundleDescriptor, deliveryContext);
        if (adapterRepositoryWrapper.getAdapterFor(adapterName) == null) {
            logger.warning("JV-1500-010: Could not find adapter: Name = " + adapterName);
            throw new ProvisioningException("JV-1500-010: Could not find adapter: Name = " + adapterName);
        }

        FulfillmentTask task =
                ProvisioningFactory.createFulfillmentTask(bundleDescriptor, deliveryContext, adapterName, duration, this);

   
            final FulfillmentTaskObject taskObject =
                    new FulfillmentTaskObject();
            taskObject.setFulfillmentID(task.getFulfillmentID());
            taskObject.setExpiryTime(task.getExpiryTime());
            taskObject.setBundleDescriptorId(task.getBundleDescriptor().getBundleID());
            final FulfillmentTaskDAO fulfillmentTaskDAO =
                    (FulfillmentTaskDAO) hibernateDaoRegistry.find("dao:fulfillment-task");
            if (fulfillmentTaskDAO == null)
                throw new ProvisioningException("JV-1500-011: Could not find DAO: DAO = dao:fulfillment-task"
                        + ", AdapterName = " + adapterName);
            //TODO: Put these in a queue - time locks
            /**
             * Don't hold up a free download here, worst case it's not recorded and download/install notify fail 
             */
            Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						fulfillmentTaskDAO.store(taskObject);
					} catch (IOException e) {
						try {
							fulfillmentTaskDAO.store(taskObject);
						} catch (IOException e1) {
							e1.printStackTrace();
							logger.log(Level.INFO, "JV-1500-011: Could not create fulfillment task: ", e1);
						}
			            
			         //   throw new ProvisioningException("JV-1500-012: Could not create fulfillment task: AdapterName = "
			          //          + adapterName);
			        }
				}
            });
            t.start();
        return task;
    }

    //TODO: Does returning unmodifiable list violate spec? Spec - Make sure that the order returned is the same as in
    //the config file
    public List<AdapterInfo> getAdapterInfos() {
    	if(adapterRepositoryWrapper == null)
    		return Collections.unmodifiableList(new ArrayList<AdapterInfo>());
        List<AdapterInfo> adapterInfos = adapterRepositoryWrapper.getAdapterInfos();
        return (adapterInfos != null) ? adapterInfos : Collections.unmodifiableList(new ArrayList<AdapterInfo>());
    }

    public List<AdapterInfo> getAdapterInfos(BundleDescriptor bundleDescriptor, String deviceId) {
        List<String> adapterNames = null;
        if (bundleDescriptor == null && deviceId == null)
            return adapterRepositoryWrapper.getAdapterInfos();//All AdapterInfos

        if (deviceId != null)
            adapterNames = deviceRepository.getAdapterNamesFor(deviceId);//AdapterNames filtered by device
        else if (bundleDescriptor != null)
            adapterNames = adapterRepositoryWrapper.getAdapterNames();//All AdapterNames

        List<AdapterInfo> adapterInfos = new ArrayList<AdapterInfo>();

        logger.finest("JV-1500-013: Found Adapters: Count = " + adapterNames.size() + ", Device Id = " + deviceId);

        for (String adapterName : adapterNames) {
            AdapterInfo adapterInfo = adapterRepositoryWrapper.getAdapterInfoFor(adapterName);

            if (bundleDescriptor != null &&
                    bundleDescriptor.getDescriptorFile() != null &&
                    matchDescriptorFile(adapterInfo, bundleDescriptor)) {

                logger.finest("JV-1500-014: Added adapter for descriptor to list: Name = " + adapterName);
                adapterInfos.add(adapterInfo);
            } else if (bundleDescriptor != null &&
                    bundleDescriptor.getContentFile() != null &&
                    adapterInfo.getDescriptorFileExtension() == null &&
                    adapterInfo.getDescriptorFileMimeType() == null) {

                logger.finest("JV-1500-015: Added adapter for content to list: Name = " + adapterName);
                adapterInfos.add(adapterInfo);
            } else if (bundleDescriptor == null) {
                logger.finest("JV-1500-016: Added adapter to list: Name = " + adapterName);
                adapterInfos.add(adapterInfo);
            }
        }
        return adapterInfos;
    }

    public BundleRepository getBundleRepository() {
        ClientBundleDAO clientBundleDAO = (ClientBundleDAO) hibernateDaoRegistry.find("dao:client-bundle");
        BundleDescriptorDAO bundleDescriptorDAO =
                (BundleDescriptorDAO) hibernateDaoRegistry.find("dao:bundle-descriptor");
        AttributeMatcherRepository matcherRepository = (AttributeMatcherRepository) repositoryRegistry.find("matchers");
        MimeTypeRepository mimeTypeRepository = (MimeTypeRepository) repositoryRegistry.find("mimetype");
        DeliverableRepository deliverableRepository = (DeliverableRepository) repositoryRegistry.find("deliverables");
        return new BundleRepositoryImpl(clientBundleDAO, bundleDescriptorDAO,
                matcherRepository, mimeTypeRepository, deliverableRepository, this);


    }

    public DeliveryContext getDeliveryContext(HttpServletRequest request) {//Spec does not define passing in null request
        if(request == null)
        {
            throw new IllegalArgumentException("request");
        }
        return getDeliveryContext(toMap(request));
    }

    private DeliveryContext getDeliveryContext(Map<String, String> requestMap) {
        if (requestMap == null) {
            logger.info("JV-1500-017: Could not get HTTP parameters from this device. Failing...");
            return null;
        }
        String networkHeader = jvendingRepository.getValue("network-header");
        String userHeader = jvendingRepository.getValue("user-header");
        String networkId = networkHeader != null ? (String) requestMap.get(networkHeader) : null;
        String user = userHeader != null ? (String) requestMap.get(userHeader) : null;

        //TODO: Device repository does not follow spec
        Capabilities capabilities = deviceRepository.getCapabilities(requestMap);
        if (capabilities == null) {
            logger.fine("JV-1500-018: Could not get capabilities for this device. Failing...");
            logger.fine(requestMap.toString());
            return null;
        }
        return ProvisioningFactory.createDeliveryContext(capabilities, networkId, user, requestMap);
    }

    public Capabilities getDeviceTypeCapabilities(String identifier) {
        return (identifier == null) ? null : deviceRepository.getCapabilitiesFor(identifier.trim());
    }

    public Set<String> getDeviceTypeIdentifiers() {
        return deviceRepository.getDeviceIds();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String encodeURL(HttpServletRequest request, HttpServletResponse response, String url) {
        return response.encodeURL(url);
    }

    //TODO: Must only be one instance and the init and destroy methods must be called.
    // returns null for invalid adapter Name
    public Adapter getAdapter(String adapterName) {
        return adapterRepositoryWrapper.getAdapterFor(adapterName);
    }

    public AdapterConfig getAdapterConfig(String adapterName) {
        return adapterRepositoryWrapper.getAdapterConfigFor(adapterName);
    }

    public DeliveryComponent getDeliveryComponent() {
        return deliveryComponent;
    }

    public FulfillmentTask getFulfillmentTask(String fulfillmentID,
                                              DeliveryContext deliveryContext,
                                              String adapterName)
            throws AdapterException, InvalidFulfillmentIDException {
        FulfillmentTaskDAO fulfillmentTaskDAO = (FulfillmentTaskDAO) hibernateDaoRegistry.find("dao:fulfillment-task");
        if (fulfillmentTaskDAO == null)
            throw new AdapterException(adapterName, "JV-1500-019: Could not find DAO: DAO = dao:fulfillment-task, AdapterName ="
                    + adapterName);
        if (fulfillmentID == null) throw new InvalidFulfillmentIDException(adapterName,
                "Cannot get a FulfillmentTask with a null id", null, "null");
        
        
        FulfillmentTask fulfillmentTask = (FulfillmentTask) getServletContext().getAttribute(fulfillmentID);

        if (fulfillmentTask == null) {
            FulfillmentTaskObject fulfillmentTaskObject = null;
            try {
                fulfillmentTaskObject =
                        fulfillmentTaskDAO.getFulfillmentTaskFor(fulfillmentID);
            } catch (IOException e) {
                throw new InvalidFulfillmentIDException(adapterName, "JV-1500-020: Unable to find task: FulfillmentID = "
                        + fulfillmentID + ", " + e.getMessage(), null, fulfillmentID);
            } 
            
            BundleDescriptor descriptor =
                    getBundleRepository().getBundleByID(fulfillmentTaskObject.getBundleDescriptorId());

            try {//TODO - this changes the fid - DURATION
                fulfillmentTask = createFulfillmentTask(descriptor,
                        deliveryContext,
                        adapterName,
                        (fulfillmentTaskObject.getExpiryTime() - System.currentTimeMillis()), fulfillmentID);
            } catch (ProvisioningException e) {
                logger.log(Level.INFO, "JV-1500-021: Could not create fulfillment task:" +
                        " Adapter Name = " + adapterName +
                        ", Fulfillment Id = " + fulfillmentID +
                        ", Bundle Descriptor = " + descriptor);
                throw new AdapterException(adapterName, "JV-1500-021: Could not create task. Fulfillment ID = "
                        + fulfillmentID, e);
            }
            getServletContext().setAttribute(fulfillmentTask.getFulfillmentID(), fulfillmentTask);
        }

  //      if (fulfillmentTask.isExpired())
 //           throw new InvalidFulfillmentIDException(adapterName, "JV-1500-022: Expired task.", null, fulfillmentID);

        return fulfillmentTask;
    }

    /*
    public FulfillmentTask getFulfillmentTaskWithDeviceId(String fulfillmentID,
                                                          DeliveryContext deliveryContext,
                                                          String deviceId)
            throws AdapterException, InvalidFulfillmentIDException {
        FulfillmentTaskDAO fulfillmentTaskDAO = (FulfillmentTaskDAO) hibernateDaoRegistry.find("dao:fulfillment-task");
        if(fulfillmentTaskDAO == null)
            throw new AdapterException("unknown", "JV-1500-023: Could not find DAO: DAO = dao:fulfillment-task, DeviceId ="
                + deviceId);

        FulfillmentTask fulfillmentTask = (FulfillmentTask) getServletContext().getAttribute(fulfillmentID);
        if (fulfillmentTask != null) {
            if (fulfillmentTask.isExpired())
                throw new InvalidFulfillmentIDException(null, "JV-1500-024: Expired task", null, fulfillmentID);
            return fulfillmentTask;
        }

        FulfillmentTaskObject fulfillmentTaskObject =
                fulfillmentTaskDAO.getFulfillmentTaskFor(fulfillmentID);
        if (fulfillmentTaskObject == null)
            throw new InvalidFulfillmentIDException(null, "JV-1500-025: Unable to find task", null, fulfillmentID);

        BundleDescriptor descriptor =
                getBundleRepository().getBundleByID(fulfillmentTaskObject.getBundleDescriptorId());

        if (descriptor == null) {
            logger.info("JV-1500-026: Could not find bundle descriptor: ID = "
                    + fulfillmentTaskObject.getBundleDescriptorId());
            return null;//?????
        }

        List adapterInfos = getAdapterInfos(descriptor, deviceId);
        if (adapterInfos.size() == 0) {
            logger.info("JV-1500-027: Could not find adapter info: Device Id = " + deviceId);
            throw new AdapterException(null, "JV-1500-027: Could not find adapter info: Fulfillment ID = "
                    + fulfillmentID, null);
        }

        String adapterName = ((AdapterInfo) adapterInfos.get(0)).getAdapterName();

        try {
            fulfillmentTask = createFulfillmentTask(descriptor,
                    deliveryContext,
                    adapterName,
                    fulfillmentTaskObject.getExpiryTime());
        } catch (ProvisioningException e) {
            logger.log(Level.INFO, "JV-1500-028: Could not create fulfillment task:" +
                    " Adapter Name = " + adapterName +
                    ", Fulfillment Id = " + fulfillmentID +
                    ", Bundle Descriptor = " + descriptor);
            throw new AdapterException(adapterName, "JV-1500-028: Could not create task: Fulfillment ID = "
                    + fulfillmentID, e);
        }

        if (fulfillmentTask != null) {
            if (fulfillmentTask.isExpired())
                throw new InvalidFulfillmentIDException(null, "JV-1500-029: Expired task", null, fulfillmentID);
            return fulfillmentTask;
        }
        throw new InvalidFulfillmentIDException(null, "JV-1500-030: Invalid ID", null, fulfillmentID);
    }
*/
    private Map<String, String> toMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<String, String>();
        if (request == null) return paramMap;
        for (Enumeration<?> en = request.getHeaderNames(); en.hasMoreElements();) {
            String headerName = (String) en.nextElement();
            String headerValue = request.getHeader(headerName);
            paramMap.put(headerName, headerValue);
        }
        return paramMap;
    }

    /**
     *
     *
     */
    private String getDefaultAdapterName(BundleDescriptor descriptor, DeliveryContext context)
            throws ProvisioningException {
        if (descriptor == null) {
            logger.info("JV-1500-031: Bundle Descriptor is null. Cannot obtain adapter name. Try Generic Adapter.");
            return "generic";
        }
        HashSet<AdapterInfo> adapterInfoSet = new HashSet<AdapterInfo>();

        if (context != null) {
            Capabilities capabilities = context.getDeviceCapabilities();
            List<String> capability = capabilities.getCapability(Constants.HardwarePlatform_DeviceIdentifier);

            if (capability == null) {
                List<AdapterInfo> adapterInfos = getAdapterInfos(descriptor, null);
                for (AdapterInfo info : adapterInfos)
                    adapterInfoSet.add(info);
            } else {
                for (String deviceId : capability ) {
                    List<AdapterInfo> adapterInfos = getAdapterInfos(descriptor, deviceId);
                    for (AdapterInfo info : adapterInfos)
                        adapterInfoSet.add(info);
                }
            }
        } else {
            List<AdapterInfo> adapterInfos = getAdapterInfos(descriptor, null);
	            for (AdapterInfo info : adapterInfos)
	                adapterInfoSet.add(info);
        }

        Deliverable descriptorFile = descriptor.getDescriptorFile();
        Deliverable contentFile = descriptor.getContentFile();

        //sanity check
        if (descriptorFile != null && contentFile != null) {
            logger.warning("JV-1500-032: Illegal state. Bundle Descriptor has both a descriptor file and content file: Descriptor = " +
                    descriptor);
            throw new ProvisioningException("JV-1500-032: Bundle descriptor contains an illegal state.");
        }

        if (descriptorFile == null && contentFile == null) {
            logger.warning("JV-1500-033: Illegal state. Bundle Descriptor has neither a descriptor file nor a content file: Descriptor = " +
                    descriptor);
            throw new ProvisioningException("JV-1500-033: Bundle descriptor contains an illegal state.");
        }

        if (descriptorFile != null) {//note: the BundleDescriptor already converts file-extension to mime-type
            String mimeType = descriptorFile.getMimeType();
            for (AdapterInfo adapterInfo : adapterInfoSet ) {
                String mimeType2 = adapterInfo.getDescriptorFileMimeType();
                if (mimeType.equals(mimeType2)) {
                    return adapterInfo.getAdapterName();
                }
            }
        } else if (contentFile != null) {
            logger.finest("GENERIC ADAPTER");
            return "generic";
        }
        throw new ProvisioningException("JV-1500-034: Adapter not found.");

    }

    private boolean matchDescriptorFile(AdapterInfo adapterInfo, BundleDescriptor bundleDescriptor) {
        if (bundleDescriptor == null || adapterInfo == null) {
            logger.finest("JV-1500-035: Unable to match adapter info. Either bundle descriptor or adapter info is null.");
            return false;
        }

        BundleDescriptorImpl bundleDescriptorImpl = (BundleDescriptorImpl) bundleDescriptor;

        String adapterFileExtension = adapterInfo.getDescriptorFileExtension();
        String adapterMimeType = adapterInfo.getDescriptorFileMimeType();

        if (adapterMimeType == null && adapterFileExtension == null) {
            logger.finest("JV-1500-036: Unable to match adapter info. Adapter mime-type and file extension are null.");
            return false;
        }

        String bundleFileName = bundleDescriptorImpl.getDescriptorFileName();
        String bundleMimeType = bundleDescriptorImpl.getDescriptorMimeType();

        logger.finest(bundleMimeType + ":" + adapterMimeType + ":" + bundleFileName + ":" + adapterFileExtension);
        return ((bundleMimeType != null && bundleMimeType.equals(adapterMimeType)) ||
                (bundleFileName != null && bundleFileName.endsWith(adapterFileExtension)));
    }
}