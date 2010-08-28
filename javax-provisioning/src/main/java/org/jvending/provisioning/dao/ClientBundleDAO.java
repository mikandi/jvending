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
