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
package org.jvending.provisioning.stocking.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

//import org.apache.solr.client.solrj.SolrServer;
//import org.apache.solr.client.solrj.SolrServerException;
//import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
//import org.apache.solr.common.SolrInputDocument;
//import org.apache.solr.core.CoreContainer;
import org.jvending.provisioning.stocking.par.CatalogProperty;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;
import org.xml.sax.SAXException;

public class SolrFilter implements StockingFilter  {

	public void doFilter(FilterTask filterTask) {
		/*
		  ProvisioningArchiveType archive = filterTask.getProvisioningArchive();
		  System.setProperty("solr.solr.home", "/Users/brittonisbell/Downloads/apache-solr-1.4.2/example/solr");
		  CoreContainer.Initializer initializer = new CoreContainer.Initializer();
		  CoreContainer coreContainer = null;
		  try {
			coreContainer = initializer.initialize();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e1) {
			e1.printStackTrace();
		}

		  SolrServer server = new EmbeddedSolrServer(coreContainer, "");
		  Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		  for(ClientBundleType bundle : archive.getClientBundle()) {
			  SolrInputDocument doc1 = new SolrInputDocument();
			  StringBuilder data = new StringBuilder();
			  
			  doc1.addField( "id", getValue("JVending.Internal.BundleId", bundle));
			  try {
				data.append(bundle.getUserDescriptions().getDisplayName().get(0));
			} catch (Exception e2) {
				e2.printStackTrace();
			}
				try {
					data.append(getValue("long-description", bundle));
				} catch (Exception e1) {
					e1.printStackTrace();
				}		  
			  try {
				data.append(getValue("tags", bundle));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			doc1.addField("description", data.toString());
			  docs.add(doc1);	
		  }
		
		  boolean isDropIndex = Boolean.parseBoolean(filterTask.getStockingHandlerConfig().getInitParameter("dropIndex"));
		  if(isDropIndex) {
			  try {
				server.deleteByQuery( "*:*" );
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		 
		  try {
				server.add(docs);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SolrServerException e) {
				e.printStackTrace();
			}
			
		  try {
			server.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
	}
	
	private static String getValue(String key, ClientBundleType bundle) {
		for(CatalogProperty cp : bundle.getCatalogProperty()) {
			if(cp.getPropertyName().equals(key)) {
				return cp.getPropertyValue();
			}
		}
		return null;
	}

}
