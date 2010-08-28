package org.jvending.registry.hibernate.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.hibernate.SessionFactory;
import org.jvending.registry.hibernate.DataAccessObject;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServletHibernateRepositoryAdaptee extends StandardHibernateRepositoryAdaptee {

    private ServletContext servletContext;

    private static Logger logger = Logger.getLogger("ServletHibernateRepositoryAdaptee");

    public ServletHibernateRepositoryAdaptee(ServletContext servletContext) {
        this.servletContext = servletContext;
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

            for (HibernateConfig config : connections) {
                logger.info("JV-100-001: Loading config:" + config.toString());

                String connectionUri = config.getConnectionConfig();
                InputStream connectionInputStream = servletContext.getResourceAsStream(connectionUri);
                if(connectionInputStream == null)
                    throw new IOException("Could not find the connection uri = " + connectionUri);

                sessionFactories = loadSessionFactory(config.getConnectionName(), connectionInputStream);
            }
        } catch (XmlPullParserException e) {
            throw new IOException("JV-100-002:" + e.toString());
        }

        Set<String> keys = prop.keySet();
        for ( String keyName : keys) {
            if (keyName.trim().startsWith("dao:")) {
                String daoClassName = (String) prop.get(keyName);
                try {
                    Class<?> c = Class.forName(daoClassName);
                    Class<?>[] param = {String.class, String.class};
                    Object[] paramObject = {daoClassName, keyName};
                    Object o = c.getConstructor(param).newInstance(paramObject);
                    if (!(o instanceof DataAccessObject))
                        throw new IOException("JV-100-103: dao tag references a class that does not implement the DataAccessObject interface.");
                    DataAccessObject dao = (DataAccessObject) o;
                    dao.setSessionFactories(sessionFactories);
                    if(repositoryRegistry == null) {
                    	throw new IllegalArgumentException("repositoryRegistry: null");
                    }
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

}
