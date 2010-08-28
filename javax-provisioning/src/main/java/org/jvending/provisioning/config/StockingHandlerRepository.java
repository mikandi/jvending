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
