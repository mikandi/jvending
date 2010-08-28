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