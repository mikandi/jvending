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
import org.jvending.provisioning.dao.FulfillmentTaskDAO;
import org.jvending.provisioning.model.fulfillmenttask.FulfillmentTaskObject;
import org.jvending.registry.RepositoryRegistry;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */


public final class FulfillmentTaskDAOImpl extends BaseProvisioningDAO implements FulfillmentTaskDAO {

    private static final String dataSource = "hibernate3";

    private static Logger logger = Logger.getLogger("DAO");

    public FulfillmentTaskDAOImpl(String className, String id) {
        super(className, id);
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }

    public void store(FulfillmentTaskObject fulfillmentTaskObject) throws IOException {
        Transaction transaction = null;
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            transaction = session.beginTransaction();
            session.merge(fulfillmentTaskObject);
            transaction.commit();
        } catch (Exception e) {
            try {
                if (transaction != null) transaction.rollback();
            } catch (HibernateException he) {
                logger.log(Level.INFO, "Failed to roll back transaction", he);
            }
            logger.log(Level.INFO, "Failed to save fulfillment task: " + fulfillmentTaskObject.toString(), e);
            throw new IOException("Failed to save fulfillment task: " + fulfillmentTaskObject.toString());
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                logger.log(Level.INFO, "Failed to close session", e);
            }
        }
    }

    public FulfillmentTaskObject getFulfillmentTaskFor(String id) throws IOException {
        Session session = null;
        String queryStatement =
                "select fulfillmentTaskObject from FulfillmentTaskObject as fulfillmentTaskObject where fulfillment_id ='"
                        + id + "'";
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            Query query = session.createQuery(queryStatement);

            List<?> list = query.list();
            if(list.size() == 0) {
            	return null;
            }
            return (FulfillmentTaskObject) list.get(0);

        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.INFO, "Query failed: Query = " + queryStatement, e);
            throw new IOException("Query failed: Query = " + queryStatement + ", Message = " + e.getMessage());
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                logger.log(Level.INFO, "Failed to close session", e);
            }
        }
    }
}