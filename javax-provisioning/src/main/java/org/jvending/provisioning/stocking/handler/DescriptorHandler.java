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
package org.jvending.provisioning.stocking.handler;

import java.util.List;
import java.util.Map;

import org.jvending.provisioning.model.clientbundle.ContentFile;
import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.par.DescriptorFile;
import org.jvending.registry.RepositoryRegistry;

/**
 * Provides a service for obtaining content for a specific descriptor, such as a JAD or JNLP descriptor. The developer
 * will implement a class based upon the format of the descriptor.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public interface DescriptorHandler {

    /**
     * Mutator for <code>RepositoryRegistry</code>
     *
     * @param repositoryRegistry
     */
    void setRepositoryRegistry(RepositoryRegistry repositoryRegistry);

    /**
     * Obtains a <code>List</code> of <code>ContentFile</code>s associated with a descriptor. Never returns a null
     * value for the list. The implementation MUST NOT modify the contentMap. Such events must be handled outside of
     * a <code>DescriptorHandler</code> context.
     *
     * @param descriptorFileType
     * @param contentMap contains descriptors and bundles.
     * @return List of ContentFiles
     * @throws StockingException the handler is unable to process a valid list of ContentFiles
     */
    List<ContentFile> getContentFiles(DescriptorFile descriptorFileType, Map<String, byte[]> contentMap) throws StockingException;

}