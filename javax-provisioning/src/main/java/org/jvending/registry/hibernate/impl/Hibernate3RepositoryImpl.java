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