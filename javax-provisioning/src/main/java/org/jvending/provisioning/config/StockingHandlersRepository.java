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
package org.jvending.provisioning.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.jvending.provisioning.config.stockinghandlers.StockingHandlerType;
import org.jvending.provisioning.config.stockinghandlers.StockingHandlersType;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.jaxb.JaxbConfiguration;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */

public final class StockingHandlersRepository implements Repository {

    private StockingHandlersType stockingHandlers;

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
        if (properties == null)
            throw new IOException("JV-1305-001: Null properties values for the StockingHandlers repository");
        if (inputStream == null)
            throw new IOException("JV-1305-002: Null inputstream for the StockingHandlers repository");

        String _package = (String) properties.get("binding-package");
        stockingHandlers = (StockingHandlersType) JaxbConfiguration.parse(inputStream, _package);
    }

    public StockingHandlerRepository getStockingHandlerRepository(String name) {
        for (StockingHandlerType stockingHandler : stockingHandlers.getStockingHandler()) {
            if (stockingHandler.getStockingHandlerName().equals(name))
                return new StockingHandlerRepository(stockingHandler);
        }
        return null;
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }
}
