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

package org.jvending.provisioning.stocking;

import java.util.List;

/**
 * Provides the catalog policy for stocking content from a PAR file.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public interface StockingPolicy {

   /**
    * The maximum size (KB) of a stockable piece of content that is remotely accessible. 
    */
    int getRemoteMaxSize();

   /**The maximum size (KB) of a stockable piece of content within a PAR file*/
    int getLocalMaxSize();

    /**
     * Is allowed to fetch content located outside of the PAR file.
     * @return boolean
     */
    boolean hasFetchContent();

    /**
     * Accessor for list of accepted mime-types
     * @return list of accepted mime-types
     */
    List<String> getMimeAccept();

    /**
     * Accessor for list of blocked mime-types
     * @return list of blocked mime-types
     */
    List<String> getMimeBlock();

    /**
     * Accessor for list of accepted URLs and IPs.
     * @return list of accepted URLs and IPs.
     */
    List<String> getWhiteList();

    /**
     * Accessor for list of blocked URLs and IPs
     * @return list of blocked URLs and IPs
     */
    List <String>getBlackList();

}