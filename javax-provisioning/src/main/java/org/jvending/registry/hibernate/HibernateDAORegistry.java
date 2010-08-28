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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;

/**
 * A registry for obtaining instances of <code>org.jvending.registry.hibernate.DataAccessObject</code>s.
 * The following sample shows how to configure and use the DAO Registry:
 <pre>
    RepositoryRegistry repositoryRegistry = RepositoryRegistry.Factory.create();
    repositoryRegistry.loadFromFile("/src/test/resources/hibernate3/sample-config.xml");

    HibernateDAORegistry daoRegistry = HibernateDAORegistry.Factory.create();
    daoRegistry.setRepositoryRegistry(repositoryRegistry);

    DAOTestClass testClass =
    (DAOTestClass) hibernateDAORegistry.find("dao:DAOTest");
    UserInfo info = new UserInfo();
    info.setFirstName("homer");
    testClass.update(info);
 </pre>
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public interface HibernateDAORegistry {

    void setRepositoryRegistry(RepositoryRegistry repositoryRegistry);

    /**
     * Returns all DataAccessObjects from the registry.
     *
     * @return a Set of all DataAccessObjects.
     */
    Set<DataAccessObject> findAll();

    /**
     * Finds a DataAccessObject from the registry.
     *
     * @param daoId the id of the DAO to find.
     * @return a DataAccessObject or null if DAO cannot be found.
     */
    DataAccessObject find(String daoId);

    /**
     * Accessor for DAO ids
     *
     * @return unmodifiable set of DataAccessObject ids
     */
    Set<String> getDaoIds();

    public static class Factory {

        private static Logger logger = Logger.getLogger("HibernateDAORegistry");

        private Factory() { }

        public static HibernateDAORegistry create() {
            return new HibernateDAORegistry() {

                private RepositoryRegistry repositoryRegistry;

                public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
                    this.repositoryRegistry = repositoryRegistry;
                }

                public Set<DataAccessObject> findAll() {
                    Set<DataAccessObject> daos = new HashSet<DataAccessObject>();
                    if(repositoryRegistry == null) {
                        logger.warning("Repository registry has not been set");
                        return Collections.unmodifiableSet(daos);
                    }
                    Set<String> names = repositoryRegistry.getRepositoryNames();
                    for (String name : names) {
                        Repository repository = repositoryRegistry.find(name);
                        if (repository instanceof HibernateRepository) {
                            Set<DataAccessObject> o = ((HibernateRepository) repository).getDataAccessObjects();
                            daos.addAll(o);
                        }
                    }
                    return Collections.unmodifiableSet(daos);
                }

                public DataAccessObject find(String daoId) {
                    if(repositoryRegistry == null) {
                        logger.warning("Repository registry has not been set");
                        return null;
                    }
                    Set<String> names = repositoryRegistry.getRepositoryNames();
                    for (String name : names) {
                        Repository repository = repositoryRegistry.find(name);
                        if (repository instanceof HibernateRepository) {
                            Set<DataAccessObject> o = ((HibernateRepository) repository).getDataAccessObjects();
                            for (DataAccessObject dao : o) {
                                if (dao.getID().trim().equals(daoId)) {
                                    return dao;
                                }
                            }
                        }
                    }
                    return null;
                }

                public Set<String> getDaoIds() {
                    Set<String> daoIds = new HashSet<String>();
                    if(repositoryRegistry == null) {
                        logger.warning("Repository registry has not been set");
                        return Collections.unmodifiableSet(daoIds);
                    }
                    Set<String> names = repositoryRegistry.getRepositoryNames();
                    for (String name :names) {
    
                        Repository repository = repositoryRegistry.find(name);
                        if (repository instanceof HibernateRepository) {
                            Set<DataAccessObject> o = ((HibernateRepository) repository).getDataAccessObjects();
                            for (DataAccessObject dao : o) {
                                daoIds.add(dao.getID());
                            }
                        }
                    }
                    return Collections.unmodifiableSet(daoIds);
                }
            };
        }
    }

}
