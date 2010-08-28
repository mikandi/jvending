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
 * <p>Interface implemented by all the matcher classes. Each instance of an implementing class represents a matching
 * algorithm for a particular requirement/capability attribute, such as a UAProf attribute. The WEB-INF/
 * matchers.xml file is used to configure the AttributeMatcher instance for each attribute that is supported by the
 * provisioning framework.</p>
 * <p/>
 * <p>Each implementing class must have a no-argument constructor. Instances of these classes are created by the
 * provisioning framework based on matcher configuration information as defined in the Matchers. The configuration
 * information may include initialization parameters. Immediately after creating an instance, the framework calls
 * its init(Map) method, passing in the parameters in the form of an unmodifiable map whose keys are the parameter
 * names and whose values are the parameter values.</p>
 * <p/>
 * <p>The match method in many or most matcher classes will always return either 0.0F or 1.0F, never anything in
 * between. PreferenceMatcher is an example of a matcher that can return in-between values. This is useful for
 * attributes like SoftwarePlatform.CcppAccept-Language for which the capabilities argument is a list of locale
 * names, in the order of the client’s preference. The match value returned is used in BundleRepository.getBundlesFor
 * when its allVariants argument is false, in which case it chooses the bundle variant which had the highest match value.</p>
 */

public interface AttributeMatcher {

    void init(String attributeName, Map<String, String> initParams) throws MatcherException;

    float match(List<String> requirements, List<String> capabilities) throws MatcherException;

}