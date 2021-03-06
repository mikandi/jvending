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

import javax.provisioning.DeliveryComponent;
import javax.provisioning.DeliveryContext;
import javax.provisioning.FulfillmentTask;
import javax.provisioning.ProvisioningContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The AdapterContext provides an adapter-specific view of the singleton ProvisioningContext.
 * That is, it defines the services that the framework provides for adapters.
 */

public interface AdapterContext extends ProvisioningContext {

    String encodeURL(HttpServletRequest request,
                            HttpServletResponse response, String url);

    Adapter getAdapter(String adapterName);

    AdapterConfig getAdapterConfig(String adapterName);

    DeliveryComponent getDeliveryComponent();

    FulfillmentTask getFulfillmentTask(String fulfillmentID,
                                              DeliveryContext deliveryContext, String adapterName)
            throws AdapterException, InvalidFulfillmentIDException;


}