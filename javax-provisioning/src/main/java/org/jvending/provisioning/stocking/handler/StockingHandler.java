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

package org.jvending.provisioning.stocking.handler;

import java.io.IOException;
import java.io.InputStream;

import org.jvending.provisioning.stocking.ProviderContext;

/**
 * Provides a way to preprocess PAR files prior to stocking them within the <code>BundleRepository</code>. The developer
 * is required to extend the class and implement the <code>addParFile</code> method. For the provisioning framework
 * to use this handler, a reference must be defined within the stocking-handlers.xml file. This is the source for
 * stocking PAR files, which is then followed by the <code>StockingFilter</code>s preprocessing and ends
 * with the <code>DataSink</code>.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public abstract class StockingHandler {

    protected StockingHandlerConfig stockingHandlerConfig;

    /**
     * Preprocesses a PAR file.
     *
     * @param inputStream PAR file
     * @return parId - this will be parId that is returned from the underlying <code>DataSink</code>, which is
     * typically the <code>BundleRepository</code>
     * @throws IOException
     */
    public abstract long addParFile(InputStream inputStream, ProviderContext providerContext) throws IOException;

    /**
     * Called when the provisioning framework takes the handler out of service.
     *
     * @throws StockingHandlerException
     */
    public void destroy() throws StockingHandlerException {

    }

    /**
     *  Called when the provisioning framework puts the handler in service.
     *
     * @param stockingHandlerConfig
     * @throws StockingHandlerException
     */
    public void init(StockingHandlerConfig stockingHandlerConfig) throws StockingHandlerException {
        this.stockingHandlerConfig = stockingHandlerConfig;
    }

    /**
     * Accessor for <code>StockingHandlerConfig</code>
     *
     * @return <code>StockingHandlerConfig</code>
     */
    public StockingHandlerConfig getStockingHandlerConfig() {
        return stockingHandlerConfig;
    }


}
