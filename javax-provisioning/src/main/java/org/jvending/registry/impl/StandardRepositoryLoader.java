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
package org.jvending.registry.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryLoader;
import org.jvending.registry.RepositoryRegistry;

/**
 * The default repository loader. This class can be extended
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public class StandardRepositoryLoader implements RepositoryLoader {

    private RepositoryRegistry repositoryRegistry;

    /**
     * Takes information from the registry-config file and dynamically builds a <code>Repository</code>
     *
     * @param fileUri        name of the repository's configuration file. It may be located on the file system
     *                        or within a jar.
     * @param repositoryClass name of the repository class
     * @param initParams      <code>Hashtable</code> containing the repository's configuration parameters.
     * @return instance of repository
     * @throws java.io.IOException
     */

    public Repository loadRepository(String fileUri, String repositoryClass, Hashtable<String, String> initParams) throws IOException {
        if(repositoryRegistry == null)
            throw new IOException("JV-000-106: The repository registry has not been set.");

        Hashtable<String, String> props = (initParams != null) ? initParams : new Hashtable<String, String>();

        if (fileUri == null || fileUri.trim().equals(""))
            throw new IOException("JV-000-100: File uri must be provided.");
        if (repositoryClass == null || repositoryClass.trim().equals(""))
            throw new IOException("JV-000-101: Repository class name must be provided: File Name = " + fileUri
                    + ", Properties = " + props.toString());

        InputStream stream = null;
        Repository repository = null;
        try {
            stream = new FileInputStream(fileUri);
        } catch (IOException e) {
            stream = this.getClass().getResourceAsStream(fileUri);
        }
        String message = "File Name = " + fileUri +
                ", Repository Class = " + repositoryClass + ", Properties = " + props.toString();

        if (stream == null)
            throw new IOException("JV-000-102: Unable to loadRegistry config file: " + message);

        try {
            Class<?> c = Class.forName(repositoryClass);
            repository = (Repository) c.newInstance();
            repository.setRepositoryRegistry(repositoryRegistry);
            repository.load(stream, props);

        } catch (IOException e) {
            throw new IOException("JV-000-103: " + e.toString() + " : " + message);
        } catch (Exception e) {
            throw new IOException("JV-000-104: " + e.toString() + " : " + message);
        } catch (Error e) {
            throw new IOException("JV-000-105: " + e.toString() + " : " + message);
        }
        return repository;
    }

    public String getLoaderName() {
        return this.getClass().getName();
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }
}
