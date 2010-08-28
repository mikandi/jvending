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