package org.jvending.provisioning.stocking.filter;

import java.util.Map;

import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.DescriptorFile;

/**
 * Filters out JNLP files.
 */
public class JnlpFilter extends BundleFilter {

    public void validateBundle(ClientBundleType clientBundle, Map<String, byte[]> content) throws StockingException {
        DescriptorFile descriptorFile = (DescriptorFile) clientBundle.getDescriptorFile();
        if (descriptorFile != null) {
            String mimeType = descriptorFile.getMimeType();
            if (mimeType != null && mimeType.equals("application/x-java-jnlp-file")) {
                throw new StockingException("Excluding JNLP files from the PAR");
            }
        }
    }
}
