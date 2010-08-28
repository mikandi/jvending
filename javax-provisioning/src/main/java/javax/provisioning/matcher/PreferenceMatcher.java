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

package javax.provisioning.matcher;

import java.util.List;

/**
 * <p>Class implementing a preference match. For example, this may be used for matching SoftwarePlatform.AcceptLanguage,
 * where the capability represents the locales acceptable to the client, in order of preference.</p>
 * <p/>
 * <p> Note that a PreferenceMatcher is like a StringMatcher except that on a successful match it may return a match
 * value greater than 0.0 and less than 1.0. In particular, the initialization parameters for a PreferenceMatcher are
 * the same as for a StringMatcher.</p>
 *
 * @author Shane Isbell
 * @since 1.2a
 */

public class PreferenceMatcher extends StringMatcher {

    /**
     * Default constructor
     */
    public PreferenceMatcher() {
        super();
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {
        if (requirement == null) return 1.0f;
        if (capabilities == null || capabilities.size() == 0) return 0.0f;

        float counter = 0f;
        for (String capabilityValue : capabilities) {
            counter++;
            if (matchCapability(requirement, capabilityValue)) return 1f / counter;
        }
        return 0.0f;
    }
}