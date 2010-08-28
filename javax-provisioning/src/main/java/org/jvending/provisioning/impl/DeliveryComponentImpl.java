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
package org.jvending.provisioning.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.provisioning.Capabilities;
import javax.provisioning.DeliveryComponent;
import javax.provisioning.DeliveryContext;
import javax.provisioning.ProvisioningException;

import org.jvending.provisioning.Events;
import org.jvending.provisioning.dao.DeliveryEventDAO;
import org.jvending.provisioning.model.deliveryevent.DeliveryEvent;
import org.jvending.provisioning.model.deliveryevent.DeviceCapability;

class DeliveryComponentImpl extends DeliveryComponent {

    private static Logger logger = Logger.getLogger("DeliveryComponentImpl");

    private DeliveryEventDAO deliveryEventDAO;

    DeliveryComponentImpl(DeliveryEventDAO deliveryEventDAO) {
        this.deliveryEventDAO = deliveryEventDAO;
    }
    
    public boolean isPaid(String fid) throws IOException {
    	List<DeliveryEvent> events = deliveryEventDAO.findByFullfillmentId(fid);
    	/*
    	for(DeliveryEvent event : events ) {
    		if(event.getCode() == javax.provisioning.DeliveryEvent.PAID) {
    			return true;
    		} 
    	}
    	*/
    	return false;
    }
    
    private static boolean exists(DeliveryEvent event) {
		return false;	
    }

    public void handleDeliveryEvent(javax.provisioning.DeliveryEvent deliveryEvent) throws ProvisioningException {
        super.handleDeliveryEvent(deliveryEvent);
        org.jvending.provisioning.model.deliveryevent.DeliveryEvent deliveryEventObject =
                translateDeliveryEventToModel(deliveryEvent);
        
        List<DeliveryEvent> de = null;
		try {
			de = deliveryEventDAO.findByFullfillmentId(deliveryEvent.getFulfillmentID());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
       if(de != null && !de.isEmpty() )
       {
    	   
    	   logger.info("Duplicate fid:" + deliveryEvent.getFulfillmentID());
    	   return;
       }
       
        deliveryEventObject.setAndroidDeviceInfo(deliveryEvent.getDeliveryContext().getAndroidDeviceInfo());
        deliveryEventObject.setEventType(Events.DELIVERY_EVENT);
        try {
            deliveryEventDAO.store(deliveryEventObject);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("JV-1500-44: Unable store delivery event: " + deliveryEvent);
        }
    }

    org.jvending.provisioning.model.deliveryevent.DeliveryEvent
            translateDeliveryEventToModel(javax.provisioning.DeliveryEvent deliveryEvent) {
        org.jvending.provisioning.model.deliveryevent.DeliveryEvent deliveryEventObject =
                new org.jvending.provisioning.model.deliveryevent.DeliveryEvent();

        DeliveryContext deliveryContext = deliveryEvent.getDeliveryContext();
        String networkId = deliveryContext != null ? deliveryContext.getNetworkID() : "UNKNOWN";
        String userId = deliveryContext != null ? deliveryContext.getUser() : "UNKNOWN";

        Capabilities capabilities = deliveryContext != null ? deliveryContext.getDeviceCapabilities() :
                ProvisioningFactory.createEmptyCapabilities();
        List<DeviceCapability> capabilityList = new ArrayList<DeviceCapability>();

        Set<String> capabilityNames = capabilities.getCapabilityNames();
        for ( String name : capabilityNames ) {
            List<String> capabilityValues = capabilities.getCapability(name);
            for (String value : capabilityValues ) {
                DeviceCapability deviceCapability = new DeviceCapability();
                deviceCapability.setName(name);
                deviceCapability.setValue(value);
                capabilityList.add(deviceCapability);
            }
        }

        deliveryEventObject.setAdapterName(deliveryEvent.getAdapterName());
        deliveryEventObject.setCode(deliveryEvent.getCode());
        deliveryEventObject.setDescription(deliveryEvent.getDescription());
        deliveryEventObject.setDeviceCapabilities(capabilityList);
        deliveryEventObject.setFulfillmentId(deliveryEvent.getFulfillmentID());
        deliveryEventObject.setNetworkId(networkId);
        deliveryEventObject.setType(deliveryEvent.getType());
        deliveryEventObject.setUserId(userId);
      
        return deliveryEventObject;
    }
}
