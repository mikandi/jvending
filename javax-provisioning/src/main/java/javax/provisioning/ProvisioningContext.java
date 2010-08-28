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

import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Interface defining the services provided by the Provisioning Framework. There
 * is only one instance of ProvisioningContext. It is created when the framework
 * is initialized, and stored as a ServletContext attribute with the name
 * javax.provisioning.ProvisioningContext.
 */

public interface ProvisioningContext {

	FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
			DeliveryContext deliveryContext) throws ProvisioningException;

	FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
			DeliveryContext deliveryContext, String adapterName)
			throws ProvisioningException;

	FulfillmentTask createFulfillmentTask(BundleDescriptor bundleDescriptor,
			DeliveryContext deliveryContext, String adapterName, long duration)
			throws ProvisioningException;

	List<AdapterInfo> getAdapterInfos();

	List<AdapterInfo> getAdapterInfos(BundleDescriptor bundleDescriptor,
			String deviceIndentifier);

	BundleRepository getBundleRepository();

	DeliveryContext getDeliveryContext(HttpServletRequest request);

	Capabilities getDeviceTypeCapabilities(String indentifier);

	Set<String> getDeviceTypeIdentifiers();

	ServletContext getServletContext();

}