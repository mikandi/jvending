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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class implementing a string matching algorithm.
 *
 * @author Shane Isbell
 * @since 1.2a
 */


public class StringMatcher extends RequirementMatcher {

    private boolean isCaseInsensitive = false;

    private boolean isWildcardsOn = false;

    /**
     * Default constructor
     */
    public StringMatcher() {
        super();
    }

    public boolean getCaseInsensitive() {
        return isCaseInsensitive;
    }

    public boolean getWildcardsOn() {
        return isWildcardsOn;
    }

    public void init(String attributeName, Map<String, String> initParams) throws MatcherException {
        super.init(attributeName, initParams);

        String caseInsensitive = (String) initParams.get("caseInsensitive");
        if (caseInsensitive != null)
            isCaseInsensitive = (caseInsensitive.equals("true"));

        String wildcardsOn = (String) initParams.get("wildcardsOn");
        if (wildcardsOn != null)
            isWildcardsOn = (wildcardsOn.equals("true"));

    }

    protected boolean matchCapability(String reqValue, String capValue) {
        if (reqValue == null) return true;
        if (capValue == null) return false;

        if (isWildcardsOn) {
            Pattern pattern = (isCaseInsensitive) ?
                    Pattern.compile(capValue, Pattern.CASE_INSENSITIVE) :
                    Pattern.compile(capValue);

            Matcher matcher = pattern.matcher(reqValue);
            return matcher.matches();

        } else {
            if (!isCaseInsensitive) return (reqValue.equals(capValue));
            else return (reqValue.equalsIgnoreCase(capValue));
        }
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {
        if (requirement == null) return 1.0f;
        if (capabilities == null || capabilities.size() == 0) return 0.0f;

        for (String s : capabilities) {
            if (matchCapability(requirement, s)) return 1.0f;
        }
        return 0.0f;
    }

}