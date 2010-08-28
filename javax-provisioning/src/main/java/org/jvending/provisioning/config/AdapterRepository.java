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
package org.jvending.provisioning.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import org.jvending.provisioning.config.adapters.AdapterType;
import org.jvending.provisioning.config.adapters.AdaptersType;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.jaxb.JaxbConfiguration;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */

public final class AdapterRepository implements Repository {

    private static Logger logger = Logger.getLogger("org.jvending.provisioning.config");

    private List<AdapterType> adapters;

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
        if (properties == null) throw new IOException("JV-1300-001: Null properties values for the adapter repository");
        if (inputStream == null) throw new IOException("JV-1300-002: Null inputstream for the adapter repository");

        String _package = (String) properties.get("binding-package");
        AdaptersType adaptersType = (AdaptersType) JaxbConfiguration.parse(inputStream, _package);
        List<AdapterType> adapt = adaptersType.getAdapter();

        if (adapt == null) {
            throw new IOException("JV-1300-003: Could not load any adapters: Binding Package = " + _package);
        }
        adapters = Collections.unmodifiableList(adapt);
        logger.info("JV-1300-004: Loaded the AdapterRepository: Binding Package = " + _package);
    }

    public List<AdapterType> getAdapters() {
        return adapters;
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }

}
