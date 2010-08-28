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
import java.util.Properties;

/**
 * This class is a simple facade for <code>java.util.properties</code>. Repositories that use an underlying properties
 * file (name, value pairs) can extend from this class and add additional domain specific methods. If the extending
 * class provides methods for adding additional properties after a loadRegistry, the getValue method may need to be
 * re-implemented to handle synchronization.
 *
<pre>
    RepositoryRegistry.loadFromFile("./sample-config.xml");
    PropertyRepository repository = (PropertyRepository) RepositoryRegistry.find("adapter");
    String value = repository.getValue("myprop");    `
</pre>
 *
 * @author Shane Isbell
 * @since 0.10
 */

public class PropertyRepository implements Repository {

     /**Internal reference for properties*/
    protected Properties properties = new Properties();

    /**Internal reference for repositoryRegistry*/
    protected RepositoryRegistry repositoryRegistry;

    /**
     * Accessor for properties
     * @param name      the name of the property
     * @return String   value for the given name
     */
    public String getValue(String name) {
        return properties.getProperty(name);
    }

    /**
     * @see org.jvending.registry.Repository#load(InputStream inputStream, Hashtable prop)
     */
    public void load(InputStream inputStream, Hashtable<String, String> prop) throws IOException {
        properties.load(inputStream);
    }

    /**
     * Mutator for <code>RepositoryRegistry</code>
     *
     * @param repositoryRegistry
     */
    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }
}
