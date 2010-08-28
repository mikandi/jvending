/*
 *  JVending
 *  Copyright (C) 2006  Shane Isbell
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

package org.jvending.provisioning.dao;

import java.util.List;

import javax.provisioning.BundleDescriptor;

/**
 * Provides services for retrieving bundle descriptors.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public interface BundleDescriptorDAO {

    BundleDescriptor getBundleDescriptorFor(String id);

    List<BundleDescriptor> getBundleDescriptors();
    
    List<BundleDescriptor> getBundleDescriptorsByEventId(long eventId);
}
