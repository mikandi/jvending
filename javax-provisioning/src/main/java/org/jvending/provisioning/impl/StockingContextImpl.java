/*
 *  JVending
 *  Copyright (C) 2006  Shane Isbell
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
package org.jvending.provisioning.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.jvending.provisioning.config.StockingHandlerRepository;
import org.jvending.provisioning.config.StockingHandlersRepository;
import org.jvending.provisioning.config.stockinghandlers.DescriptorHandlerType;
import org.jvending.provisioning.config.stockinghandlers.InitParamType;
import org.jvending.provisioning.config.stockinghandlers.PolicyType;
import org.jvending.provisioning.config.stockinghandlers.StockingPolicyType;
import org.jvending.provisioning.stocking.DataSink;
import org.jvending.provisioning.stocking.ProviderContext;
import org.jvending.provisioning.stocking.StockingComponent;
import org.jvending.provisioning.stocking.StockingContext;
import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.StockingPolicy;
import org.jvending.provisioning.stocking.filter.StockingFilter;
import org.jvending.provisioning.stocking.handler.StockingHandler;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.provisioning.stocking.handler.StockingHandlerException;
import org.jvending.registry.RepositoryRegistry;

/**
 * Provides an implementation of the <code>StockingContext</code>.
 *
 * @author Shane Isbell
 */
final class StockingContextImpl implements StockingContext {

    private static Logger logger = Logger.getLogger("StockingComponent");

    private ServletContext servletContext;

    private RepositoryRegistry repositoryRegistry;

    private StockingHandlersRepository stockingHandlersRepository;

    private StockingComponent stockingComponent;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void destroy() {
        servletContext.removeAttribute("org.jvending.provisioning.stocking.StockingContext");
    }

    public ProviderContext getProviderContext(HttpServletRequest request) {
        String id = request.getHeader("provider-id");
        return new ProviderContextImpl(id);
    }

    public void init(ServletContext servletContext) {
        stockingComponent = new StockingComponentImpl();
        stockingComponent.init(this);
        this.servletContext = servletContext;
        repositoryRegistry =
                (RepositoryRegistry) servletContext.getAttribute("org.jvending.registry.RepositoryRegistry");
        stockingHandlersRepository = (StockingHandlersRepository) repositoryRegistry.find("stocking-handlers");
        servletContext.setAttribute("org.jvending.provisioning.stocking.StockingContext", this);
    }

    public StockingHandler getStockingHandler(String name) throws StockingException {
        if (name == null) throw new StockingException("Can not create a StockingHandler for Name = null");
        StockingHandler stockingHandler = new StockingHandlerImpl();
        StockingHandlerConfig stockingHandlerConfig = getStockingConfig(name);
        if (stockingHandlerConfig == null)
            throw new StockingException("StockingHandlerConfig is null: Name = " + name);

        try {
            stockingHandler.init(stockingHandlerConfig);
        } catch (StockingHandlerException e) {
            e.printStackTrace();
            throw new StockingException("Failed to initialize the StockingHandler: Name = " + name);
        }
        return stockingHandler;

    }

    public StockingComponent getStockingComponent() {
        return stockingComponent;
    }

    private StockingHandlerConfig getStockingConfig(String configName) throws StockingException {
        if (stockingHandlersRepository == null) throw new StockingException("Can not find StockingHandlersRepository ");

        StockingHandlerRepository stockingHandlerRepository =
                stockingHandlersRepository.getStockingHandlerRepository(configName);
        if (stockingHandlerRepository == null)
            throw new StockingException("Can not find StockingHandlerRepository: Name = " + configName);

        List<InitParamType> initParams = stockingHandlerRepository.getInitParams();
        List<String> filterNames = stockingHandlerRepository.getStockingFilterNames();
        StockingPolicyType stockingPolicyType = stockingHandlerRepository.getStockingPolicyType();
        List<DescriptorHandlerType> descriptorHandlers = stockingHandlerRepository.getDescriptorHandlers();
        DataSink dataSink = StockingFactory.createDataSink(stockingHandlerRepository.getDataSinkName());
        if (dataSink == null) throw new StockingException("Could not create the DataSink: Name = "
                + stockingHandlerRepository.getDataSinkName());

        String handlerName = configName;

        HashMap<String, String> initParamMap = new HashMap<String, String>();
        if (initParams != null && initParams.size() > 0) {
            for (InitParamType initParamType : initParams ) {
                String name = initParamType.getParamName();
                String value = initParamType.getParamValue();
                initParamMap.put(name, value);
            }
        }

        if (stockingPolicyType != null) {
          //  PolicyType globalPolicyType = stockingPolicyType.getGlobalPolicy();
            PolicyType previewPolicyType = stockingPolicyType.getPreviewPolicy();
            PolicyType iconPolicyType = stockingPolicyType.getIconPolicy();
            PolicyType descriptorPolicyType = stockingPolicyType.getDescriptorPolicy();
            PolicyType contentPolicyType = stockingPolicyType.getContentPolicy();
            PolicyType copyrightPolicyType = stockingPolicyType.getCopyrightPolicy();

         //   StockingPolicy globalPolicy = StockingPolicyFactory.createStockingPolicy(globalPolicyType);
            StockingPolicy previewPolicy = StockingPolicyFactory.createStockingPolicy(previewPolicyType);
            StockingPolicy iconPolicy = StockingPolicyFactory.createStockingPolicy(iconPolicyType);
            StockingPolicy descriptorPolicy = StockingPolicyFactory.createStockingPolicy(descriptorPolicyType);
            StockingPolicy contentPolicy = StockingPolicyFactory.createStockingPolicy(contentPolicyType);
            StockingPolicy copyrightPolicy = StockingPolicyFactory.createStockingPolicy(copyrightPolicyType);

            StockingHandlerConfig config = new StockingHandlerConfigImpl(contentPolicy, descriptorPolicy, iconPolicy, previewPolicy,
                    copyrightPolicy, initParamMap, handlerName, this, descriptorHandlers, filterNames, dataSink);
            dataSink.init(config);
            return config;
        }

        StockingHandlerConfig config = new StockingHandlerConfigImpl(null, null, null, null, null, initParamMap,
                handlerName, this, descriptorHandlers, filterNames, dataSink);
        dataSink.init(config);
        return config;
    }

    private static class StockingComponentImpl extends StockingComponent {
    }

    private static class ProviderContextImpl implements ProviderContext {

        private String id;

        ProviderContextImpl(String id) {
            this.id = id;
        }

        public String getUser() {
            return id;
        }
    }

    private static class StockingHandlerConfigImpl implements StockingHandlerConfig {

        private final StockingPolicy contentPolicy;

        private final StockingPolicy descriptorPolicy;

        private final StockingPolicy iconPolicy;

        private final StockingPolicy previewPolicy;

        private final StockingPolicy copyrightPolicy;

        private final String handlerName;

        private final StockingContext stockingContext;

        private final List<DescriptorHandlerType> descriptorHandlers;

        private final Map<String, String> initParam;

        private final List<String> filterNames;

        private final DataSink dataSink;

        StockingHandlerConfigImpl(StockingPolicy contentPolicy,
                                  StockingPolicy descriptorPolicy,
                                  StockingPolicy iconPolicy,
                                  StockingPolicy previewPolicy,
                                  StockingPolicy copyrightPolicy,
                                  Map<String, String> initParam,
                                  String handlerName,
                                  StockingContext stockingContext,
                                  List<DescriptorHandlerType> descriptorHandlers,
                                  List<String> filterNames,
                                  DataSink dataSink) {
            this.dataSink = dataSink;
            this.contentPolicy = contentPolicy;
            this.descriptorPolicy = descriptorPolicy;
            this.iconPolicy = iconPolicy;
            this.previewPolicy = previewPolicy;
            this.copyrightPolicy = copyrightPolicy;
            this.initParam = initParam;
            this.handlerName = handlerName;
            this.stockingContext = stockingContext;
            this.descriptorHandlers = descriptorHandlers;
            this.filterNames = filterNames;
        }

        public List getDescriptorHandlers() {
            return descriptorHandlers;
        }

        public StockingContext getStockingContext() {
            return stockingContext;
        }

        public DataSink getDataSink() {
            return dataSink;
        }

        public StockingPolicy getContentPolicy() {
            return contentPolicy;
        }

        public StockingPolicy getDescriptorPolicy() {
            return descriptorPolicy;
        }

        public StockingPolicy getIconPolicy() {
            return iconPolicy;
        }

        public StockingPolicy getPreviewPolicy() {
            return previewPolicy;
        }

        public StockingPolicy getCopyrightPolicy() {
            return copyrightPolicy;
        }

        public String getInitParameter(String name) {
            return (String) initParam.get(name);
        }

        public Set<String> getInitParameterNames() {
            Set<String> set = initParam.keySet();
            return (set == null) ? Collections.unmodifiableSet(new HashSet<String>()) : Collections.unmodifiableSet(set);
        }

        public String getStockingHandlerName() {
            return handlerName;
        }

        public List<StockingFilter> getStockingFilters() {
            if (filterNames == null) return null;
            List<StockingFilter> filters = new ArrayList<StockingFilter>();

            for (String className : filterNames) {
                try {
                    Class<?> c = Class.forName(className);
                    StockingFilter stockingFilter = (StockingFilter) c.newInstance();
                    filters.add(stockingFilter);
                    logger.fine("JV-000-020: Added filter to list: " + className);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "JV-000-021: Could not add filter: " + className, e);
                }
            }
            return filters;
        }
    }
}

