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

import java.net.URL;

import javax.provisioning.DescriptorFile;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterException;

/**
 * Adapter for handling generic content types. Content types mapped to the Generic Adapter do not need any special
 * type of content handling.
 *
 * @author Shane Isbell
 * @since 1.2
 */

public final class GenericAdapter extends Adapter {

//    private static final Logger logger = Logger.getLogger("GenericAdapter");

    /**
     * Default Constructor. This class is meant to be instantiated by the framework, not by the application developer.
     */
    public GenericAdapter() {
    }

    /**
     * @see Adapter#createDescriptorFile(java.net.URL)
     */
    public DescriptorFile createDescriptorFile(URL url) throws AdapterException {
        throw new AdapterException("GENERIC",
                "JV-1202-001: The GENERIC adapter does not support the creation of descriptor files");
    }

    /**
     * @see Adapter#createFulfillmentURI(javax.provisioning.FulfillmentTask)
     */
    public String createFulfillmentURI(FulfillmentTask fulfillmentTask) throws AdapterException {
        if (fulfillmentTask == null) throw new AdapterException("GENERIC", "JV-1202-002: FulfillmentTask is null");

        return adapterConfig.getAdapterContext().getServletContext().getInitParameter("CONTENT_DELIVERY_URI")
                + adapterConfig.getBaseURI() + "?fid=" + fulfillmentTask.getFulfillmentID();
    }
}