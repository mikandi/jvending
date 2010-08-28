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