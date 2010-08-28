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
package org.jvending.registry.hibernate;

/**
 *  A service for setting a <code>HibernateRepositoryAdaptee.</code>.
 *
 *  Typically, a class will
 *  implement both a <code>HibernateRepositoryAdaptor</code> and <code>HibernateRepository</code> interface. It then
 *  delegates the loading of the connection config info to the <code>HibernateRepositoryAdaptee</code> instance. This
 *  allows the developer to plug in different ways of reading the config file or to changes the connections config file format
 *  completely.
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public interface HibernateRepositoryAdaptor extends HibernateRepository {

    /**
     *
     * @param hibernateRepositoryAdaptee
     */
    void setHibernateRepositoryAdaptee(HibernateRepositoryAdaptee hibernateRepositoryAdaptee);

}
