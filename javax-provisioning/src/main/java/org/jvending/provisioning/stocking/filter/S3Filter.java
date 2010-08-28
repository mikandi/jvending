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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jvending.provisioning.stocking.par.CatalogProperty;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ContentFile;
import org.jvending.provisioning.stocking.par.Description;
import org.jvending.provisioning.stocking.par.Icon;
import org.jvending.provisioning.stocking.par.Preview;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

public class S3Filter implements StockingFilter {

  //  private static Logger logger = Logger.getLogger("BundleFilter");

    protected FilterTask filterTask;
    
    private S3Service service;
    
    private String cloud;
    
    private String catalog;
    
    private String apps;
    
    private boolean isPush = false;
    
    public void doFilter(FilterTask filterTask) {
        this.filterTask = filterTask;
        isPush = Boolean.parseBoolean(filterTask.getStockingHandlerConfig().getInitParameter("pushToS3"));
        
		AWSCredentials myCredentials = new AWSCredentials(filterTask.getStockingHandlerConfig().getInitParameter("AWS_ACCESS_KEY"), 
				filterTask.getStockingHandlerConfig().getInitParameter("AWS_SECRET_ACCESS_KEY"));
		try {
			service = new RestS3Service(myCredentials);
		} catch (S3ServiceException e1) {
			 throw new Error("JV-1500-45: Could not initialize S3 Service");
		}
		cloud = filterTask.getStockingHandlerConfig().getInitParameter("CLOUDFRONT_IMAGE_BUCKET");
		apps = filterTask.getStockingHandlerConfig().getInitParameter("S3_APK_BUCKET");
		catalog = filterTask.getStockingHandlerConfig().getInitParameter("S3_IMAGE_BUCKET");
		
        ProvisioningArchiveType archive = filterTask.getProvisioningArchive();
        Map<String, byte[]> contentMap = filterTask.getContent();

        List<ClientBundleType> clientBundles = archive.getClientBundle();
		for(ClientBundleType clientBundle : clientBundles) {
			String bundleId = null;
			for(CatalogProperty cp : clientBundle.getCatalogProperty()) {
				if(cp.getPropertyName().equalsIgnoreCase("JVending.Internal.BundleId")) {
					bundleId = cp.getPropertyValue();
					break;
				}
			}
			pushToS3(clientBundle, bundleId, contentMap);
		}
       	try {
			service.shutdown();
		} catch (S3ServiceException e) {
			e.printStackTrace();
		}
		
	}
    
    private boolean insert(ClientBundleType c) {
    	if(c.getContentFile() == null) return false;
    	if(c.getPreview() == null || c.getPreview().isEmpty()) return false;
    	if(c.getUserDescriptions().getDisplayName().isEmpty()) return false;
    	if(c.getVersion() == null || c.getVersion().length() == 0) return false;
    	if(c.getVendorInfo().getVendorName() == null) return false;
    	if(c.getUserDescriptions().getIcon().isEmpty()) return false;
    	
    	//TODO - main-category
    	boolean x = false, y = false, z= false;
    	for(CatalogProperty cp : c.getCatalogProperty()) {
    		if("large-icon".equals(cp.getPropertyName())) {
    			x= true;
    		} else if("long-description".equals(cp.getPropertyName())) {
    			y= true;
    		}
    		else if("main-category".equals(cp.getPropertyName())) {
    			z= true;
    		}
    	}
    	
    	return x && y && z;
    	
    }
    
    private void pushToS3(ClientBundleType clientBundle, String bundleId, Map<String, byte[]> contentMap) {
    	//TODO: - Temporary for import
    	if(insert(clientBundle)) {
        	CatalogProperty c = new CatalogProperty();
        	c.setPropertyName("JVending.Internal.Insert");
        	c.setPropertyValue("true");
        	clientBundle.getCatalogProperty().add(c);   		
    	}

    	//
   		S3Bucket apkBucket;
		try {
			apkBucket = service.getBucket(apps);
		} catch (S3ServiceException e) {
			e.printStackTrace();
			return;
		}
		
		S3Bucket imageBucket;
		AccessControlList bucketAcl;
		try {
			imageBucket = service.getBucket(catalog);
			bucketAcl = service.getBucketAcl(imageBucket);
		} catch (S3ServiceException e) {
			e.printStackTrace();
			return;
		}
		
		
		String cbid = clientBundle.getContentId() + "__" + bundleId;
		
    	ContentFile contentFile =  clientBundle.getContentFile();
    	if(contentFile != null ) { 		
			//APK File
        	String contentUri = contentFile.getValue();
        	String objectKey = cbid + "__" + contentUri;
        	
        	byte[] c = contentMap.get(contentUri);  
        	if(c != null) {
    			S3Object object = new S3Object(apkBucket, objectKey);
    			
    			object.setDataInputStream(new ByteArrayInputStream(c));
				if (isPush) {
					try {
						service.putObject(apkBucket, object);
					} catch (S3ServiceException e) {
						e.printStackTrace();
					}
				}
    			contentFile.setValue("http://" + apps +".s3.amazonaws.com/" + objectKey );
        	}			
    	}
    	
		//Screenshots
    	for(Preview preview : clientBundle.getPreview()) {
    		String p = preview.getValue();
    		if(p != null) {
            	byte[] c = contentMap.get(p);  
            	if(c != null) {
                	String objectKey = cbid + "__" + p;
        			S3Object object = new S3Object(imageBucket, objectKey);
        			object.setAcl(bucketAcl);
        			object.setDataInputStream(new ByteArrayInputStream(c));
        			if(isPush) {
	        			try {
	        				service.putObject(imageBucket, object);
	        			} catch (S3ServiceException e) {
	        				e.printStackTrace();
	        			}   
        			}
        		
        			preview.setValue("http://" + cloud + ".cloudfront.net/" + objectKey);           		
            	}
    		}
    	}
    	
    	
    	//Icon
    	List<Icon> icons = clientBundle.getUserDescriptions().getIcon();
    	for(Icon icon : icons) {
    		String i = icon.getValue();
    		if(i != null) {
            	byte[] c = contentMap.get(i); 
            	if(c != null) {
                	String objectKey = cbid + "__" + i;
        			S3Object object = new S3Object(apkBucket, objectKey);
        			object.setDataInputStream(new ByteArrayInputStream(c));
        			object.setAcl(bucketAcl);
        			if(isPush) {
	        			try {
	        				service.putObject(imageBucket, object);
	        			} catch (S3ServiceException e) {
	        				e.printStackTrace();
	        			}   
        			}
        			
        			icon.setValue("http://"+ cloud +".cloudfront.net/" + objectKey);    	           		
            	}	
    		}
    	}
    	
    	//Large Icon + longDescription
    	List<CatalogProperty> cps = new ArrayList<CatalogProperty>(clientBundle.getCatalogProperty());;
    	if(cps != null) {
    		for(CatalogProperty cp : cps) {
    			if("large-icon".equals(cp.getPropertyName())) {
    				String contentUri = cp.getPropertyValue();
    				byte[] c = contentMap.get(contentUri); 
    				if(c != null) {
                    	String objectKey = cbid + "__" + contentUri;
            			S3Object object = new S3Object(apkBucket, objectKey);
            			object.setDataInputStream(new ByteArrayInputStream(c));
            			object.setAcl(bucketAcl);
            			if(isPush) {
	            			try {
	            				service.putObject(imageBucket, object);
	            			} catch (S3ServiceException e) {
	            				e.printStackTrace();
	            			}   
            			}
            			cp.setPropertyValue("http://" + cloud + ".cloudfront.net/" + objectKey);    
    				}		 
    			} else if("long-description".equals(cp.getPropertyName())) {
    				String description = cp.getPropertyValue();
    				if(description != null) {
                    	String objectKey = cbid + "__long_description.txt";
            			S3Object object = new S3Object(apkBucket, objectKey);
            			object.setDataInputStream(new ByteArrayInputStream(description.getBytes()));
            			object.setAcl(bucketAcl);
            			if(isPush) {
	            			try {
	            				service.putObject(imageBucket, object);
	            			} catch (S3ServiceException e) {
	            				e.printStackTrace();
	            			}   
            			}
            			CatalogProperty desc = new CatalogProperty();
            			desc.setPropertyName("longDescriptionUri");
            			desc.setPropertyValue("http://"+ cloud + ".cloudfront.net/" + objectKey);
            			clientBundle.getCatalogProperty().add(desc);	    				
        			}    					
    				}

    		}
    	}
    	
    	for(Description d : clientBundle.getUserDescriptions().getDescription())
    	{
    		if(d.getValue() != null) {
        		String objectKey = cbid + "__short_description.txt";
    			S3Object object = new S3Object(apkBucket, objectKey);
    			object.setDataInputStream(new ByteArrayInputStream(d.getValue().getBytes()));
    			object.setAcl(bucketAcl);
    			if(isPush) {
	    			try {
	    				service.putObject(imageBucket, object);
	    			} catch (S3ServiceException e) {
	    				e.printStackTrace();
	    			}  
    			}
    			CatalogProperty desc = new CatalogProperty();
    			desc.setPropertyName("shortDescriptionUri");
    			desc.setPropertyValue("http://" + cloud + ".cloudfront.net/" + objectKey);
    			clientBundle.getCatalogProperty().add(desc);	    		
    		}
  	        		
    	}


    }
}
