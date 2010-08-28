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
package org.jvending.registry.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryLoader;
import org.jvending.registry.RepositoryRegistry;

/**
 * The default repository loader. This class can be extended
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public class StandardRepositoryLoader implements RepositoryLoader {

    private RepositoryRegistry repositoryRegistry;

    /**
     * Takes information from the registry-config file and dynamically builds a <code>Repository</code>
     *
     * @param fileUri        name of the repository's configuration file. It may be located on the file system
     *                        or within a jar.
     * @param repositoryClass name of the repository class
     * @param initParams      <code>Hashtable</code> containing the repository's configuration parameters.
     * @return instance of repository
     * @throws java.io.IOException
     */

    public Repository loadRepository(String fileUri, String repositoryClass, Hashtable<String, String> initParams) throws IOException {
        if(repositoryRegistry == null)
            throw new IOException("JV-000-106: The repository registry has not been set.");

        Hashtable<String, String> props = (initParams != null) ? initParams : new Hashtable<String, String>();

        if (fileUri == null || fileUri.trim().equals(""))
            throw new IOException("JV-000-100: File uri must be provided.");
        if (repositoryClass == null || repositoryClass.trim().equals(""))
            throw new IOException("JV-000-101: Repository class name must be provided: File Name = " + fileUri
                    + ", Properties = " + props.toString());

        InputStream stream = null;
        Repository repository = null;
        try {
            stream = new FileInputStream(fileUri);
        } catch (IOException e) {
            stream = this.getClass().getResourceAsStream(fileUri);
        }
        String message = "File Name = " + fileUri +
                ", Repository Class = " + repositoryClass + ", Properties = " + props.toString();

        if (stream == null)
            throw new IOException("JV-000-102: Unable to loadRegistry config file: " + message);

        try {
            Class<?> c = Class.forName(repositoryClass);
            repository = (Repository) c.newInstance();
            repository.setRepositoryRegistry(repositoryRegistry);
            repository.load(stream, props);

        } catch (IOException e) {
            throw new IOException("JV-000-103: " + e.toString() + " : " + message);
        } catch (Exception e) {
            throw new IOException("JV-000-104: " + e.toString() + " : " + message);
        } catch (Error e) {
            throw new IOException("JV-000-105: " + e.toString() + " : " + message);
        }
        return repository;
    }

    public String getLoaderName() {
        return this.getClass().getName();
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }
}
