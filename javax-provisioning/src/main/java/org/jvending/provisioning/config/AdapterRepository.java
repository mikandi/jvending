/*
 *  JVending
 *  Copyright (C) 2006  Shane Isbell
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
