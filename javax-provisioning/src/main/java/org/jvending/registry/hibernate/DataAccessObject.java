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
package org.jvending.registry.hibernate;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.jvending.registry.RepositoryRegistry;

/**
 * Interface for DataAccessObjects
 *
 * @author Shane Isbell
 * @since 1.0.0
 */
public interface DataAccessObject {
    /**
     * Mutator for SessionFactories
     *
     * @param sessionFactories which may be of type Hibernate2 or Hibernate3
     */
    void setSessionFactories(Map<String, SessionFactory> sessionFactories);

    /**
     * Class name accessor.
     *
     * @return class name of the DataAccessObject
     */
    String getClassName();

    /**
     * Accessor for ID
     *
     * @return id of the DataAccessObject
     */
    String getID();

    void setRepositoryRegistry(RepositoryRegistry repositoryRegistry);

}
