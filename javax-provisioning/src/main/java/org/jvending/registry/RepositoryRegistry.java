/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 * JVending
 * Copyright (c) 2005 - 2006 Shane Isbell.  All rights reserved.
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
package org.jvending.registry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

/**
 * Interface that provides services for loading registry config files and accessing and managing repositories.
 *
 * @author Shane Isbell
 * @since 1.2.0
 */

public interface RepositoryRegistry {

    /**
     * Mutator for setting the <code>RepositoryLoader</code>
     *
     * @param repositoryLoader
     */
    void setRepositoryLoader(RepositoryLoader repositoryLoader);

    /**
     * Mutator for setting the <code>RegistryLoader</code>
     *
     * @param registryLoader
     */
    void setRegistryLoader(RegistryLoader registryLoader);

    /**
     * Loads the registry from inputStream. Multiple config files may be loaded into the registry.
     *
     * @param inputStream contains the jvending-config file.
     * @throws java.io.IOException thrown on interrupted I/O
     */
    void loadFromInputStream(InputStream inputStream) throws IOException;

    /**
     * Convenience method for loading a file off of a file system.
     *
     * @param fileName relative or absolute path of the file
     * @throws IOException thrown on interrupted I/O
     */
    void loadFromFile(String fileName) throws IOException;


    /**
     * Convenience method for loading from a JAR or Resource.
     *
     * @param fileName relative or absolute path of the file
     * @throws IOException thrown on interrupted I/O
     */
    void loadFromResource(String fileName, Class<?> sourceClass) throws IOException;

    /**
     * Adds a repository to the registry. If the repository name already exists, this method will overwrite the old
     * Repository instance within the registry.
     *
     * @param name       name of the repository
     * @param repository instance of the repository
     */
    void addRepository(String name, Repository repository);

    /**
     * Finds a repository from the registry.
     *
     * @param name name of the repository.
     * @return instance of the Repository or null if instance does not exist
     */
    Repository find(String name);

    /**
     * Removes a repository from the registry
     *
     * @param name name of the repository
     */
    void removeRepository(String name);

    /**
     * Accessor for repository names.
     *
     * @return unmodifiable set of repository names
     */
    Set<String> getRepositoryNames();

    /**
     * Empties all of the repositories from the registry.
     */
    void empty();
    
    void setServletContext(ServletContext sc);
    
    ServletContext getServletContext();

    public static class Factory {
        private Factory() {
        }

        public static RepositoryRegistry create() {
            return new RepositoryRegistry() {

                private Hashtable<String, Repository> repositories = new Hashtable<String, Repository>();

                private RepositoryLoader repositoryLoader;

                private RegistryLoader registryLoader;
                
                private ServletContext servletContext;

                public synchronized void setRepositoryLoader(RepositoryLoader loader) {
                    repositoryLoader = loader;
                }

                public synchronized void setRegistryLoader(RegistryLoader loader) {
                    registryLoader = loader;
                }

                public synchronized void loadFromInputStream(InputStream inputStream) throws IOException {
                    if (repositoryLoader == null || registryLoader == null) {
                        InputStream stream =
                                org.jvending.registry.RepositoryRegistry.class.getResourceAsStream("/registry.properties");

                        if (stream == null)
                            throw new IOException("JV-000-001: Could not find /registry.properties file with the jar");

                        Properties prop = new Properties();
                        prop = new Properties();
                        prop.load(stream);

                        if (repositoryLoader == null) {
                            String loaderClassName = prop.getProperty("repositoryLoader");
                            if (loaderClassName == null)
                                throw new IOException("JV-000-002: Missing the repositoryLoader from the /registry.properties");

                            String message = "Repository Loader = " + loaderClassName;
                            try {
                                Class<?> c = Class.forName(loaderClassName);
                                repositoryLoader = (RepositoryLoader) c.newInstance();
                                //if(repositoryLoader == null)
                            } catch (Exception e) {
                                throw new IOException("JV-000-003: " + e.toString() + " : " + message);
                            } catch (Error e) {
                                throw new IOException("JV-000-004: " + e.toString() + " : " + message);
                            }
                        }

                        if (registryLoader == null) {
                            String loaderClassName = prop.getProperty("registryLoader");
                            if (loaderClassName == null)
                                throw new IOException("JV-000-005: Missing the registryLoader from the /registry.properties");

                            String message = "Registry Loader = " + loaderClassName;
                            try {
                                Class<?> c = Class.forName(loaderClassName);
                                registryLoader = (RegistryLoader) c.newInstance();
                            } catch (Exception e) {
                                throw new IOException("JV-000-006: " + e.toString() + " : " + message);
                            } catch (Error e) {
                                throw new IOException("JV-000-007: " + e.toString() + " : " + message);
                            }
                        }
                    }
  
                    repositoryLoader.setRepositoryRegistry(this);
                    registryLoader.setRepositoryLoader(repositoryLoader);
                    registryLoader.loadRegistry(inputStream);//TODO - NPE
                    repositories.putAll(registryLoader.getRepositories());
                }

                public synchronized void loadFromFile(String fileName) throws IOException {
                    FileInputStream fis = new FileInputStream(fileName);
                    loadFromInputStream(fis);
                }


                public synchronized void loadFromResource(String fileName, Class<?> sourceClass) throws IOException {
                    if (sourceClass == null)
                        throw new IOException("JV-000-008: The class cannot be null when loading from a resource");
                    InputStream stream = sourceClass.getResourceAsStream(fileName);

                    if (stream == null)
                        throw new IOException("JV-000-009: Could not locate resource: File Name = " + fileName);
                    loadFromInputStream(stream);
                }

                public synchronized void addRepository(String name, Repository repository) {
                    repositories.put(name, repository);
                }


                public synchronized Repository find(String name) {
                    return (Repository) repositories.get(name);
                }

                public synchronized void removeRepository(String name) {
                    repositories.remove(name);
                }

                public synchronized Set<String> getRepositoryNames() {
                    return Collections.unmodifiableSet(repositories.keySet());
                }

                public synchronized void empty() {
                    repositories.clear();
                }

				public ServletContext getServletContext() {
					return servletContext;
				}

				public void setServletContext(ServletContext sc) {
					this.servletContext = sc;
				}
            };
        }
    }
}
