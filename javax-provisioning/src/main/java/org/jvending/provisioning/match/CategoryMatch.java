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
import javax.provisioning.MatchPolicy;


/**
 * Matches a bundle descriptor from the <code>BundleDescriptorRepository</code> by bundle category.
 *
 * @author Shane Isbell
 * @since 1.3.9a
 */

public final class CategoryMatch implements MatchPolicy {

    private List<String> categories;

    public CategoryMatch(List<String> categories) {
        this.categories = categories;
    }

    public float doMatch(BundleDescriptor bundleDescriptor) {
        if (bundleDescriptor == null) return 0f;
        if (categories == null || categories.size() == 0) return 1.0f;

        String category = bundleDescriptor.getCatalogProperty("Category");
        if (category == null || category.equals("")) category = "unspecified";

        for (String categoryToMatch : categories) {
            if (category.equals(categoryToMatch)) {
                return 1.0f;
            }
        }
        return 0.0f;
    }

}