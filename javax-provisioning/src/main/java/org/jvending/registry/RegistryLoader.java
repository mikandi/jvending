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
package org.jvending.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * Interface for custom repository loaders.
 *
 * @author Shane Isbell
 * @since 1.2.0
 */

public interface RegistryLoader {

    /**
     *
     * @param inputStream of the registry-config.xml. This may be of a custom format.
     *
     * @throws IOException
     */
    void loadRegistry(InputStream inputStream) throws IOException;

    /**
     * Accessor for Repositories.
     *
     * @return repositories
     */
    Hashtable<String, Repository> getRepositories();

    /**
     * 
     * @param repositoryLoader
     */
    void setRepositoryLoader(RepositoryLoader repositoryLoader);

}
