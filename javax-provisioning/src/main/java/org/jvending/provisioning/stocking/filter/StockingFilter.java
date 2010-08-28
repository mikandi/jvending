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