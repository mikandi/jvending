/*
 *  JVending
 *  Copyright (C) 2004  Shane Isbell
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

package org.jvending.provisioning.stocking.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jvending.provisioning.stocking.ContentVerificationException;
import org.jvending.provisioning.stocking.StockingComponent;
import org.jvending.provisioning.stocking.StockingEvent;
import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

/**
 * Base class for filtering out content from the PAR file. The extending class will provide the logic on which
 * <code>ClientBundle</code> to filter.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public abstract class BundleFilter implements StockingFilter {

    private static Logger logger = Logger.getLogger("BundleFilter");

    protected FilterTask filterTask;

    public void doFilter(FilterTask filterTask) {
        this.filterTask = filterTask;
        
        ProvisioningArchiveType archive = filterTask.getProvisioningArchive();
        Map<String, byte[]> contentMap = filterTask.getContent();

        List<ClientBundleType> bundlesToRemove = new ArrayList<ClientBundleType>();
        List<ClientBundleType> clientBundles = archive.getClientBundle();

        StockingComponent stockingComponent =
                filterTask.getStockingHandlerConfig().getStockingContext().getStockingComponent();

        for (ClientBundleType clientBundle : clientBundles) {
            try {
                validateBundle(clientBundle, contentMap);
            } catch (StockingException e) {
                bundlesToRemove.add(clientBundle);
                logger.log(Level.INFO,
                        "JV-1801-000: Removing content bundle from catalog: Content Id = " +
                                clientBundle.getContentId(), e.getMessage());
                try {
                    StockingEvent event = new StockingEvent(filterTask.getProviderContext(),
                            filterTask.getFilterID(), StockingEvent.REMOVED,
                            "JV-1801-000: Removing content bundle from catalog: Content Id = "
                                    + clientBundle.getContentId() + ":" + e.getMessage());
                    stockingComponent.handleStockingEvent(event);
                } catch (StockingException e1) {
                    logger.info("Unable to handle this StockingEvent");
                }
            }
        }
        clientBundles.removeAll(bundlesToRemove);
    }

    public abstract void validateBundle(ClientBundleType clientBundle, Map<String, byte[]> contentMap)
            throws ContentVerificationException, StockingException;

}