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
package org.jvending.provisioning.stocking;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jvending.provisioning.stocking.handler.StockingHandler;

/**
 * Provides stocking services for the provisioning framework. There is only one instance of this class within the
 * framework. It is stored as a <code>ServletContext</code> attribute with the name
 * "org.jvending.provisioning.stocking.StockingContext"
 *
 * @author Shane Isbell
 * @since 2.0.0
 */

public interface StockingContext {

    /**
     * Accessor for the <code>ServletContext</code>
     *
     * @return <code>ServletContext</code>
     */
    ServletContext getServletContext();

    /**
     * Accessor for the <code>StockingHandler</code>
     *
     * @param name of the <code>StockingHandler</code>. It matches with the stocking-handler-name tag within the
     * stocking-handlers.xml file.
     * @return <code>StockingHandler</code>
     */
    StockingHandler getStockingHandler(String name) throws StockingException;

    /**
     * Accessor for the <code>StockingComponent</code>
     *
     * @return <code>StockingComponent</code>
     */
    StockingComponent getStockingComponent();

    /**
     * Called by the provisioning framework when the <code>StockingContext</code> is put into service.
     *
     * @param servletContext
     */
    void init(ServletContext servletContext);

    /**
     *  Called when the provisioning framework takes the <code>StockingContext</code> out of service.
     */
    void destroy();

    /**
     * Obtains a <code>ProviderContext</code>
     *
     * @param request
     * @return <code>ProviderContext</code>
     */
    ProviderContext getProviderContext(HttpServletRequest request);

}
