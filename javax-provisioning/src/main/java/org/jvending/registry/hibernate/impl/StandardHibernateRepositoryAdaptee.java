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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.hibernate.SessionFactory;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.hibernate.DataAccessObject;
import org.jvending.registry.hibernate.HibernateRepositoryAdaptee;
import org.jvending.registry.hibernate.HibernateRepositoryAdaptor;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Standard implementation of the <code>HibernateRepositoryAdaptee</code>. It will look for the config files on the
 * file system and within its <code>ClassLoader</code>. If you need to load config files within a dynamic loading
 * environment (Application Servers), check the upcoming registry-j2ee project for how this is done.
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public class StandardHibernateRepositoryAdaptee implements HibernateRepositoryAdaptee {

    private static Logger logger = Logger.getLogger("StandardHibernateRepositoryAdaptee");

    protected Set<DataAccessObject> daos = new HashSet<DataAccessObject>();

    protected RepositoryRegistry repositoryRegistry;

    protected HibernateRepositoryAdaptor hibernateRepositoryAdaptor;

    public StandardHibernateRepositoryAdaptee() { }

    public final void setHibernateRepositoryAdaptor(HibernateRepositoryAdaptor hibernateRepositoryAdaptor) {
        this.hibernateRepositoryAdaptor = hibernateRepositoryAdaptor;
    }

    /**
     * Accessor for connection names
     *
     * @return unmodifiable set of Connection Names.
     */
    public final Set<String> getConnectionNames() {
        return hibernateRepositoryAdaptor.getConnectionNames();
    }


    public final void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }

    /**
     * @return unmodifiable set of DAOs.
     */
    public final Set<DataAccessObject> getDataAccessObjects() {
        return Collections.unmodifiableSet(daos);
    }


    public final Map<String, SessionFactory> loadSessionFactory(String connectionName, InputStream inputStream) throws IOException {
        return hibernateRepositoryAdaptor.loadSessionFactory(connectionName, inputStream);
    }

    public void load(InputStream inputStream, Hashtable<String, String> prop) throws IOException {
        KXmlParser parser = new KXmlParser();
        try {
            parser.setInput(inputStream, null);
        } catch (XmlPullParserException e) {
            throw new IOException("JV-100-107: " + e.toString());
        }
        Map<String, SessionFactory> sessionFactories = null;
        try {
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "connections");
            List<HibernateConfig> connections = new ArrayList<HibernateConfig>();
            while (parser.nextTag() == XmlPullParser.START_TAG) {
                parser.require(XmlPullParser.START_TAG, null, "connection");

                HibernateConfig config = new HibernateConfig();
                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "connection-name");
                config.setConnectionName(parser.nextText());

                parser.nextTag();
                parser.require(XmlPullParser.START_TAG, null, "connection-config");
                config.setConnectionConfig(toPath(parser.nextText()));
                connections.add(config);
            }

            for (HibernateConfig config  : connections) {
                logger.info("JV-100-001: Loading config:" + config.toString());

                InputStream connectionInputStream = null;
                String connectionUri = config.getConnectionConfig();
                try {
                    connectionInputStream = new FileInputStream(config.getConnectionConfig());
                } catch(FileNotFoundException e) {
                    logger.info("JV-100-108: Will check in jars. Did not find file on system: Name = " + connectionUri);
                    connectionInputStream =
                            Thread.currentThread().getContextClassLoader().getResourceAsStream(connectionUri);
                }
                if(connectionInputStream == null)
                    throw new IOException("JV-100-109: Could not find the connection uri = " + connectionUri);

                sessionFactories = loadSessionFactory(config.getConnectionName(), connectionInputStream);
            }
        } catch (XmlPullParserException e) {
            throw new IOException("JV-100-002:" + e.toString());
        }

        Set<String> keys = prop.keySet();
        for (String keyName : keys) {
            if (keyName.trim().startsWith("dao:")) {
                String daoClassName = prop.get(keyName);
                try {
                    Class<?> c = Class.forName(daoClassName);
                    Class<?>[] param = {String.class, String.class};
                    Object[] paramObject = {daoClassName, keyName};
                    Object o = c.getConstructor(param).newInstance(paramObject);
                    if (!(o instanceof DataAccessObject))
                        throw new IOException("JV-100-103: dao tag references a class that does not implement the DataAccessObject interface.");
                    DataAccessObject dao = (DataAccessObject) o;
                    dao.setSessionFactories(sessionFactories);
                    dao.setRepositoryRegistry(repositoryRegistry);
                    daos.add(dao);
                    logger.info("JV-100-104: Adding data access object: Class Name = " + daoClassName);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("JV-100-105: Problem instantiating the DAO Class: Class Name = " + daoClassName);
                } catch (Error e) {
                    e.printStackTrace();
                    throw new IOException("JV-100-106: Problem instantiating the DAO Class: Class Name = " + daoClassName);
                }
            }
        }
    }



   // protected abstract Map loadSessionFactory(String connectionName, InputStream inputStream) throws IOException;

    /**
     * Value object for hibernate configuration information.
     */
    protected class HibernateConfig {

        /**
         * Connection name
         */
        private String connectionName;

        /**
         * Name of configuration file that contains a mapping of connection names to cfg files
         */
        private String connectionConfig;

        public String getConnectionConfig() {
            return connectionConfig;
        }

        public void setConnectionConfig(String connectionConfig) {
            this.connectionConfig = connectionConfig;
        }

        public String getConnectionName() {
            return connectionName;
        }

        public void setConnectionName(String connectionName) {
            this.connectionName = connectionName;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final HibernateConfig that = (HibernateConfig) o;

            if (!connectionConfig.equals(that.connectionConfig)) return false;
            if (!connectionName.equals(that.connectionName)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = connectionName.hashCode();
            result = 29 * result + connectionConfig.hashCode();
            return result;
        }

        public String toString() {
            return "Connection Name = " + connectionName + ", Connection Config FileName = " + connectionConfig;
        }
    }

    /**
     * Resolves system variables within the path
     *
     * @param fileName name of the configuration file
     * @return path of the file with resolved system variables. Default value is '.'
     */
    protected String toPath(String fileName) {
        byte[] path = fileName.getBytes();
        int length = path.length;
        StringBuffer env = new StringBuffer();
        StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < length;) {
            if (i >= length - 2) {
                filePath.append((char) path[i++]);
            } else if (path[i] == 36) {
                if (path[++i] == 123) {
                    i++;
                    while (i < length - 1 && path[i] != 125) {
                        env.append((char) path[i++]);
                    }
                    if (path[i] == 125) i++;
                } else {
                    i--;
                    i--;
                }
                String pathEnv = System.getProperty(env.toString().trim(), ".");
                filePath.append(pathEnv.toString());
            } else {
                filePath.append((char) path[i++]);
            }
        }//end for:i
        return filePath.toString();
    }
}
