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
