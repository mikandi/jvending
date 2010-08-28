/*
 *   JVending
 *   Copyright (C) 2005  Shane Isbell
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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