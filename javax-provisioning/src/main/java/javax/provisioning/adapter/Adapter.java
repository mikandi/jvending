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