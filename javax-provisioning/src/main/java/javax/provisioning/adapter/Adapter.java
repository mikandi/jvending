/*
 *  JVending
 *  Copyright (C) 2004  Shane Isbell
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


package javax.provisioning.adapter;

import java.net.URL;

import javax.provisioning.DescriptorFile;
import javax.provisioning.FulfillmentTask;

/**
 * Every provisioning adapter must define a class which extends this this class. The implementation
 * class must have a no-argument constructor. When the provisioning framework is initialized,
 * it creates an instance of each adapter and calls its init method.
 *
 * @author Shane Isbell
 * @version 1.0
 */


public abstract class Adapter {

    protected AdapterConfig adapterConfig;

    public abstract DescriptorFile createDescriptorFile(URL url) throws AdapterException;

    public abstract String createFulfillmentURI(FulfillmentTask fulfillmentTask)
            throws AdapterException;

    public void destroy() throws AdapterException {

    }

    public void init(AdapterConfig adapterConfig) throws AdapterException {
        this.adapterConfig = adapterConfig;
    }

}