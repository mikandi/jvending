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

package org.jvending.provisioning.dao.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.BundleDescriptor;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jvending.provisioning.dao.BaseProvisioningDAO;
import org.jvending.provisioning.dao.BundleDescriptorDAO;
import org.jvending.provisioning.impl.BundleDescriptorImpl;
import org.jvending.registry.RepositoryRegistry;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */


public final class BundleDescriptorDAOImpl extends BaseProvisioningDAO implements BundleDescriptorDAO {

    private static Logger logger = Logger.getLogger("DAO");

    private String dataSource = "hibernate3";

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }

    public BundleDescriptorDAOImpl(String className, String id) {
        super(className, id);
    }

    /* TO DO: Fix the BundleDescriptorImpl dependency: make interface & remove cyclical problem
        void removeBundle(BundleDescriptor bundleDescriptor) {
            ClientBundle clientBundle = ((BundleDescriptorImpl) bundleDescriptor).getClientBundle();
            Session session = null;
            Transaction transaction = null;
            try {
                logger.finest("Opening session from data source = " + dataSource);

                session = ConnectionManager.openSession(dataSource);
              transaction = session.beginTransaction();
                session.delete(clientBundle);
                transaction.commit();
            } catch(Exception e) {
                logger.log(Level.INFO, "Deletion failed:", e);
            } finally {
                try {
                    if(session != null) session.close();
                } catch(HibernateException e) {
                    logger.log(Level.INFO, "Failed to close session", e);
                }
            }
        }
*/
    
    public List<BundleDescriptor> getBundleDescriptorsByEventId(long eventId) {
        Session session = null;
        Timestamp t = new Timestamp(eventId);
        String queryStatement =
                "select new org.jvending.provisioning.impl.BundleDescriptorImpl(clientBundle) " +
                        "from ClientBundle as clientBundle where version_timestamp>'" + t + "'";//TODO: optimize: prepared
        try {
            logger.finest("Opening session from data source = " + dataSource);
            session = getSessionFactoryByName(dataSource).openSession();
            Query query = session.createQuery(queryStatement);

            List<BundleDescriptor> results = query.list();
            return results;
        } catch (Exception e) {
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement, e);
        }
        return null;
    }
    
    public BundleDescriptor getBundleDescriptorFor(String id) {
        Session session;
        String queryStatement =
                "select new org.jvending.provisioning.impl.BundleDescriptorImpl(clientBundle) " +
                        "from ClientBundle as clientBundle where bundle_id ='" + id + "'";//TODO: optimize: prepared
        try {
            logger.finest("Opening session from data source = " + dataSource);
            session = getSessionFactoryByName(dataSource).openSession();
            Query query = session.createQuery(queryStatement);

            List<?> bundleDescriptorList = query.list();

            int bundleSize = bundleDescriptorList.size();
            if (bundleSize != 1) {
                logger.warning("List of descriptors is not unique or is empty: Descriptor Size = " + bundleSize +", id = " + id);
                return null;
                //    throw new IOException("List of descriptors is not unique or is empty");
            }
            BundleDescriptorImpl descriptor = (BundleDescriptorImpl) bundleDescriptorList.get(0);
            descriptor.getClientBundle().setSession(session);
            return descriptor;

        } catch (Exception e) {
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement, e);
        } 
        return null;
    }

    public List<BundleDescriptor> getBundleDescriptors() {
        Session session;
        String queryStatement =
                "select new org.jvending.provisioning.impl.BundleDescriptorImpl(clientBundle) " +
                        "from ClientBundle as clientBundle";
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            Query query = session.createQuery(queryStatement);
            List<BundleDescriptor> results = query.list();
            Hibernate.initialize(results);
            return results;
        } catch (Exception e) {
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement, e);
        }
        return null;
    }
}
