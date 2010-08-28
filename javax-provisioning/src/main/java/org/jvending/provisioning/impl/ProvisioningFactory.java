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

/**
 * Provides factory methods for creating objects needed by the provisioning framework.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.Capabilities;
import javax.provisioning.DeliveryContext;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.ProvisioningContext;
import javax.provisioning.ProvisioningException;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterContext;
import javax.provisioning.adapter.AdapterException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jvending.provisioning.deliverable.BaseDeliverable;
import org.jvending.provisioning.deliverable.BaseDescriptorFile;
import org.jvending.provisioning.model.event.AndroidDeviceInfo;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

public final class ProvisioningFactory {

    private static Logger logger = Logger.getLogger("ProvisioningFactory");

    private ProvisioningFactory() {
    }
  
    public static void populateDeviceInfo(AndroidDeviceInfo info, HttpServletRequest request) {
    	populateDeviceInfo(info, toMap(request));
    }
    
    public static void populateDeviceInfo(AndroidDeviceInfo info, Map<String, String> requestMap) {
        info.setDeviceId(requestMap.get("x-deviceid"));
        info.setNetworkCountryIso(requestMap.get("x-networkcountryiso"));
        info.setDeviceSoftwareVersion(requestMap.get("x-softwareversion"));
        info.setNetworkOperator(requestMap.get("x-networkoperator"));
        info.setNetworkOperatorName(requestMap.get("x-networkoperatorname"));
        info.setNetworkRoaming( "true".equals(requestMap.get("x-isnetworkroaming")));
        info.setNetworkType(requestMap.get("x-networktype"));
        info.setPhoneType(requestMap.get("x-phoneytype"));
        info.setSimCountryIso(requestMap.get("x-simcountryiso"));
        info.setSimOperator(requestMap.get("x-simoperator"));
        info.setSimSerialNumber(requestMap.get("x-simserialnumber"));
        info.setSubscriberId(requestMap.get("x-subscriberid"));
        
        info.setBrand(requestMap.get("x-build-brand"));
        info.setBuildId(requestMap.get("x-build-id"));
        info.setHost(requestMap.get("x-build-host"));
        info.setModel(requestMap.get("x-build-model"));
        info.setProduct(requestMap.get("x-build-product"));
        info.setDevice(requestMap.get("x-build-device"));
        info.setDisplay(requestMap.get("x-build-display"));
        info.setSdkVersion(requestMap.get("x-sdk-version"));
        info.setClientVersion(requestMap.get("x-client-version"));
    }
    
    static FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
            DeliveryContext deliveryContext, String adapterName, long duration,
            ProvisioningContext provisioningContext, String fid) {
    	return new FulfillmentTaskImpl(bundleDescriptor, deliveryContext, adapterName, duration, provisioningContext, fid);
    }

    static FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
                                                 DeliveryContext deliveryContext, String adapterName, long duration,
                                                 ProvisioningContext provisioningContext) {
        return new FulfillmentTaskImpl(bundleDescriptor, deliveryContext, adapterName, duration, provisioningContext);
    }

    static DeliveryContext createDeliveryContext(Capabilities deviceCapabilities,
                                                 String networkID, String user, Map<String, String> requestMap) {
        return new DeliveryContextImpl(deviceCapabilities, networkID, user, requestMap);
    }

    static Capabilities createEmptyCapabilities() {
        return new EmptyCapabilities();
    }

    static ProvisioningArchiveType createProvisioningArchive(InputStream is) throws IOException {
        if (is == null) {
            logger.info("JV-1501-012: No provisioning descriptor");
            throw new IOException("JV-1501-012: No provisioning descriptor");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("org.jvending.provisioning.stocking.par");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
           // unmarshaller.setValidating(true);
            return (ProvisioningArchiveType) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            logger.log(Level.INFO, "JV-1501-013: Parsing exception for provisioning descriptor", e);
            throw new IOException("JV-1501-013: Parsing exception for provisioning descriptor");
        }
    }
    
    private static Map<String, String> toMap(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<String, String>();
        if (request == null) return paramMap;
        for (Enumeration<?> en = request.getHeaderNames(); en.hasMoreElements();) {
            String headerName = (String) en.nextElement();
            String headerValue = request.getHeader(headerName);
            paramMap.put(headerName, headerValue);
        }
        return paramMap;
    }

    private static class DeliveryContextImpl implements DeliveryContext {

        private final Capabilities deviceCapabilities;

        private final String networkID;

        private final String user;

        private final Map<String, String> requestMap;
        
        private final AndroidDeviceInfo info;

        DeliveryContextImpl(Capabilities deviceCapabilities,
                            String networkID, String user, Map<String, String> requestMap) {
            this.networkID = networkID;
            this.deviceCapabilities = deviceCapabilities;
            this.user = user;
            this.requestMap = requestMap;
            this.info = new AndroidDeviceInfo();
            ProvisioningFactory.populateDeviceInfo(info, requestMap);
        }
                
        public AndroidDeviceInfo getAndroidDeviceInfo() {
			return info;
		}

        public Capabilities getDeviceCapabilities() {
            return deviceCapabilities != null ? deviceCapabilities : new EmptyCapabilities();
        }

        public String getNetworkID() {
            return networkID != null ? networkID : "UNKNOWN";
        }

        public String getUser() {
            return user != null ? user : "UNKNOWN";
        }

        public String toString() {
            return "Delivery Context: Network Id = " + networkID +
                    ", User = " + user +
                    ", Headers:\r\n" + requestMap.toString();
        }
    }

    private static class EmptyCapabilities implements Capabilities {

        //List of Strings: Values of capability
        public List<String> getCapability(String name) {
            return Collections.unmodifiableList(new ArrayList<String>());
        }

        public Set<String> getCapabilityNames() {
            return Collections.unmodifiableSet(new HashSet<String>());
        }
    }

    private static class FulfillmentTaskImpl implements FulfillmentTask {

        private BundleDescriptor bundleDescriptor;

        private final DeliveryContext deliveryContext;

        private final String adapterName;

        private final long duration;

        private final String fulfillmentID;

        private final long expiryTime;

        private final ProvisioningContext provisioningContext;
        
		FulfillmentTaskImpl(BundleDescriptor bundleDescriptor,
				DeliveryContext deliveryContext, String adapterName,
				long duration, ProvisioningContext provisioningContext,
				String fid) {

			this.bundleDescriptor = bundleDescriptor;
			this.deliveryContext = deliveryContext;
			this.adapterName = adapterName;
			this.duration = duration;
			expiryTime = System.currentTimeMillis() + duration;
			fulfillmentID = ( fid != null) ? fid : UUID.randomUUID().toString();
			this.provisioningContext = provisioningContext;
		}

        FulfillmentTaskImpl(BundleDescriptor bundleDescriptor,
                            DeliveryContext deliveryContext, String adapterName, long duration,
                            ProvisioningContext provisioningContext) {

            this.bundleDescriptor = bundleDescriptor;
            this.deliveryContext = deliveryContext;
            this.adapterName = adapterName;
            this.duration = duration;
            expiryTime = System.currentTimeMillis() + duration;
            fulfillmentID = UUID.randomUUID().toString();
            this.provisioningContext = provisioningContext;
        }

        public String getAdapterName() {
            return adapterName;
        }

        public BundleDescriptor getBundleDescriptor() {
            return bundleDescriptor;
        }

        public String getDeliveryURI() throws ProvisioningException {

            URI bundleURI = null;

            BaseDescriptorFile descriptorFile = (BaseDescriptorFile) bundleDescriptor.getDescriptorFile();
            BaseDeliverable contentFile = (BaseDeliverable) bundleDescriptor.getContentFile();
            if (descriptorFile != null) {
                bundleURI = descriptorFile.getURI();
            } else if (contentFile != null) {
                bundleURI = contentFile.getURI();
            }

            //sanity check: should not happen, let null pointer propagate up
            if (bundleURI == null)
                logger.warning("Bundle URI is null. Problem with the data: Bundle Id = " +
                        bundleDescriptor.getBundleID());

            if (bundleURI.isAbsolute())//handled by some other server
                return bundleURI.toString();

            AdapterContext adapterContext = (AdapterContext) provisioningContext;
            Adapter adapter = adapterContext.getAdapter(adapterName);
            try {
                return adapter.createFulfillmentURI(this);
            } catch (AdapterException e) {
                throw new ProvisioningException("JV: Could not create Fulfillment URI", e);
            }
        }

        public DeliveryContext getDeliveryContext() {
            return deliveryContext;
        }

        public long getExpiryTime() {
            return expiryTime;
        }

        public boolean isExpired() {
            return duration != 0 && expiryTime < System.currentTimeMillis();
        }

        public String getFulfillmentID() {
            return fulfillmentID;
        }

        public String toString() {
            return "Fulfillment Task: Task Id = " + fulfillmentID +
                    ", Bundle Id = " + bundleDescriptor.getBundleID() +
                    ", Adapter Name = " + adapterName;
        }
    }
}
