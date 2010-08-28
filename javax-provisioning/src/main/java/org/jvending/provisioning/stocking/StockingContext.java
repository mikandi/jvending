/*
 *   JVending
 *   Copyright (C) 2006  Shane Isbell
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
