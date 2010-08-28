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
import org.jvending.provisioning.dao.DeliveryEventDAO;
import org.jvending.provisioning.model.deliveryevent.DeliveryEvent;
import org.jvending.registry.RepositoryRegistry;


public final class DeliveryEventDAOImpl extends BaseProvisioningDAO implements DeliveryEventDAO {

    private static Logger logger = Logger.getLogger("DAO");

    private static final String dataSource = "hibernate3";

    public DeliveryEventDAOImpl(String className, String id) {
        super(className, id);
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }
 
    @SuppressWarnings("unchecked")
	public List<DeliveryEvent> findByFullfillmentId(String fid) throws IOException {
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select deliveryevent from DeliveryEvent as deliveryevent " +
                    "where fulfillment_id ='" + fid + "'";
            Query query = session.createQuery(queryStatement);
            List<DeliveryEvent> events = query.list();
            return events;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }    	
    }
    
    @SuppressWarnings("unchecked")
	public List<DeliveryEvent> findBy(String userId, String networkId) throws IOException {
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select deliveryevent from DeliveryEvent as deliveryevent " +
                    "where network_id='" + networkId + "' AND user_name = '" + userId + "'";
            Query query = session.createQuery(queryStatement);
            List<DeliveryEvent> events = query.list();
            return events;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }    	
    }

    public void store(Object deliveryEvent) throws IOException {
        Transaction transaction = null;
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();

            transaction = session.beginTransaction();
            session.save(deliveryEvent);
            transaction.commit();
        } catch (Exception e) {
            try {
                if (transaction != null) transaction.rollback();
            } catch (HibernateException he) {
                logger.log(Level.INFO, "Failed to roll back transaction", he);
            }
            logger.log(Level.INFO, "Failed to save delivery event: " + deliveryEvent.toString(), e);
            throw new IOException("Failed to save delivery event: " + deliveryEvent.toString());
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                logger.log(Level.INFO, "Failed to close session", e);
            }
        }
    }

}
