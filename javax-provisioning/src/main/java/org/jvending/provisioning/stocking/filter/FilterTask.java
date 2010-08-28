/*
 *   JVending
 *   Copyright (C) 2006  Shane Isbell
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
