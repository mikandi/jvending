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
