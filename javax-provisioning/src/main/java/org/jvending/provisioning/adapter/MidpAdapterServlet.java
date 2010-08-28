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
import java.net.URL;
import java.util.logging.Logger;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.Deliverable;
import javax.provisioning.DeliveryComponent;
import javax.provisioning.DeliveryContext;
import javax.provisioning.DeliveryEvent;
import javax.provisioning.DescriptorFile;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.ProvisioningException;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterContext;
import javax.provisioning.adapter.AdapterException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MidpAdapterServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1733725860930107239L;

	private static Logger logger = Logger.getLogger("MidpAdapterServlet");

    private final static int NOTIFY_REQUEST = 0;

    private final static int DESCRIPTOR_FILE_REQUEST = 1;

    private final static int DELIVERABLE_REQUEST = 2;

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //Get everything we need to handle this request
        String fulfillmentId = request.getParameter("fid");
        if (fulfillmentId == null) throw new IOException("JV: No FulfillmentID. Can not process request");

        AdapterContext adapterContext =
                (AdapterContext) this.getServletContext().getAttribute("javax.provisioning.ProvisioningContext");
        DeliveryComponent deliveryComponent = adapterContext.getDeliveryComponent();
        DeliveryContext deliveryContext = adapterContext.getDeliveryContext(request);
        Adapter adapter = adapterContext.getAdapter("midp");

        switch (getRequestType(request)) {
            case NOTIFY_REQUEST:
                String responseNotify = request.getParameter("response");
                if (responseNotify.equals("install-notify")) {
                    DeliveryEvent deliveryEvent = new DeliveryEvent(deliveryContext, "midp",
                            DeliveryEvent.CONFIRMED, fulfillmentId, 0, "Bundle Installed");
                    try {
                        deliveryComponent.handleDeliveryEvent(deliveryEvent);
                    } catch (ProvisioningException e) {
                        logger.info("JV: Unable to handle DeliveryEvent: = " + deliveryEvent.toString());
                    }
                } else if (responseNotify.equals("delete-notify")) {
                    DeliveryEvent deliveryEvent = new DeliveryEvent(deliveryContext, "midp",
                            DeliveryEvent.DELETED, fulfillmentId, 0, "Bundle Deleted");
                    try {
                        deliveryComponent.handleDeliveryEvent(deliveryEvent);
                    } catch (ProvisioningException e) {
                        logger.info("JV: Unable to handle DeliveryEvent: = " + deliveryEvent.toString());
                    }
                } else
                    throw new IOException("JV: Unknown response: Value = " + responseNotify);
                break;
            case DESCRIPTOR_FILE_REQUEST:
                String requestUrl = request.getRequestURL().append("?").append(request.getQueryString()).toString();
                DescriptorFile descriptorFile;
                try {
                    descriptorFile = adapter.createDescriptorFile(new URL(requestUrl));
                } catch (AdapterException e) {
                    e.printStackTrace();
                    throw new IOException("JV: Could not create a DescriptorFile: " + e.getMessage());
                }

                OutputStream os = response.getOutputStream();
                response.setContentType(descriptorFile.getMimeType());
                try {
                    descriptorFile.writeContents(os);
                } catch (IOException e) {
                    DeliveryEvent event = new DeliveryEvent(deliveryContext, "midp",
                            DeliveryEvent.FAILED, fulfillmentId, 0, "Descriptor Delivery Failed");
                    try {
                        deliveryComponent.handleDeliveryEvent(event);
                    } catch (ProvisioningException e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    if (os != null) os.close();
                }
                break;
            case DELIVERABLE_REQUEST:
                FulfillmentTask fulfillmentTask;
                try {
                    fulfillmentTask = adapterContext.getFulfillmentTask(fulfillmentId, deliveryContext, "midp");
                } catch (AdapterException e) {
                    throw new IOException("JV: Could not get FulfillmentTask: ID = " + fulfillmentId);
                }
                BundleDescriptor bundleDescriptor = fulfillmentTask.getBundleDescriptor();
                Deliverable deliverable =
                        ((DescriptorFile) bundleDescriptor.getDescriptorFile()).getContentFiles().get(0);
                os = response.getOutputStream();
                response.setContentType(deliverable.getMimeType());
                try {
                    deliverable.writeContents(os);
                } catch (IOException e) {
                    DeliveryEvent event = new DeliveryEvent(deliveryContext, "midp",
                            DeliveryEvent.FAILED, fulfillmentId, 0, "Content Delivery Failed");
                    try {
                        deliveryComponent.handleDeliveryEvent(event);
                    } catch (ProvisioningException e1) {
                        e1.printStackTrace();
                    }
                } finally {
                    if (os != null) os.close();
                }
                break;
        }
    }

    private int getRequestType(HttpServletRequest request) {
        if (request.getParameter("response") != null)
            return NOTIFY_REQUEST;
        else if (request.getParameter("type") != null && request.getParameter("type").equals("jar")) {
            return DELIVERABLE_REQUEST;
        } else
            return DESCRIPTOR_FILE_REQUEST;

    }
}
