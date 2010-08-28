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
