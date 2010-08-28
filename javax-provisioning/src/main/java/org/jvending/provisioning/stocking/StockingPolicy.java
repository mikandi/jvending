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