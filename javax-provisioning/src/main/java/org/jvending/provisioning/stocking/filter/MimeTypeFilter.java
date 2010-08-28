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
package org.jvending.provisioning.stocking.filter;

import java.util.Map;

import org.jvending.provisioning.config.MimeTypeRepository;
import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.DescriptorFile;
import org.jvending.registry.RepositoryRegistry;

/**
 * If mime-type is not includes, attempts to determine the mime-type from the URL and adds it into the provisioning
 * descriptor.
 */
public class MimeTypeFilter extends BundleFilter {

    public void validateBundle(ClientBundleType clientBundle, Map<String, byte[]> content) throws StockingException {
        DescriptorFile descriptorFile = (DescriptorFile) clientBundle.getDescriptorFile();
        if (descriptorFile != null) {
            String mimeType = descriptorFile.getMimeType();
            if (mimeType == null) {
                RepositoryRegistry registry = (RepositoryRegistry)
                        filterTask.getStockingHandlerConfig().getStockingContext()
                                .getServletContext().getAttribute("org.jvending.registry.RepositoryRegistry");
                MimeTypeRepository mimeRepository = (MimeTypeRepository) registry.find("mimetype");
                mimeType = mimeRepository.getMimeTypeFromUri(descriptorFile.getValue());
            }
            if(mimeType != null) {
                descriptorFile.setMimeType(mimeType);
            }
        }
    }
}
