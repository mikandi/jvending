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