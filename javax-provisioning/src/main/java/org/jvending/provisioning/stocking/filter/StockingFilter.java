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

/**
 * Provides a service for filtering content and modifying the provisioning archive descriptor.
 * @author Shane Isbell
 * @since 1.3a
 */

public interface StockingFilter {

    /**
     * Uses the <code>FilterTask</code> to filter the content and to modify the provisioning archive descriptor.
     * This method may also choose to use <code>StockingComponent.handleStockingEvent()</code> to notify the
     * provisioning framework of any modifications to the PAR file.
     * @param filterTask
     */
    void doFilter(FilterTask filterTask);

}