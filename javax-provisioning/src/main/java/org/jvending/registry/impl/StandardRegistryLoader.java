/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 * JVending
 * Copyright (c) 2006 Shane Isbell.  All rights reserved.
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
package org.jvending.registry.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jvending.registry.RegistryLoader;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryLoader;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * The default loader for the registry-config.xml file.
 *
 * @author Shane Isbell
 * @since 1.2.0
 */

public class StandardRegistryLoader implements RegistryLoader {
    /**
     * Internal list of <code>RepositoryObject</code>s
     */
    private List<RepositoryObject> repositories = new ArrayList<RepositoryObject>();

    private Hashtable<String, Repository> repoMap = new Hashtable<String, Repository>();

    private RepositoryLoader repositoryLoader;

    public void setRepositoryLoader(RepositoryLoader repositoryLoader) {
        this.repositoryLoader = repositoryLoader;
    }

    /**
     * Loads the registry-config file
     *
     * @param inputStream inputstream containing registry-config file
     * @throws java.io.IOException
     */
    public final void loadRegistry(InputStream inputStream) throws IOException {

        KXmlParser parser = new KXmlParser();
        try {
            parser.setInput(inputStream, null);
        } catch (XmlPullParserException e) {
            throw new IOException(e.toString());
        }
        try {
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "registry-config");
            parser.nextTag();
            parser.require(XmlPullParser.START_TAG, null, "repositories");

            while (parser.nextTag() == XmlPullParser.START_TAG) {
                parser.require(XmlPullParser.START_TAG, null, "repository");
                repositories.add(getRepositoryObject(parser));
            }
        } catch (XmlPullParserException e) {
            throw new IOException("JV-000-010:" + e.toString());
        }
        loadIntoRegistry();
    }

    public final Hashtable<String, Repository> getRepositories() {
        return repoMap;
    }



      /**
     * Resolves system variables within the path
     *
     * @param fileName name of the configuration file
     * @return path of the file with resolved system variables. Default value is '.'
     */
    private String toPath(String fileName) {
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

    /**
     * Loads all of the repositories into the registry
     *
     * @throws IOException
     */
    private void loadIntoRegistry() throws IOException {
        if(repositoryLoader == null) throw new IOException("JV-000-011: Repository Loader does not exist");
        for ( RepositoryObject repositoryObject: repositories) {
            String repositoryName = repositoryObject.getRepositoryName();
            String className = repositoryObject.getRepositoryClass();
            String fileName = repositoryObject.getRepositoryConfig();
            //instantiate class based on info in the registry-config file
            Repository repository = null;
			try {
				repository = repositoryLoader.loadRepository(toPath(fileName), className, repositoryObject.getInitParams());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(repository == null) {
				System.out.println("Tried to load repository but failed");
				throw new IOException("Tried to load repository but failed");
			}
            repoMap.put(repositoryName, repository);
        }
    }

    /**
     * Constructs a <code>RepositoryObject</code> from the registry-config file
     *
     * @param parser
     * @return <code>RepositoryObject</code>
     * @throws IOException
     * @throws XmlPullParserException
     */
    private RepositoryObject getRepositoryObject(KXmlParser parser) throws IOException, XmlPullParserException {
        RepositoryObject repositoryObject = new RepositoryObject();
        Hashtable<String, String> initParams = new Hashtable<String, String>();
        for (int i = 0; parser.nextTag() == XmlPullParser.START_TAG; i++) {
            switch (i) {
                case 0:
                    parser.require(XmlPullParser.START_TAG, null, "repository-name");
                    repositoryObject.setRepositoryName(parser.nextText());
                    break;
                case 1:
                    parser.require(XmlPullParser.START_TAG, null, "repository-class");
                    repositoryObject.setRepositoryClass(parser.nextText());
                    break;
                case 2:
                    parser.require(XmlPullParser.START_TAG, null, "repository-config");
                    repositoryObject.setRepositoryConfig(parser.nextText());
                    break;
                default:
                    parser.require(XmlPullParser.START_TAG, null, "init-param");

                    String paramName = null;
                    String paramValue = null;
                    for (int j = 0; parser.nextTag() == XmlPullParser.START_TAG; j++) {

                        switch (j) {
                            case 0:
                                parser.require(XmlPullParser.START_TAG, null, "param-name");
                                paramName = parser.nextText();
                                break;
                            case 1:
                                parser.require(XmlPullParser.START_TAG, null, "param-value");
                                paramValue = parser.nextText();
                                break;
                            default:
                                throw new IOException();
                        }
                    }//end params
                    if (paramName != null && paramValue != null) initParams.put(paramName, paramValue);
            }//end all tags
            repositoryObject.setInitParams(initParams);
        }

        return repositoryObject;
    }

    /**
     * Value Object for Repository Information
     */
    private class RepositoryObject {

        /**
         * Name of the repository
         */
        private String repositoryName;

        /**
         * package and class name of the repository
         */
        private String repositoryClass;

        /*Path and name of the repository config file*/
        private String repositoryConfig;

        /**
         * Initialization parameters of the repository
         */
        private Hashtable<String, String> initParams;

        /**
         * Empty Constructor
         */
        RepositoryObject() {
        }

        String getRepositoryName() {
            return repositoryName;
        }

        void setRepositoryName(String repositoryName) {
            this.repositoryName = repositoryName;
        }

        String getRepositoryClass() {
            return repositoryClass;
        }

        void setRepositoryClass(String repositoryClass) {
            this.repositoryClass = repositoryClass;
        }

        String getRepositoryConfig() {
            return repositoryConfig;
        }

        void setRepositoryConfig(String repositoryConfig) {
            this.repositoryConfig = repositoryConfig;
        }

        Hashtable<String, String> getInitParams() {
            return initParams;
        }

        void setInitParams(Hashtable<String, String> initParams) {
            this.initParams = initParams;
        }

        /**
         * Classes are equal if they have the same values for class, config and name
         */
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RepositoryObject)) return false;

            final RepositoryObject repositoryObject = (RepositoryObject) o;

            if (!repositoryClass.equals(repositoryObject.repositoryClass)) return false;
            if (!repositoryConfig.equals(repositoryObject.repositoryConfig)) return false;
            if (!repositoryName.equals(repositoryObject.repositoryName)) return false;

            return true;
        }

        /**
         * Classes have identical hash code if they have the same values for class, config and name
         */
        public int hashCode() {
            int result;
            result = repositoryName.hashCode();
            result = 29 * result + repositoryClass.hashCode();
            result = 29 * result + repositoryConfig.hashCode();
            return result;
        }

        public String toString() {
            return "Name = " + repositoryName +
                    ", Class = " + repositoryClass +
                    ", Config = " + repositoryConfig;
        }
    }

}
