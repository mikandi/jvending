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

import java.util.EventObject;

/**
 * The DeliveryEvent class is used to notify the provisioning developer of events that occur during or after delivery
 * of a bundle. The events may originate either from the provisioning adapter used to deliver the bundle, or from
 * the client device to which the adapter is delivering the bundle. The source of the event (see
 * java.util.EventObject.getSource()) is the DeliveryContext associated with the device to which the provisioning
 * server is delivering.
 */

public class DeliveryEvent extends EventObject implements java.io.Serializable {

	private static final long serialVersionUID = -3132434541478896727L;

	/*
	 * Notification of successful delivery of a bundle.
	 */
	public static int COMPLETE = 0;

	/*
	 * Notification from the client device of successful completion of delivery of a bundle.
	 */
    public static int CONFIRMED = 1;

    /*
     * Notification from the client device of successful deletion of a bundle from the client.
     */
    public static int DELETED = 2;

    /*
     * Notification of failure to complete delivery of a bundle for some reason.
     */
    public static int FAILED = 3;

    /*
     * Notification of a client requesting a resource using a fulfillment ID which is invalid or has expired.
     */
    public static int INVALID_FULFILLMENT_ID = 4;

    /*
     * Notification of some other type of event.
     */
    public static int OTHER = 5; 

    private final String adapterName;

    private final int code;

    private final DeliveryContext deliveryContext;

    private final String description;

    private final int type;

    private final String fulfillmentID;

    /**
     * Constructor
     *
     * @param deliveryContext - can not be null
     * @param adapterName
     * @param type
     * @param fulfillmentID
     * @param code
     * @param description
     */
    public DeliveryEvent(DeliveryContext deliveryContext, String adapterName, int type,
                         String fulfillmentID, int code, String description) {
        super(deliveryContext);
        this.deliveryContext = deliveryContext;
        this.adapterName = adapterName;
        this.type = type;
        this.fulfillmentID = fulfillmentID;
        this.code = (type == COMPLETE || type == INVALID_FULFILLMENT_ID) ? 0 : code;
        this.description = description;
    }

    public String getAdapterName() {
        return adapterName;
    }

    public int getCode() {
        return code;
    }

    public DeliveryContext getDeliveryContext() {
        return deliveryContext;
    }

    public String getDescription() {
        return description;
    }

    public String getFulfillmentID() {
        return fulfillmentID;
    }

    public int getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DeliveryEvent event = (DeliveryEvent) o;

        if (code != event.code) return false;
        if (type != event.type) return false;
        if (!adapterName.equals(event.adapterName)) return false;
        if (!deliveryContext.equals(event.deliveryContext)) return false;
        if (!description.equals(event.description)) return false;
        if (!fulfillmentID.equals(event.fulfillmentID)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = adapterName.hashCode();
        result = 29 * result + code;
        result = 29 * result + deliveryContext.hashCode();
        result = 29 * result + description.hashCode();
        result = 29 * result + type;
        result = 29 * result + fulfillmentID.hashCode();
        return result;
    }

    public String toString() {
        return "Fulfillment ID = " + fulfillmentID + ", AdapterName = " + adapterName
                + ", Description = " + description + ", Code = " + code + ", Type = " + type;
    }

}