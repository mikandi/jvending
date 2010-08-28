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

import java.io.Serializable;

/**
 * InvalidFulfillmentIDException are exceptions created by the provisioning framework to indicate an invalid or
 * expired fulfillment task id.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public class InvalidFulfillmentIDException
        extends AdapterException implements Serializable {

	private static final long serialVersionUID = -1765312779434354976L;
	
	private String fulfillmentID;

    public InvalidFulfillmentIDException(String adapterName,
                                         String message, Throwable cause, String fulfillmentID) {
        super(adapterName, message, cause);
        this.fulfillmentID = fulfillmentID;
    }

    public String getFulfillmentID() {
        return fulfillmentID;
    }

}