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