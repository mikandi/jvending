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

import java.util.Collection;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.MatchPolicy;

/**
 * Matches a bundle descriptor from the <code>BundleDescriptorRepository</code> by bundle id.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public final class BundleIdMatch implements MatchPolicy {
    //collection of strings denoting ids
    private Collection<String> id;

    public BundleIdMatch(Collection<String> id) {
        this.id = id;
    }

    public float doMatch(BundleDescriptor bd) {
        if (bd == null) return 0f;
        if (id == null) return 1.0f;
        return id.contains(bd.getBundleID()) ? 1.0f : 0f;
    }

}
