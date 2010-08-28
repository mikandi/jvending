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