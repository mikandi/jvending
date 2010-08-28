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