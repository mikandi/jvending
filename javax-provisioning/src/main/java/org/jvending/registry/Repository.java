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

/**
 * The interface for repositories managed by the <code>RepositoryRegistry</code>. The implementing classes should
 * provide the methods for accessing the domain specific data.
 *
 * @author Shane Isbell
 * @since 0.10
 */

public interface Repository {

    /**
     * Loads the configuration file and configuration properties. In the case below, the <code>inputStream</code>
     * contains the adapters.txt file and the <code>properties</code> holds the init-params. The init params should be
     * used to specialize the repository configuration.  The example below shows that you can add new properties
     * to <code>MyRepository</code> but not delete them.
     * <pre>
&lt;registry-config&gt;
   &lt;repositories&gt;
      &lt;repository&gt;
         &lt;repository-name&gt;adapter&lt;/repository-name&gt;
         &lt;repository-class&gt;org.jvending.sample.MyRepository&lt;/repository-class&gt;
         &lt;repository-config&gt;${basedir}/adapters.txt&lt;/repository-config&gt;
	    &lt;init-param&gt;
		    &lt;param-name&gt;add&lt;/param-name&gt;
		    &lt;param-value&gt;true&lt;/param-value&gt;
	    &lt;/init-param&gt;
        &lt;init-param&gt;
		    &lt;param-name&gt;delete&lt;/param-name&gt;
		    &lt;param-value&gt;false&lt;/param-value&gt;
	    &lt;/init-param&gt;
      &lt;/repository&gt;
    &lt;/repositories&gt;
&lt;/registry-config&gt;
     </pre>
     *
     * Since this method uses an <code>InputStream</code> parameter, the configuration file can be loaded off of the
     * local file system or from a specific URL located at an HTTP address.
     *
     * @param inputStream       the configuration file
     * @param properties        the properties used to configure the repository
     * @exception IOException   thrown on interrupted I/O. Implementing class may also use this exception to throw
     *                          other exceptions like invalid properties.
     */
    void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException;

    /**
     * 
     * @param repositoryRegistry
     */
    void setRepositoryRegistry(RepositoryRegistry repositoryRegistry);

}