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
import java.util.Map;

/**
 * This abstract class handles the aggregation of the matching operations of each individual requirement value
 * against the list of capability values. If allMustMatch is true, then all the requirement values must be matched, so
 * the result is the product of the individual match results. Otherwise, the result is the maximum of the individual
 * match results.
 *
 * @author Shane Isbell
 * @since 1.2a
 */


public abstract class RequirementMatcher implements AttributeMatcher {

    String attributeName;

    Map<String, String> initParams;

    private boolean isAllMustMatch = false;

    /**
     * Default constructor
     */
    public RequirementMatcher() {
    }

    public boolean getAllMustMatch() {
        return isAllMustMatch;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void init(String attributeName, Map<String, String> initParams) throws MatcherException {
        this.attributeName = attributeName;
        this.initParams = initParams;
        String allMustMatch = (String) initParams.get("allMustMatch");

        if (allMustMatch != null)
            isAllMustMatch = (allMustMatch.equals("true"));
    }

    public float match(List<String> requirements, List<String> capabilities) throws MatcherException {
        if (requirements == null || requirements.size() == 0) return 1.0F;
        if (capabilities == null || capabilities.size() == 0) return 0.0F;

        float matchValue = 1f;

        for (String requirementName : requirements) {
            float f = matchRequirement(requirementName, capabilities);
            if (isAllMustMatch) {//All Requirements match
                matchValue = matchValue * f;
            } else {
                matchValue = (matchValue - 1 > f) ? matchValue : f;
            }
        }
        return matchValue;
    }

    protected abstract float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException;
}