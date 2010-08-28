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
