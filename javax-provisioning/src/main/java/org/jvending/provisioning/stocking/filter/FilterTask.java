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

import org.jvending.provisioning.stocking.ProviderContext;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

/**
 * Represents the task of filtering a PAR file within a provider context.
 * @author Shane Isbell
 * @since 2.0.0
 */

public interface FilterTask {

    /**
     * Returns the <code>ProviderContext</code> for this task.
     * @return the <code>ProviderContext</code> for this task.
     */
    ProviderContext getProviderContext();

    /**
     * Returns a unique ID for this task.
     * @return a unique ID for this task.
     */
    String getFilterID();

    /**
     * Returns a map of all of the content within the PAR file. The key to each entry is the entry name within the PAR
     * file. For instance, the provisioning archive can be obtained by
     * <code>FilterTask.getContent().get("META-INF/provisioning.xml")</code> The returned value must be cast as
     * <code>byte[]</code>
     * @return a map of all of the content within the PAR file.
     */
    Map<String, byte[]> getContent();

    /**
     * Returns an unmarshalled version of the provisioning.xml file. The object structure is directly based on
     * <code>http://java.sun.com/xml/ns/j2ee-cp Provisioning_1_0.xsd</code>.
     * @return an unmarshalled version of the provisioning.xml file.
     */
    ProvisioningArchiveType getProvisioningArchive();

    /**
     * Returns the <code>StockingHandlerConfig</code> for this task.
     * @return the <code>StockingHandlerConfig</code> for this task.
     */
    StockingHandlerConfig getStockingHandlerConfig();

}
