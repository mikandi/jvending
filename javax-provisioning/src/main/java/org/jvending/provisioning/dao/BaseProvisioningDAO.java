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
package org.jvending.provisioning.dao;

import java.io.IOException;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.jvending.registry.hibernate.DataAccessObject;

/**
 * Provides basic implementation of the DataAccessObject that can be extended.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */


public abstract class BaseProvisioningDAO implements DataAccessObject {

    protected Map<String, SessionFactory> sessionFactories;

    protected final String className;

    protected final String id;

    protected BaseProvisioningDAO(String className, String id) {
        this.className = className;
        this.id = id;
    }

    protected SessionFactory getSessionFactoryByName(String name) throws IOException {
        for (String key: sessionFactories.keySet()) {
            if (key.equals(name)) return sessionFactories.get(key);
        }
        throw new IOException("Could not find session factory: Name = " + name);
    }

    public void setSessionFactories(Map<String, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
    }

    public String getClassName() {
        return className;
    }

    public String getID() {
        return id;
    }

}
