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
 * Class implementing an integer match. E.g. HardwarePlatform.BitsPerPixel. The requirement value is either
 * an integer, which must be matched exactly, or an integer with a plus sign suffix, which means that any
 * larger value is acceptable. Initialization parameters are the same as for RequirementMatcher.
 *
 * @author Shane Isbell
 * @since 1.2a
 */

public class IntegerMatcher extends RequirementMatcher {

    /**
     * Default constructor
     */
    public IntegerMatcher() {
        super();
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {
        if (capabilities == null || capabilities.size() == 0) return 0.0f;
        if (requirement == null) return 1.0f;

        for (String capabilityValue : capabilities) {
            if (matchCapability(requirement, capabilityValue)) return 1.0f;
        }
        return 0.0f;
    }

    private boolean matchCapability(String reqValue, String capValue) {
        if (reqValue == null) return true;
        if (capValue == null) return false;

        if (reqValue.indexOf("+") != -1) {
            reqValue = reqValue.replace('+', ' ').trim();
            try {
                Integer req = new Integer(reqValue.trim());
                Integer cap = new Integer(capValue);
                int compare = cap.compareTo(req);
                return (compare >= 0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return (reqValue.equals(capValue)); //equal
        }
    }
}