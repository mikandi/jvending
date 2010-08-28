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