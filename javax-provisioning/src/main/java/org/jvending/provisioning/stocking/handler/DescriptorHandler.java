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