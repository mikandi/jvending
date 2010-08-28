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