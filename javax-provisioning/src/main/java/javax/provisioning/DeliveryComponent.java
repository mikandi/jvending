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
package javax.provisioning;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.provisioning.adapter.AdapterContext;
import javax.servlet.ServletException;


/**
 * A provisioning application developer extends DeliveryComponent to handle requests for delivery for
 * client bundles. The lifecycle of a delivery component is managed by the provisioning framework using
 * the init() and destroy() methods.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public abstract class DeliveryComponent {

    private static Logger logger = Logger.getLogger("DeliveryComponent");

    protected AdapterContext adapterContext;

    /**
     * Called by the provisioning framework when the component is being taken out of service.
     */
    public void destroy() {
        logger.info("JV-1001-001: Destroying DeliveryComponent");
    }
    

    public abstract boolean isPaid(String fid) throws IOException;


    /**
     * Called by the provisioning framework whenever the client is requesting delivery of bundle content.
     * The content of a bundle may be a MIDlet JAR file, for example.
     */
    public Deliverable getContentFile(Deliverable deliverable,
                                      FulfillmentTask fulfillmentTask) throws ProvisioningException {

        if (fulfillmentTask == null || deliverable == null)
            throw new ProvisioningException("Fulfillment task and/or deliverable have null values.");
        if (fulfillmentTask.isExpired()) {
            DeliveryEvent event = new DeliveryEvent(fulfillmentTask.getDeliveryContext(),
                    fulfillmentTask.getAdapterName(), DeliveryEvent.INVALID_FULFILLMENT_ID,
                    fulfillmentTask.getFulfillmentID(), 0, "Expired FulfillmentTask");
            handleDeliveryEvent(event);
            throw new ProvisioningException("JV-1001-002: This fulfillment task has expired: Fulfillment Task ID = "
                    + fulfillmentTask.getFulfillmentID() + ", Expiry Time = "
                    + fulfillmentTask.getExpiryTime() + ", Adapter Name = " + fulfillmentTask.getAdapterName());

        }

        String mimeType = deliverable.getMimeType();

        /* The Spec (CP 7.1) only requires the support of MIDP OTA. Later we can add in other types like
        * JNLP.
        */
        if (mimeType != null && mimeType.equals("text/vnd.sun.j2me.app-descriptor")) {
            DescriptorFile descriptorFile = (DescriptorFile) deliverable;
            List<Deliverable> contentFiles = descriptorFile.getContentFiles();
            if (contentFiles == null || contentFiles.size() != 1)
                throw new ProvisioningException("JAD has incorrect number of deliverables associated with it");
            return (Deliverable) contentFiles.get(0);
        } else
            return deliverable;
    }


    /**
     * Called by the provisioning framework whenever the client notifies it of a delivery event. For example,
     * notification of successful installation from a MIDP device.
     */
    public void handleDeliveryEvent(DeliveryEvent deliveryEvent) throws ProvisioningException {
        logger.info("Delivery Event: Event = " + deliveryEvent.toString());//TODO -Store in DB (Used for Purchasing) - contains deviceId
    }

    /**
     * Called by the provisioning framework whenever the client is requesting a bundle’s descriptor file.
     */
    public void handleDescriptorFile(DescriptorFile descriptorFile,
                                     FulfillmentTask fulfillmentTask) throws ProvisioningException {
    	
        if (fulfillmentTask.isExpired()) {
            DeliveryEvent event = new DeliveryEvent(fulfillmentTask.getDeliveryContext(),
                    fulfillmentTask.getAdapterName(), DeliveryEvent.INVALID_FULFILLMENT_ID,
                    fulfillmentTask.getFulfillmentID(), 0, "Expired FulfillmentTask");
            handleDeliveryEvent(event);
            throw new ProvisioningException("JV-1001-003: This fulfillment task has expired: Fulfillment Task ID = "
                    + fulfillmentTask.getFulfillmentID() + ", Expiry Time = "
                    + fulfillmentTask.getExpiryTime() + ", Adapter Name = " + fulfillmentTask.getAdapterName());
        }

        /*
        The fulfillmentTask could be put in the ServletContext but this can be dangerous for certain deployments
        (non-clustered apps with round-robin loader balancer config) because the attribute may not be cleaned up
        The AdapterContext.getFulfillmentTask will use the fulfillmentTask in the servletContext if already set here
        For single server and clustered deployments you can add the following code.

        String fulfillmentId = fulfillmentTask.getFulfillmentID();
        ServletContext servletContext = adapterContext.getServletContext();
        servletContext.setAttribute(fulfillmentId, fulfillmentTask);

         A sticky session load balancer may not work, with the above code, unless its layer 7 and can do URL inspection
         because deliveries like a JAD and JAR may possibly be on different IPs (when going through proxies and so on).
         Again all of this depends on your network configuration.
        */
    }

    /**
     * Called by the provisioning framework when the component is being put into service.
     */
    public void init(ProvisioningContext provisioningContext) throws ServletException {
        this.adapterContext = (AdapterContext) provisioningContext;
    }
}