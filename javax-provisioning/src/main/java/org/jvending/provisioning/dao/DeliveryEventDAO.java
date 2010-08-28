package org.jvending.provisioning.dao;

import java.io.IOException;
import java.util.List;

import org.jvending.provisioning.model.deliveryevent.DeliveryEvent;

public interface DeliveryEventDAO {

    void store(Object deliveryEvent) throws IOException;
    
    List<DeliveryEvent> findByFullfillmentId(String fid) throws IOException;
    
    List<DeliveryEvent> findBy(String userId, String networkId) throws IOException;

}
