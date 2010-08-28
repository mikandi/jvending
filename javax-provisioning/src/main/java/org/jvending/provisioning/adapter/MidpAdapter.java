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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.Deliverable;
import javax.provisioning.DescriptorFile;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterConfig;
import javax.provisioning.adapter.AdapterContext;
import javax.provisioning.adapter.AdapterException;

/**
 * Adapter for handling the MIDP interaction of JAD/JAR delivery to the device.
 *
 * @author Shane Isbell
 * @since 1.0
 */

public final class MidpAdapter extends Adapter {

    /**
     * Default Constructor. This class is meant to be instantiated by the framework, not by the application developer.
     */
    public MidpAdapter() {
    }

    public DescriptorFile createDescriptorFile(URL url) throws AdapterException {
        if (url == null) throw new AdapterException("JV-1200-001: MIDP", "URL can not be null");
        AdapterContext adapterContext = adapterConfig.getAdapterContext();
        //sanity check
        if (adapterContext == null)
            throw new AdapterException("MIDP", "JV-1200-002: SEVERE exception: AdapterContext is null");

        //spec (7.3.2.1): must pass to delivery component
        String fulfillmentId = null;
        String query = url.getQuery();
        if (query == null)
            throw new AdapterException("MIDP", "JV-1200-008: The URL is missing the fid parameter: URL = "
                    + url.toString());

        String[] queryTokens = query.split("[&]");
        for (int i = 0; i < queryTokens.length; i++) {
            String token = queryTokens[i];
            if (token.startsWith("fid")) {
                String[] ids = token.split("=");
                fulfillmentId = ids[1];
            }
        }

        FulfillmentTask fulfillmentTask = adapterContext.getFulfillmentTask(fulfillmentId, null, "midp");
        BundleDescriptor bundleDescriptor = fulfillmentTask.getBundleDescriptor();

        if (bundleDescriptor == null)
            throw new AdapterException("MIDP", "JV-1200-003: Could not locate the BundleDescriptor: FulfillmentID = "
                    + fulfillmentId);

        //Get the descriptorFile (JAD) they we are going to modify with new properties
        DescriptorFile descriptorFile = (DescriptorFile) bundleDescriptor.getDescriptorFile();
        if (descriptorFile == null)
            throw new AdapterException("MIDP", "JV-1200-004: Could not locate the DescriptorFile: FulfillmentID = "
                    + fulfillmentId);

        //Set Notify and URL other properties in the JAD
        String baseUri = adapterConfig.getAdapterContext().getServletContext().getInitParameter("CONTENT_DELIVERY_URI")
                + adapterConfig.getBaseURI();
        String jarUrl = baseUri + "?fid=" + fulfillmentId + "&type=jar";
        String installNotifyUrl = baseUri + "?fid=" + fulfillmentId + "&response=install-notify";
        String deleteNotifyUrl = baseUri + "?fid=" + fulfillmentId + "&response=delete-notify";

        descriptorFile.setAppProperty("MIDlet-Jar-URL", jarUrl);

        String installNotifyParam = adapterConfig.getInitParameter("install-notify");
        String deleteNotifyParam = adapterConfig.getInitParameter("delete-notify");

        //Spec (7.9.1 : Jad must always have, by default, a value for intall-notify and delete-notify)
        if ((installNotifyParam != null && !(installNotifyParam.trim().equalsIgnoreCase("true")
                || installNotifyParam.trim().equalsIgnoreCase("false")))) {
            throw new AdapterException("MIDP",
                    "JV-1200-005: The install-notify parameter within the adapters.xml value is not valid {must be true or false}: install-notify = "
                            + installNotifyParam);
        }

        if ((deleteNotifyParam != null && !(deleteNotifyParam.trim().equalsIgnoreCase("true")
                || deleteNotifyParam.trim().equalsIgnoreCase("false")))) {
            throw new AdapterException("MIDP",
                    "JV-1200-006: The delete-notify parameter within the adapters.xml value is not valid {must be true or false}: delete-notify = "
                            + deleteNotifyParam);
        }

        String installNotify = (installNotifyParam == null) ? "true" : installNotifyParam.trim();
        String deleteNotify = (deleteNotifyParam == null) ? "true" : deleteNotifyParam.trim();

        if (installNotify.equalsIgnoreCase("true"))
            descriptorFile.setAppProperty("MIDlet-Install-Notify", installNotifyUrl);
        if (deleteNotify.equalsIgnoreCase("true"))
            descriptorFile.setAppProperty("MIDlet-Delete-Notify", deleteNotifyUrl);

        //Get the Size of the associated JAR and set it. This is a bit expensive but the most reliable.
        List<Deliverable> jars = descriptorFile.getContentFiles();
        if (jars.size() == 0) {
            URL descriptorURL = descriptorFile.getURL();
            boolean isUriAbsolute;
            try {
                isUriAbsolute = descriptorURL.toURI().isAbsolute();
            } catch (URISyntaxException e) {
                e.printStackTrace();
                throw new AdapterException("midp", "The DescriptorFile has an invalid URI: URL = " + url.toString());
            }

            if (isUriAbsolute) {//The file is hosted elsewhere
                return descriptorFile;
            } else {
                throw new AdapterException("midp", "JV: JAD contains no JAR reference");
            }
        } else if (jars.size() > 1)
            throw new AdapterException("midp",
                    "JV: JAD either contains more than one JAR references: Size = " + jars.size());

        Deliverable deliverable = (Deliverable) jars.get(0);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = deliverable.getInputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = is.read(buffer)) >= 0) {
                os.write(buffer, 0, n);
            }
            int jarSize = os.toByteArray().length;
            descriptorFile.setAppProperty("MIDlet-Jar-Size", String.valueOf(jarSize));
        } catch (IOException e) {
            throw new AdapterException("midp", "JV: Could not get InputStream for the Deliverable");
        } finally {
            try {
                if (is != null) is.close();
                os.close();
            } catch (IOException e) {

            }
        }
        return descriptorFile;
    }

    //Used by the above method (so need to embed the fulfillmentID in the URL)
    public String createFulfillmentURI(FulfillmentTask fulfillmentTask) throws AdapterException {
        if (fulfillmentTask == null) throw new AdapterException("MIDP", "JV-1200-007: FulfillmentTask is null");

        return adapterConfig.getAdapterContext().getServletContext().getInitParameter("CONTENT_DELIVERY_URI")
                + adapterConfig.getBaseURI() + "?fid=" + fulfillmentTask.getFulfillmentID();
    }

    protected void setAdapterConfig(AdapterConfig adapterConfig) {
        this.adapterConfig = adapterConfig;
    }
}