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
