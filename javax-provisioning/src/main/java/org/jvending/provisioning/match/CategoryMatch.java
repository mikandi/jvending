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