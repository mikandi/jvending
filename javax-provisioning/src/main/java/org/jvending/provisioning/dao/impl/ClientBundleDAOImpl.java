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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jvending.provisioning.dao.BaseProvisioningDAO;
import org.jvending.provisioning.dao.ClientBundleDAO;
import org.jvending.provisioning.model.clientbundle.ClientBundle;
import org.jvending.registry.RepositoryRegistry;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */


public final class ClientBundleDAOImpl extends BaseProvisioningDAO implements ClientBundleDAO {

    private static Logger logger = Logger.getLogger("DAO");

    private static final String dataSource = "hibernate3";

    public ClientBundleDAOImpl(String className, String id) {
        super(className, id);
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }

    public ClientBundle getBundleByID(String id) {
        Session session = null;
        String queryStatement =
                "select clientBundle " +
                        "from ClientBundle as clientBundle where bundle_id ='" + id + "'";
        try {
            logger.finest("Opening session from data source = " + dataSource);
            session = getSessionFactoryByName(dataSource).openSession();

            Query query = session.createQuery(queryStatement);

            List<?> bundleDescriptorList = query.list();

            int bundleSize = bundleDescriptorList.size();
            if (bundleSize != 1) {
                logger.warning("List of descriptors is not unique or is empty: Descriptor Size = " + bundleSize 
                		+ ", Bundle ID =" + id);
                throw new IOException();
            }
            ClientBundle bundle = (ClientBundle) bundleDescriptorList.get(0);
            bundle.setSession(session);
            return bundle;

        } catch (Exception e) {
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement);
        } 
        return null;
    }
    
    public List<ClientBundle> getBundles() {
        Session session = null;
        String queryStatement =
                "select clientBundle " +
                        "from ClientBundle as clientBundle";
        try {
            logger.finest("Opening session from data source = " + dataSource);
            session = getSessionFactoryByName(dataSource).openSession();

            Query query = session.createQuery(queryStatement);

            return (List<ClientBundle>) query.list();


        } catch (Exception e) {
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement);
        } 
        return null;    	
    }
    
    public void store(List<ClientBundle> clientBundles) throws IOException {
        if (clientBundles == null) throw new IOException("No client bundles to store");
        Session session = null;
        Transaction transaction = null;
        try {
            logger.finest("Opening session from data source = " + dataSource);
            try {
				session = getSessionFactoryByName(dataSource).getCurrentSession();
			} catch (Exception e) {
			//	e.printStackTrace();
			}	
			

			if(session == null) {
	            try {
					session = getSessionFactoryByName(dataSource).openSession();
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}

            transaction = session.beginTransaction();

            for (ClientBundle clientBundle : clientBundles) {
                logger.info("Content Id = " + clientBundle.getContentId() +
                        ", Primary Id = " + clientBundle.getPrimaryId());
                
                session.merge(clientBundle);
            }
            transaction.commit();
        } catch (Exception e) {
            try {
                if (transaction != null) transaction.rollback();
            } catch (HibernateException he) {
                logger.log(Level.INFO, "Failed to roll back transaction", he);
            }
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                logger.log(Level.INFO, "Failed to close session", e);
            }
        }
    }


}