/*
 *  JVending
 *  Copyright (C) 2006  Shane Isbell
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

package org.jvending.provisioning.dao;

import java.io.IOException;
import java.util.List;

import org.jvending.provisioning.model.clientbundle.ClientBundle;

/**
 * Provides methods for storing and retrieving client bundles.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public interface ClientBundleDAO {

    /**
     * Returns the client bundle for the specified id.
     *
     * @param id the ID of the client bunlde to return
     * @return the client bundle for the specified id
     */
    ClientBundle getBundleByID(String id);
    
    List<ClientBundle> getBundles();

    /**
     * Stores the list of client bundles. Depending on the implementation and/or configuration, the client bundle
     * may be persisted or merely cached in memory.
     *
     * @param clientBundles the client bundles to store
     * @throws IOException
     */
    void store(List<ClientBundle> clientBundles) throws IOException;
}
