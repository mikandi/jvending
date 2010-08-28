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
