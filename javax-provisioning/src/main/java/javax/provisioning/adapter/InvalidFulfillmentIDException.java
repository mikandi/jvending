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