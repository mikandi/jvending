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

package org.jvending.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * This class is a simple facade for <code>java.util.properties</code>. Repositories that use an underlying properties
 * file (name, value pairs) can extend from this class and add additional domain specific methods. If the extending
 * class provides methods for adding additional properties after a loadRegistry, the getValue method may need to be
 * re-implemented to handle synchronization.
 *
<pre>
    RepositoryRegistry.loadFromFile("./sample-config.xml");
    PropertyRepository repository = (PropertyRepository) RepositoryRegistry.find("adapter");
    String value = repository.getValue("myprop");    `
</pre>
 *
 * @author Shane Isbell
 * @since 0.10
 */

public class PropertyRepository implements Repository {

     /**Internal reference for properties*/
    protected Properties properties = new Properties();

    /**Internal reference for repositoryRegistry*/
    protected RepositoryRegistry repositoryRegistry;

    /**
     * Accessor for properties
     * @param name      the name of the property
     * @return String   value for the given name
     */
    public String getValue(String name) {
        return properties.getProperty(name);
    }

    /**
     * @see org.jvending.registry.Repository#load(InputStream inputStream, Hashtable prop)
     */
    public void load(InputStream inputStream, Hashtable<String, String> prop) throws IOException {
        properties.load(inputStream);
    }

    /**
     * Mutator for <code>RepositoryRegistry</code>
     *
     * @param repositoryRegistry
     */
    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        this.repositoryRegistry = repositoryRegistry;
    }
}
