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

package org.jvending.provisioning.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import javax.provisioning.Deliverable;
import javax.provisioning.ProvisioningException;

import org.jvending.provisioning.config.deliverables.DeliverableType;
import org.jvending.provisioning.config.deliverables.DeliverablesType;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.jaxb.JaxbConfiguration;

/**
 * @author Shane Isbell
 * @since 1.3a
 */

public final class DeliverableRepository implements Repository {

    private static Logger logger = Logger.getLogger("DeliverableRepository");

    private List<DeliverableType> deliverables;

    public Deliverable getDeliverableFor(URL url, URI uri, String mimeType, List<Deliverable> contentDeliverables)
            throws ProvisioningException {
        if (url == null || mimeType == null || contentDeliverables == null) {
            throw new ProvisioningException("JV-1302-001: Could not find deliverable: URL = " + url + ", URI = " + uri +
                    ", Mime-Type = " + mimeType);
        }

        for (DeliverableType deliverableType : deliverables) {
            for (String mime : (List<String>) deliverableType.getMimeType()) {
                logger.finest("JV-1302-006: Mime = " + mime);
                if (mime.equals(mimeType)) {
                    return createDeliverableFor(url, uri, mimeType, contentDeliverables,
                            deliverableType.getDeliverableClass());
                } else if (!mime.equals("") && mime.endsWith("*")) {
                    if (mime.split("[/]")[0].equals(mimeType.split("[/]")[0])) {
                        return createDeliverableFor(url, uri, mimeType, contentDeliverables,
                                deliverableType.getDeliverableClass());
                    }
                }
            }
        }
        throw new ProvisioningException("JV-1302-002: Could not find deliverable: URL = " + url + ", URI = "
                + uri + ", Mime-Type = " + mimeType);
    }

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
        if (properties == null || inputStream == null)
            throw new IOException("JV-1302-007: Null values for the repository");
        String _package = (String) properties.get("binding-package");
        DeliverablesType deliverablesType = (DeliverablesType) JaxbConfiguration.parse(inputStream, _package);
        deliverables = Collections.unmodifiableList(deliverablesType.getDeliverable());
        if (deliverables == null) {
            throw new IOException("JV-1302-003: Deliverables null.");
        }
    }

    private Deliverable createDeliverableFor(URL url, URI uri, String mimeType,
                                             List<Deliverable> contentDeliverables, String className) throws ProvisioningException {

        int size = (contentDeliverables != null) ? contentDeliverables.size() : 0;

        logger.finest(
                "JV-1302-004: Creating deliverable: URL = " + url + ", URI + " + uri + ", Mime-Type = " + mimeType +
                        "Content Size = " + size + ", Class Name = " + className);

        Deliverable deliverable;
        try {

            Object[] args;
            Class<?>[] classParam;
            args = new Object[4];
            args[3] = contentDeliverables;

            classParam = new Class[4];
            classParam[3] = List.class;
            classParam[0] = URL.class;
            classParam[1] = URI.class;
            classParam[2] = String.class;

            args[0] = url;
            args[1] = uri;
            args[2] = mimeType;

            Class<?> deliverableClass = Class.forName(className);
            Constructor<?> deliverableConstructor = deliverableClass.getConstructor(classParam);
            deliverable = (Deliverable) deliverableConstructor.newInstance(args);
            return deliverable;
        } catch (Exception e) {
            throw new ProvisioningException("JV-1302-005: Unable to create Deliverable: URL = " + url + ", URI = " + uri +
                    ", Mime-Type = " + mimeType +
                    ", Class Name = " + className, e);
        }
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }

}