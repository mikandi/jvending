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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.jvending.provisioning.config.stockinghandlers.DescriptorHandlerType;
import org.jvending.provisioning.stocking.handler.DescriptorHandler;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.registry.RepositoryRegistry;

/**
 *  Provides functionality for handling the stocking process.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public abstract class StockingComponent {

    private static Logger logger = Logger.getLogger("StockingComponent");

    private StockingContext stockingContext;

    /**
     *
     * @param stockingEvent the stocking event to handle
     * @throws StockingException
     */
    public void handleStockingEvent(StockingEvent stockingEvent) throws StockingException {
        if(stockingEvent == null) throw new StockingException("JV-1802-006: StockingEvent is null");
        logger.info("JV-1802-001: Stocking Event = " + stockingEvent.toString());
    }

    /**
     *
     * @param handlerName
     * @param mimeType  the mime-type. This value should not be null.
     * @return DescriptorHandler - should not be null
     * @throws StockingException
     */
    public DescriptorHandler createDescriptorHandler(String handlerName, String mimeType) throws StockingException {
        if(stockingContext == null) throw new StockingException("JV-1802-002: Could not find the StockingContext");

        StockingHandlerConfig stockingHandlerConfig =
                stockingContext.getStockingHandler(handlerName).getStockingHandlerConfig();

        if(mimeType == null) {
            logger.info("JV-1802-003: Unable to get descriptor handler: Mime-Type is null");
            throw new StockingException("JV-1802-003: Unable to get descriptor handler: Mime-Type is null");
        }

        List descriptorHandlers = stockingHandlerConfig.getDescriptorHandlers();

        String descriptorHandlerClassName = null;

        for(Iterator i = descriptorHandlers.iterator(); i.hasNext(); ) {
            DescriptorHandlerType descriptorHandlerType = (DescriptorHandlerType) i.next();
            String handlerMimeType = descriptorHandlerType.getMimeType();
            if(handlerMimeType != null && mimeType.trim().equals(handlerMimeType.trim())) {
                descriptorHandlerClassName = descriptorHandlerType.getDescriptorHandlerClass().trim();
            }
        }

        try {
            Class<?> c = Class.forName(descriptorHandlerClassName);
            org.jvending.provisioning.stocking.handler.DescriptorHandler handler =
                    (org.jvending.provisioning.stocking.handler.DescriptorHandler) c.newInstance();
            RepositoryRegistry repositoryRegistry = (RepositoryRegistry)
                    stockingContext.getServletContext().getAttribute("org.jvending.registry.RepositoryRegistry");

            handler.setRepositoryRegistry(repositoryRegistry);
            logger.info("Created Descriptor Handler: " + descriptorHandlerClassName);
            return handler;
        } catch(Exception e) {
            e.printStackTrace();
            throw new StockingException("JV-1802-004: Could not instantiate Descriptor Handler: Name = "
                    + descriptorHandlerClassName + ", Mime-Type = " + mimeType);
        }
    }

    /**
     * Called by the provisioning framework when it puts the StockingComponent into service.
     *
     * @param stockingContext the stocking context
     */
    public void init(StockingContext stockingContext) {
        this.stockingContext = stockingContext;
    }

    /**
     * Called by the provisioning framework when it takes the StockingComponent out of service.
     */
    public void destroy() {
        logger.info("JV-1802-005: Destroying StockingComponent");
    }

}
