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
 * Class implementing a dimension match. E.g. HardwarePlatform.ScreenSize.
 *
 * @author Shane Isbell
 * @since 1.2a
 */


public class DimensionMatcher extends RequirementMatcher {

    /**
     * Default constructor
     */
    public DimensionMatcher() {
        super();
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {
        if (capabilities == null || capabilities.size() == 0) return 0.0f;
        if (requirement == null) return 1.0f;
        int index = requirement.indexOf("x");
        if (index == -1) return 0.0f;

        String reqWidth = requirement.substring(0, index - 1).trim();
        String reqH = requirement.substring(index + 1).trim();

        float max = 0f;
        for (String capabilityValue : capabilities) {
            max = Math.max(matchCapability(reqWidth, reqH, capabilityValue), max);
        }
        return max;
    }

    private float matchCapability(String reqWidth, String reqH, String capValue) {
        if (reqWidth == null || reqH == null) return 1f;
        if (capValue == null) return 0f;

        int index = capValue.indexOf("x");
        String capWidth = capValue.substring(0, index).trim();
        String capH = capValue.substring(index + 1).trim();

        float hasWidthMatch = matchDimension(reqWidth, capWidth);
        float hasHeightMatch = matchDimension(reqH, capH);

        if (hasWidthMatch == 0 || hasHeightMatch == 0) return 0f;
        return (hasWidthMatch + hasHeightMatch) / 2;
    }

    private int matchState(String reqValue, String capValue) {
        int state = 0;
        int hasReqPlus = reqValue.indexOf("+");
        int hasCapPlus = capValue.indexOf("+");

        if (hasReqPlus != -1 && hasCapPlus != -1 || hasReqPlus != -1 && hasCapPlus == -1)
            state = 0;
        else if (hasReqPlus == -1 && hasCapPlus == -1)
            state = 1;
        return state;

        //+, +
        //exact, +
        //+, exact
        //exact, exact
    }

    private float matchDimension(String reqValue, String capValue) {
        if (reqValue == null && capValue == null) return 0f;

        int state = matchState(reqValue, capValue);
        switch (state) {
            case 0:
                reqValue = reqValue.replace('+', ' ').trim();
                capValue = capValue.replace('+', ' ').trim();
                try {
                    float req = Float.parseFloat(reqValue);
                    float cap = Float.parseFloat(capValue);
                    if (cap < req) return 0f;
                    return (req / cap);
                } catch (NumberFormatException e) {
                    return 0f;
                }
            case 1:
                return (reqValue.equals(capValue)) ? 1f : 0f;
        }
        return 0f;
    }
}