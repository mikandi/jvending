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
package org.jvending.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.jvending.registry.hibernate.HibernateRepositoryAdaptor;
import org.jvending.registry.hibernate.impl.ServletHibernateRepositoryAdaptee;

public class ServletRepositoryLoader implements RepositoryLoader {

    private ServletContext servletContext;

    private RepositoryRegistry repositoryRegistry;

    private static Logger logger = Logger.getLogger("ServletRepositoryLoader");

    public ServletRepositoryLoader(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }

    /**
     *
     * @param fileUri a URL, file system path or war file
     * @param repositoryClass
     * @param initParams
     * @return
     * @throws IOException
     */
    public Repository loadRepository(String fileUri, String repositoryClass, Hashtable<String, String> initParams) throws IOException {
        Hashtable<String, String> props = (initParams != null) ? initParams : new Hashtable<String, String>();

        if (fileUri == null || fileUri.trim().equals("")) throw new IOException("File uri must be provided.");

        if (repositoryClass == null || repositoryClass.trim().equals(""))
            throw new IOException("Repository class name must be provided: File Name = " + fileUri
                    + ", Properties = " + props.toString());

        Repository repository = null;

        InputStream inputStream = servletContext.getResourceAsStream(fileUri);
            if(inputStream == null) {
                logger.severe("Could not find " + fileUri);
                throw new IOException("Could not find " + fileUri);
            }

        String message = "File Name = " + fileUri +
                ", Repository Class = " + repositoryClass + ", Properties = " + props.toString();

        try {
            Class<?> c = Class.forName(repositoryClass);
            repository = (Repository) c.newInstance();
            if(repository instanceof HibernateRepositoryAdaptor) {
                ServletHibernateRepositoryAdaptee adaptee = new ServletHibernateRepositoryAdaptee(servletContext);
                if(repositoryRegistry == null) {
                	throw new IllegalArgumentException("repositoryRegistry: null");
                }
                adaptee.setRepositoryRegistry(repositoryRegistry);
                
                ((HibernateRepositoryAdaptor) repository).setHibernateRepositoryAdaptee(adaptee);
            }
            if(repositoryRegistry == null) {
            	throw new IllegalArgumentException("repositoryRegistry: null");
            }
            repository.setRepositoryRegistry(repositoryRegistry);
            repository.load(inputStream, props);
        } catch (IOException e) {
            throw new IOException("JV-REG-001:" + e.toString() + " : " + message );
        } catch (Exception e) {
            throw new IOException("JV-REG-001a:" + e.toString() + " : " + message);
        } catch (Error e) {
            throw new IOException("JV-REG-001b:" + e.toString() + " : " + message);
        }
        return repository;
    }

    public String getLoaderName() {
        return this.getClass().getName();
    }
}
