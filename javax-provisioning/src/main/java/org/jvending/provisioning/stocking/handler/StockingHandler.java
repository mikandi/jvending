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
