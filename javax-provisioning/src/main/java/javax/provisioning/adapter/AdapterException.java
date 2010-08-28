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
 * AdapterExceptions are exceptions created by Adapters or the AdapterContext to indicate a failure in
 * procressing that must be handled.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public class AdapterException extends Exception implements Serializable {

	private static final long serialVersionUID = 5523740273668110934L;
	
	private String adapterName;

    public AdapterException(String adapterName, String message) {
        super((adapterName + ":" + message));
        this.adapterName = adapterName;
    }

    public AdapterException(String adapterName, String message, Throwable cause) {
        super(message, cause);
        this.adapterName = adapterName;
    }

    public String getAdapterName() {
        return adapterName;
    }

}