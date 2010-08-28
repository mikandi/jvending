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