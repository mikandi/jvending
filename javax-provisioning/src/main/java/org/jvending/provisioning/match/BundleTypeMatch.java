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

package org.jvending.provisioning.match;

import java.util.List;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.BundleType;
import javax.provisioning.MatchPolicy;

/**
 * Matches a bundle descriptor from the <code>BundleDescriptorRepository</code> by bundle type (Wallpaper, Application,
 * etc).
 *
 * @author Shane Isbell
 * @since 1.3.9a
 */

public final class BundleTypeMatch implements MatchPolicy {

    private List<String> bundleTypes;

    //List of Strings
    public BundleTypeMatch(List<String> bundleTypes) {
        this.bundleTypes = bundleTypes;
    }

    public float doMatch(BundleDescriptor bundleDescriptor) {
        if (bundleDescriptor == null) return 0f;
        if (bundleTypes == null || bundleTypes.size() == 0) return 1.0f;

        BundleType bundleType = bundleDescriptor.getBundleType();

        for (String bundleTypeToMatch : bundleTypes) {
            if (bundleType.toString().equals(bundleTypeToMatch)) {
                return 1.0f;
            }
        }
        return 0.0f;
    }

}