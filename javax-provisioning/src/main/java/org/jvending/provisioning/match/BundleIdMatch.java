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
