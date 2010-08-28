package org.jvending.provisioning.stocking.filter;

import java.util.Map;

import org.jvending.masa.CommandExecutor;
import org.jvending.provisioning.stocking.ApkParser;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

public class ApkMetadataFilter implements StockingFilter {

	public void doFilter(FilterTask filterTask) {
		ApkParser parser = new ApkParser();
       	CommandExecutor e = CommandExecutor.Factory.createDefaultCommmandExecutor();
        
        ProvisioningArchiveType archive = filterTask.getProvisioningArchive();
        Map<String, byte[]> contentMap = filterTask.getContent();
        for(ClientBundleType clientBundle : archive.getClientBundle()) {
        	String path = clientBundle.getContentFile().getValue();
        	if(path == null) continue;
        	byte[] b = contentMap.get(path);
        }
	}

}
