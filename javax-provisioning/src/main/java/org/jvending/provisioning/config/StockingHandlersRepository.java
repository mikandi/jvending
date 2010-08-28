/*
 *  JVending
 *  Copyright (C) 2005  Shane Isbell
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
