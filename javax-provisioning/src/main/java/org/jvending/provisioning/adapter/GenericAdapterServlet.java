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
package org.jvending.provisioning.adapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.Deliverable;
import javax.provisioning.DeliveryComponent;
import javax.provisioning.DeliveryContext;
import javax.provisioning.DeliveryEvent;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.ProvisioningException;
import javax.provisioning.adapter.AdapterContext;
import javax.provisioning.adapter.AdapterException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class GenericAdapterServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8918325411716423393L;
	
	private static Logger logger = Logger.getLogger("GenericAdapterServlet");

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String fulfillmentId = request.getParameter("fid");
        logger.info("Fulfillment ID = " + fulfillmentId);

        AdapterContext adapterContext = (AdapterContext)
                this.getServletContext().getAttribute("javax.provisioning.ProvisioningContext");
        DeliveryContext deliveryContext = adapterContext.getDeliveryContext(request);
        DeliveryComponent deliveryComponent = adapterContext.getDeliveryComponent();

        FulfillmentTask fulfillmentTask;
        try {
            fulfillmentTask = adapterContext.getFulfillmentTask(fulfillmentId, deliveryContext, "generic");
        } catch (AdapterException e) {
            e.printStackTrace();
            throw new IOException("Could not get fulfillment task: Message = " + e.getMessage());
        }

        
        Deliverable deliverable;
        try {
            BundleDescriptor bundleDescriptor = fulfillmentTask.getBundleDescriptor();
            if (bundleDescriptor == null)
                throw new ProvisioningException("JV: Could not find the BundleDescriptor for this FulfillmentTask");
            Deliverable contentFile = bundleDescriptor.getContentFile();
            if(contentFile == null) {
            	throw new ProvisioningException("JV: Could not find content for this FulfillmentTask");
            }
            deliverable = deliveryComponent.getContentFile(contentFile, fulfillmentTask);
        } catch (ProvisioningException e) {
            throw new IOException(e.getMessage());
        }

        OutputStream os = response.getOutputStream();
        response.setContentType(deliverable.getMimeType());
        try {
            deliverable.writeContents(os);
        } catch (IOException e) {
            DeliveryEvent event = new DeliveryEvent(deliveryContext, fulfillmentTask.getAdapterName(),
                    DeliveryEvent.FAILED, fulfillmentId, 0, "Delivery Failed");
            try {
                deliveryComponent.handleDeliveryEvent(event);
            } catch (ProvisioningException e1) {
                e1.printStackTrace();
            }
            throw new IOException(e.getMessage());
        }
        os.close();

        DeliveryEvent event = new DeliveryEvent(deliveryContext, fulfillmentTask.getAdapterName(),
                DeliveryEvent.COMPLETE, fulfillmentId, 0, "Delivery Complete");

        try {
            deliveryComponent.handleDeliveryEvent(event);
        } catch (ProvisioningException e) {
            e.printStackTrace();
        }
    }
}
