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
package javax.provisioning;

/**
 * Gives the provisioning developer a view onto the properties of an individual provisioning adapter.
 *
 * @author Shane Isbell
 */

public interface AdapterInfo {

    /**
     * Returns the adapter name.
     *
     * @return the adapter name
     */
    String getAdapterName();

    /**
     * Returns the default duration in milliseconds for fulfillment tasks which use this adapter.
     *
     * @return the default duration in milliseconds for fulfillment tasks which use this adapter
     */
    long getDefaultFulfillmentDuration();

    /**
     * Returns the descriptor file extension, or null if this adapter does not define a descriptor file extension.
     *
     * @return the descriptor file extension, or null if this adapter does not define a descriptor file extension
     */
    String getDescriptorFileExtension();

    /**
     * Returns the descriptor file mime type, or null if this adapter does not define a descriptor file extension.
     *
     * @return the descriptor file mime type, or null if this adapter does not define a descriptor file extension
     */
    String getDescriptorFileMimeType();

}