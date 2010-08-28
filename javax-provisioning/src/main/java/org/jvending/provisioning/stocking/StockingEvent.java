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
package org.jvending.provisioning.stocking;

import java.io.Serializable;
import java.util.EventObject;

/**
 * Used for notification of events that occur while stocking a PAR file.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */

public class StockingEvent extends EventObject implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2401242611301837325L;

	public static int COMPLETE = 0;

    public static int CONFIRMED = 1;

    public static int REMOVED = 2;

    public static int FAILED = 3;

    public static int OTHER = 5;

    private int eventCode;

    private String message;

    private ProviderContext providerContext;

    private String filterID;

    public StockingEvent(ProviderContext providerContext, String filterID, int eventCode, String message) {
        super(providerContext);
        this.filterID = filterID;
        this.eventCode = eventCode;
        this.message = message;
        this.providerContext = providerContext;
    }

    public int getEventCode() {
        return this.eventCode;
    }

    public String getMessage() {
        return this.message;
    }

    public ProviderContext getProviderContext() {
        return providerContext;
    }

    public String getFilterID() {
        return filterID;
    }
}
