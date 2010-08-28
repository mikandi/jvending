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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.jvending.registry.Repository;

/**
 *  A service for loading session factories and for obtaining <code>DataAccessObject</code>s.
 *
 *  The following code shows how to get hibernate sessions from multiple DBs.
 * <pre>
        repositoryRegistry.loadFromFile("/home/name/registry-config.xml");

        HibernateRepository hsqlRepository = (HibernateRepository) repositoryRegistry.find("hibernate-hsqldb");
        SessionFactory hsqlPeerFactory = hsqlRepository.getSessionFactoryByName("peer");
        Session peerSession = hsqlPeerFactory.openSession();

        HibernateRepository mysqlRepository = (HibernateRepository) repositoryRegistry.find("hibernate-mysql");
        SessionFactory mysqlUserFactory = mysqlRepository.getSessionFactoryByName("user");
        Session userSession = mysqlUserFactory.openSession();
    </pre>

 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public interface HibernateRepository extends Repository {

    /**
     * Accessor for the names of Hibernate connections (as given in connections.xml)
     *
     * @return connection names
     */
    Set<String> getConnectionNames();

    /**
     * Accessor for <code>DataAccessObject</code>s.
     *
     * @return unmodifiable set of data access objects.
     */
    Set<DataAccessObject> getDataAccessObjects();

    /**
     *
     * @param connectionName
     * @param inputStream
     * @return map containing <code>SessionFactory</code> instances indexed by the connection names.
     * @throws IOException
     */
    Map<String, SessionFactory> loadSessionFactory(String connectionName, InputStream inputStream) throws IOException;



}
