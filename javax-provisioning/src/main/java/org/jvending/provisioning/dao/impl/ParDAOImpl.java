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

import org.jvending.provisioning.dao.BaseProvisioningDAO;
import org.jvending.provisioning.dao.ParDAO;
import org.jvending.registry.RepositoryRegistry;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */


public class ParDAOImpl extends BaseProvisioningDAO implements ParDAO {

    public ParDAOImpl(String className, String id) {
        super(className, id);
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }

/*
void deleteAll() throws IOException {
    hasSetDataSource = true;
    Session session = null;
    Transaction transaction = null;

    try{
        logger.finest("Opening session from data source = " + dataSource);
        session = ConnectionManager.openSession(dataSource);
        List list = stockingBundleDAO.getBundles();
        transaction = session.beginTransaction();

        for(Iterator i = list.iterator(); i.hasNext(); ) {
            session.delete((StockingBundle) i.next());
        }
        transaction.commit();

   } catch(Exception e) {
        try {
            if(transaction != null) transaction.rollback();
        } catch(HibernateException he) {
            logger.log(Level.INFO, "Failed to roll back transaction", he);
        }
        logger.log(Level.INFO, "Rolled back transaction", e);
        throw new IOException();
    } finally {
        try {
            if(session != null) session.close();
        } catch(HibernateException e) {
            logger.log(Level.INFO, "Failed to close session", e);
        }
    }
}

void delete(List parIds) throws IOException {
    hasSetDataSource = true;
    Session session = null;
    Transaction transaction = null;
    try{
        logger.finest("Opening session from data source = " + dataSource);
        session = ConnectionManager.openSession(dataSource);
      transaction = session.beginTransaction();

        for(Iterator i = parIds.iterator(); i.hasNext(); ) {
            session.delete("from stockingbundle in class StockingBundle where stockingbundle.par_id = " +
                           ((Long) i.next()).toString());
        }
        transaction.commit();
   } catch(Exception e) {
        try {
            if(transaction != null) transaction.rollback();
        } catch(HibernateException he) {
            logger.log(Level.WARNING, "Failed to roll back transaction", he);
        }
        logger.log(Level.INFO, "Rolled back transaction", e);
        throw new IOException();
    } finally {
        try {
            if(session != null) session.close();
        } catch(HibernateException e) {
            logger.log(Level.INFO, "Failed to close session", e);
        }
    }
}
*/
}
