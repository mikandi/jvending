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
package javax.provisioning;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Represents an entry point to the server ’s repository of client bundles.
 * Applications obtain a BundleRepository instance by calling the <code>ProvisioningContext.getBundleRepository</code>
 * method.
 *
 * @author Shane Isbell
 */

public interface BundleRepository {

    long addParFile(InputStream inputStream) throws IOException;

    void emptyRepository() throws IOException;

    BundleDescriptor getBundleByID(String bundleID);

    Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies)
            throws IOException;

    Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies,
                                    boolean allVersions, boolean allVariants) throws IOException;
    

    
    Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies, long EventId) throws IOException;

    float matchAttribute(String attributeName, List<String> requirementValues,
                                List<String> capabilityValues);

    void removeParFile(long parFileID) throws IOException;
 
    /**
     * Not in spec
     */
    Collection<BundleDescriptor> getBundlesFor(Capabilities deviceCapabilities, List<MatchPolicy> matchPolicies,
            boolean allVersions, boolean allVariants, long EventId) throws IOException;   
    
    void close();

}