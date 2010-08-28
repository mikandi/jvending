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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jvending.provisioning.config.stockinghandlers.DescriptorHandlerType;
import org.jvending.provisioning.config.stockinghandlers.DescriptorHandlersType;
import org.jvending.provisioning.config.stockinghandlers.InitParamType;
import org.jvending.provisioning.config.stockinghandlers.StockingFiltersType;
import org.jvending.provisioning.config.stockinghandlers.StockingHandlerType;
import org.jvending.provisioning.config.stockinghandlers.StockingPolicyType;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */

public final class StockingHandlerRepository implements Repository {

 //   private StockingHandlerType stockingHandlerType;

    private List<String> stockingFilterNames;

    private List<DescriptorHandlerType> descriptorHandlers;

    private List<InitParamType> initParams;

    private StockingPolicyType stockingPolicy;

    private String dataSinkName;

    StockingHandlerRepository(StockingHandlerType stockingHandlerType) {
      //  this.stockingHandlerType = stockingHandlerType;

        StockingFiltersType stockingFiltersType = stockingHandlerType.getStockingFilters();
        if (stockingFiltersType != null) stockingFilterNames = stockingFiltersType.getStockingFilter();

        initParams = stockingHandlerType.getInitParam();

        stockingPolicy = stockingHandlerType.getStockingPolicy();

        DescriptorHandlersType descriptorHandlersType = stockingHandlerType.getDescriptorHandlers();
        descriptorHandlers = (descriptorHandlersType != null) ? descriptorHandlersType.getDescriptorHandler() :
                new ArrayList<DescriptorHandlerType>();

        dataSinkName = stockingHandlerType.getDataSink();
    }

    public List<String> getStockingFilterNames() {
        return stockingFilterNames;
    }

    public List<DescriptorHandlerType> getDescriptorHandlers() {
        return descriptorHandlers;
    }

    public String getDataSinkName() {
        return dataSinkName;
    }

    public StockingPolicyType getStockingPolicyType() {
        return stockingPolicy;
    }

    public List<InitParamType> getInitParams() {
        return initParams;
    }

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }
}
