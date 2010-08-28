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
package org.jvending.provisioning.deliverable;

import java.net.URI;
import java.net.URL;

import javax.provisioning.Deliverable;

/**
 * Use this class if you want the provisioning framework to handle the construction of deliverables, otherwise
 * extend from Deliverable.
 *
 * @author Shane Isbell
 * @since 1.3.8a
 */


public class BaseDeliverable extends Deliverable {

    private final URI fileUri;

    /**
     * Constructor
     *
     * @param url
     * @param fileUri
     * @param mimeType
     */
    public BaseDeliverable(URL url, URI fileUri, String mimeType) {
        super(url, mimeType);
        this.fileUri = fileUri;
    }

    public URI getURI() {
        return fileUri;
    }

}
