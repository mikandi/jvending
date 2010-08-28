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
package org.jvending.registry.hibernate.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.codehaus.plexus.util.IOUtil;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.hibernate.DataAccessObject;
import org.jvending.registry.hibernate.Hibernate3Repository;
import org.jvending.registry.hibernate.HibernateRepositoryAdaptee;
import org.jvending.registry.hibernate.HibernateRepositoryAdaptor;

/**
 * This class provides services for accessing hibernate3 session factories.
 *
 * @author Shane Isbell
 * @since 1.1.0
 */
public final class Hibernate3RepositoryImpl implements Hibernate3Repository, HibernateRepositoryAdaptor {

    private static Logger logger = Logger.getLogger("Hibernate3Repository");

    Map<String, SessionFactory> sessionFactories = new HashMap<String, SessionFactory>();

    private HibernateRepositoryAdaptee hibernateRepositoryAdaptee = new StandardHibernateRepositoryAdaptee();

    /**
     * @return unmodifiable set of DAOs.
     */
    public Set<DataAccessObject> getDataAccessObjects() {
        return hibernateRepositoryAdaptee.getDataAccessObjects();
    }

    public void setHibernateRepositoryAdaptee(HibernateRepositoryAdaptee hibernateRepositoryAdaptee) {
        logger.info("JV-103-106: Setting the HibernateRepositoryAdaptee: Class = "
                + hibernateRepositoryAdaptee.getClass().getClass().getName());
        this.hibernateRepositoryAdaptee = hibernateRepositoryAdaptee;
    }

    public void load(InputStream inputStream, Hashtable<String, String> prop) throws IOException {
        hibernateRepositoryAdaptee.setHibernateRepositoryAdaptor(this);
        hibernateRepositoryAdaptee.load(inputStream, prop);
    }


    /**
     * Returns the session factory for the given name.
     *
     * @param name contains the name of the factory
     * @return a session factory for the given name
     * @throws HibernateException if there is a problem obtaining the session factory
     */
    public SessionFactory getSessionFactoryByName(String name) throws HibernateException {
        SessionFactory sessionFactory = (SessionFactory) sessionFactories.get(name);
        if (sessionFactory == null)
            throw new HibernateException("JV-103-001: Session Factory is null: Factory Name = " + name);
        return sessionFactory;
    }

    public Map<String, SessionFactory> loadSessionFactory(String connectionName, InputStream inputStream) throws IOException {
        if (connectionName == null) throw new IOException("JV-103-002: Connection Name cannot be null");
        if (inputStream == null)
            throw new IOException("JV-103-003: File cannot be null: Connection Name = " + connectionName);
        try {
        	byte[] b = IOUtil.toByteArray(inputStream);
        	
            sessionFactories.put(connectionName,
                    new HibernateConfiguration().configure(new ByteArrayInputStream(b)).buildSessionFactory());
            
            logger.info("JV-103-105: Added Session Factory: Connection Name = " + connectionName + "/r/n" + new String(b));
        } catch (HibernateException e) {
            e.printStackTrace();
            throw new IOException("JV-103-004: Had problem loading session factories: Connection Name = "
                    + connectionName);
        }
        return sessionFactories;
    }

    /**
     * Accessor for connection names
     *
     * @return unmodifiable set of Connection Names.
     */
    public Set<String> getConnectionNames() {
        return Collections.unmodifiableSet(sessionFactories.keySet());
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }

    private static class HibernateConfiguration extends Configuration {
        /**
		 * 
		 */
		private static final long serialVersionUID = 5545097092299165188L;

		public Configuration configure(InputStream inputStream) throws HibernateException {
            return super.doConfigure(inputStream, "");
        }
    }

}