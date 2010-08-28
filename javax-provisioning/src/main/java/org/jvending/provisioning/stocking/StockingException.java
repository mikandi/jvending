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

/**
 * An exception class for exceptions generated during stocking activities.
 * @author Shane Isbell
 * @since 1.3a
 */

public class StockingException extends Exception implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3914977681064699549L;

	public StockingException() {
        super();
    }
    public StockingException(String message) {
        super(message);
    }

    public StockingException(String message, Throwable cause) {
        super(message, cause);
    }

    public StockingException(Throwable cause) {
        super(cause);
    }


}