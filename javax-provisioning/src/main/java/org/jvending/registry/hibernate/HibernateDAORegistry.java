/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 * JVending
 * Copyright (c) 2005 Shane Isbell.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by
 *        JVending (http://jvending.sourceforge.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "JVending" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact shane.isbell@gmail.com.
 *
 * 5. Products derived from this software may not be called "JVending"
 *    nor may "JVending" appear in their names without prior written
 *    permission of Shane Isbell.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 *
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
