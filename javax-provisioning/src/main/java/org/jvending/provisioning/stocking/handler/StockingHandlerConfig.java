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

import java.util.List;
import java.util.Set;

import org.jvending.provisioning.stocking.DataSink;
import org.jvending.provisioning.stocking.StockingContext;
import org.jvending.provisioning.stocking.StockingPolicy;
import org.jvending.provisioning.stocking.filter.StockingFilter;

/**
 * Contains configuration information for the stocking handler, which is obtained from the stocking-handlers.xml file.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public interface StockingHandlerConfig {

    /**
     * Returns the value of an initialization parameter for the given name.
     * @param name of the parameter
     * @return the value of an initialization parameter for the given name.
     */
    String getInitParameter(String name);

    /**
     * Returns a set of initialization parameter names
     * @return  a set of initialization parameter names
     */
    Set<String> getInitParameterNames();

    /**
     * Returns the <code>StockingContext</code>
     * @return the <code>StockingContext</code>
     */
    StockingContext getStockingContext();

    /**
     * Returns a list of <code>StockingFilter</code>instances.
     * @return  a list of <code>StockingFilter</code>instances.
     */
    List<StockingFilter> getStockingFilters();

    /**
     * Returns a list of <code>DescriptorHandler</code> instances.
     * @return a list of <code>DescriptorHandler</code> instances.
     */
    List<DescriptorHandler> getDescriptorHandlers();

    /**
     * Returns a <code>DataSink</code> that will be used as the sink for thr PAR file.
     * @return a <code>DataSink</code> that will be used as the sink for thr PAR file.
     */
    DataSink getDataSink();

    /**
     * Returns the name of the <code>StockingHandler</code> that this configuration file belongs to.
     * @return the name of the <code>StockingHandler</code> that this configuration file belongs to.
     */
    String getStockingHandlerName();

    /**
     * Returns a <code>StockingPolicy</code> for the content contained within the PAR file. Content refers to the
     * primary content bundle that is to be delivered to the user, not meta-data associated with that content.
     * @return a <code>StockingPolicy</code> for the content contained within the PAR file.
     */
    StockingPolicy getContentPolicy();

    /**
     * Returns a <code>StockingPolicy</code> for the descriptor files contained withing the PAR file. Typically, this
     * will be a policy for how to handle a JAD or JNLP file, but may also include custom descriptor types.
     * @return a <code>StockingPolicy</code> for the descriptor files contained withing the PAR file.
     */
    StockingPolicy getDescriptorPolicy();

    /**
     * Returns a <code>StockingPolicy</code> for the icons contained with the PAR file.
     * @return a <code>StockingPolicy</code> for the icons contained with the PAR file.
     */
    StockingPolicy getIconPolicy();

    /**
     * Returns a <code>StockingPolicy</code> for the previews contained with the PAR file.
     * @return a <code>StockingPolicy</code> for the previews contained with the PAR file.
     */
    StockingPolicy getPreviewPolicy();

    /**
     * Returns a <code>StockingPolicy</code> for the copyrights contained with the PAR file. Copyrights can be an icon or text,
     * so they need to be managed as any other binary. Sometimes the copyright is written directly within the
     * provisioning.xml file, in which case this policy will be ignored.
     * @return a <code>StockingPolicy</code> for the copyrights contained with the PAR file.
     */
    StockingPolicy getCopyrightPolicy();

}
