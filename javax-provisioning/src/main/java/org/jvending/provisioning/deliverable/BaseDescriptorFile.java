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
import java.util.Collections;
import java.util.List;

import javax.provisioning.Deliverable;
import javax.provisioning.DescriptorFile;

/**
 * Use this class if you want the provisioning framework to handle the construction of content files, otherwise
 * extend from DescriptorFile.
 *
 * @author Shane Isbell
 * @version 1.3.8a
 */

public abstract class BaseDescriptorFile extends DescriptorFile {

    private final List<Deliverable> contentDeliverables;

    private final URI uri;

    /**
     * Constructor
     *
     * @param url                 the URL of the descriptor. This URL is an internal one and should not be given out
     *                            to the device.
     * @param uri                 the URI of the descriptor. This URI is the one from the provisioning descriptor.
     * @param mimeType            the mime-type of the descriptors
     * @param contentDeliverables a list of content deliverables associated with this descriptor.
     */
    public BaseDescriptorFile(URL url, URI uri, String mimeType, List<Deliverable> contentDeliverables) {
        super(url, mimeType);
        this.uri = uri;
        this.contentDeliverables = contentDeliverables;
    }

    /**
     * Returns list of deliverables associated with this descriptor.
     *
     * @return list of deliverables associated with this descriptor
     */
    public List<Deliverable> getContentFiles() {
        return Collections.unmodifiableList(contentDeliverables);
    }

    /**
     * Returns URI of this descriptor. This URI is the one from the provisioning descriptor, not the URI that the
     * provisioning framework gives to the device.
     *
     * @return the URI of this descriptor.
     */
    public URI getURI() {
        return uri;
    }
}