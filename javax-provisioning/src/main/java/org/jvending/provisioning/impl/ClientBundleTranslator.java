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

package org.jvending.provisioning.impl;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.provisioning.ProvisioningException;

import org.hibernate.Hibernate;
import org.jvending.provisioning.config.MimeTypeRepository;
import org.jvending.provisioning.model.clientbundle.CatalogProperty;
import org.jvending.provisioning.model.clientbundle.ClientBundle;
import org.jvending.provisioning.model.clientbundle.ContentFile;
import org.jvending.provisioning.model.clientbundle.Copyright;
import org.jvending.provisioning.model.clientbundle.Description;
import org.jvending.provisioning.model.clientbundle.DescriptorFile;
import org.jvending.provisioning.model.clientbundle.DeviceRequirement;
import org.jvending.provisioning.model.clientbundle.DisplayName;
import org.jvending.provisioning.model.clientbundle.Icon;
import org.jvending.provisioning.model.clientbundle.Preview;
import org.jvending.provisioning.model.clientbundle.ToolDescription;
import org.jvending.provisioning.model.clientbundle.UserDescription;
import org.jvending.provisioning.model.clientbundle.VendorInfo;
import org.jvending.provisioning.stocking.StockingComponent;
import org.jvending.provisioning.stocking.StockingException;
import org.jvending.provisioning.stocking.handler.DescriptorHandler;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ToolDescriptions;
import org.jvending.provisioning.stocking.par.UserDescriptions;

/**
 * Translates a JAXB generated client bundle into Hibernate generated JavaBeans.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */

final class ClientBundleTranslator {

    /**
     * Logger instance
     */
    private static Logger logger = Logger.getLogger("ClientBundleTranslator");

    static ClientBundle translate(ClientBundleType bundleType,
                                  Map<String, byte[]> contentMap,
                                  StockingHandlerConfig stockingHandlerConfig,
                                  MimeTypeRepository mimeTypeRepository) throws ProvisioningException {
        if (stockingHandlerConfig == null) throw new ProvisioningException("StockingHandlerConfig is null");
        if (bundleType == null) throw new ProvisioningException("ClientBundleType is null");

        ClientBundle bundle = new ClientBundle();
        bundle.setContentId(bundleType.getContentId());
        bundle.setBundleType(bundleType.getBundleType());
        bundle.setVersion(bundleType.getVersion());

        bundle.setUploadTime(new Date(System.currentTimeMillis()));
        bundle.setVendorInfo(translateVendorInfo(bundleType.getVendorInfo()));
        bundle.setPreviews(translatePreviews(bundleType.getPreview(), contentMap));
        bundle.setToolDescription(translateToolDescriptions(bundleType.getToolDescriptions(), contentMap));
        bundle.setUserDescription(translateUserDescriptions(bundleType.getUserDescriptions(), contentMap));
        bundle.setCopyrights(translateCopyrights(bundleType.getCopyright(), contentMap));
        bundle.setCatalogProperties(translateCatalogProperties(bundleType.getCatalogProperty()));
        bundle.setDeviceRequirements(translateDeviceRequirements(bundleType.getDeviceRequirement()));
/*
        for(org.jvending.provisioning.stocking.par.CatalogProperty cp : bundleType.getCatalogProperty()) {
            if("JVending.Internal.Insert".equals(cp.getPropertyName())) {
            	PublishedStatus ps = new PublishedStatus();
            	ps.setStatus("INSERT");
    			bundle.setPublishedStatus(ps);
    			break;
    		}        	
        }
*/
        
        org.jvending.provisioning.stocking.par.DescriptorFile  descriptorFileType = bundleType.getDescriptorFile();
        if (descriptorFileType != null) {
            DescriptorFile descriptorFile = translateDescriptorFile(bundleType.getDescriptorFile(), contentMap);
            List<ContentFile> contentFiles = translateContentFilesFromDescriptor(descriptorFileType, contentMap,
                    stockingHandlerConfig, mimeTypeRepository);
            List<DescriptorFile> descriptorFiles = new ArrayList<DescriptorFile>();
            descriptorFiles.add(descriptorFile);
          //  String hashId = createHashId(aggregateDescriptorsToBytes(descriptorFiles),
          //          aggregateContentToBytes(contentFiles));
           // bundle.setHashId(hashId);
            bundle.setDescriptorFile(descriptorFile);
            bundle.setContentFilesFromDescriptor(contentFiles);
        } else {
        	org.jvending.provisioning.stocking.par.ContentFile contentFileType = bundleType.getContentFile();
            if (contentFileType == null) {
                logger.info("Both descriptor file and content file are null. Content Id = " +
                        bundleType.getContentId());
            }
            ContentFile contentFile = translateContentFile(contentFileType, contentMap);
           // List<ContentFile> contentFiles = new ArrayList<ContentFile>();
           // contentFiles.add(contentFile);
           // String hashId = createHashId(null,
           //         aggregateContentToBytes(contentFiles));

            bundle.setContentFile(contentFile);
           // bundle.setHashId(hashId);
        }

        return bundle;
    }

    /**
     * Creates a hash (SHA1) for the content. If there is a descriptor and content associated with that descriptor,
     * the returned ID includes a hash of both. Otherwise it just includes a hash ID of either a descriptor or a
     * content.
     *
     * @param descriptor
     * @param content
     * @return hash ID
     */
    /*
    private static String createHashId(byte[] descriptor, byte[] content) {
        if (descriptor == null && content == null)
            return null;

        int descriptorLength = (descriptor != null) ? descriptor.length : 0;
        int contentLength = (content != null) ? content.length : 0;

        byte[] b = new byte[(descriptorLength + contentLength)];

        if (descriptor != null)
            System.arraycopy(descriptor, 0, b, 0, descriptorLength);

        if (content != null && descriptor != null)
            System.arraycopy(content, 0, b, descriptorLength, contentLength);
        else if (content != null && descriptor == null)
            System.arraycopy(content, 0, b, 0, descriptorLength);

        try {
            String bundleId = new String(MessageDigest.getInstance("SHA1").digest(b));
            logger.finest("Created bundle id: ID = " + bundleId);
            return bundleId;
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.INFO, "Could not create bundle id", e);
            return null;
        }
    }
*/
    /**
     * Aggregates a list of content files into a single byte array.
     *
     * @param contentFiles
     * @return array of aggregated content
     */
    /*
    private static byte[] aggregateContentToBytes(List<ContentFile> contentFiles) {
        ByteArrayOutputStream aggregatedContent = new ByteArrayOutputStream();
        if (contentFiles == null) {
            logger.warning("Could not aggregate content bytes");
            return aggregatedContent.toByteArray();
        }

        for (ContentFile contentFile : contentFiles) {
            if (contentFile.getBytes() == null) return aggregatedContent.toByteArray();
            try {
                aggregatedContent.write(contentFile.getBytes());
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not aggregate content bytes", e);
            }
        }
       
        return aggregatedContent.toByteArray();
    }
*/
    /**
     * Aggregates a list of descriptors files into a single byte array.
     *
     * @param contentFiles
     * @return array of aggregated descriptors
     */
    /*
    private static byte[] aggregateDescriptorsToBytes(List<DescriptorFile> contentFiles) {
        ByteArrayOutputStream aggregatedContent = new ByteArrayOutputStream();
        for ( DescriptorFile contentFile : contentFiles) {
            byte[] b = contentFile.getBytes();
            if (b == null) return aggregatedContent.toByteArray();
            try {
                aggregatedContent.write(b);
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not aggregate descriptor bytes", e);
            }
        }
        return aggregatedContent.toByteArray();
    }
*/

    static private List<ContentFile> translateContentFilesFromDescriptor(org.jvending.provisioning.stocking.par.DescriptorFile descriptorFileType, Map<String, byte[]> contentMap,
                                                            StockingHandlerConfig stockingHandlerConfig,
                                                            MimeTypeRepository mimeTypeRepository)
            throws ProvisioningException {
        StockingComponent stockingComponent = stockingHandlerConfig.getStockingContext().getStockingComponent();
        String handlerName = stockingHandlerConfig.getStockingHandlerName();
        logger.info("JV-xxx-000: Stocking Handler Name: " + handlerName);
        String mimeType = descriptorFileType.getMimeType();
        if (mimeType == null || mimeType.trim().equals(""))
            mimeType = mimeTypeRepository.getMimeTypeFromUri(descriptorFileType.getValue());
        try {
            DescriptorHandler descriptorHandler = stockingComponent.createDescriptorHandler(handlerName, mimeType);
            if (descriptorHandler == null)
                throw new ProvisioningException("Could not create DescriptorHandler: Handler = " + handlerName
                        + ", Mime-Type = " + mimeType);
            return descriptorHandler.getContentFiles(descriptorFileType, contentMap);
        } catch (StockingException e) {
            e.printStackTrace();
            throw new ProvisioningException("Could not create DescriptorHandler: Message = " + e.getMessage());
        }
    }

    static private DescriptorFile translateDescriptorFile(org.jvending.provisioning.stocking.par.DescriptorFile descriptorFileType, Map<String, byte[]> contentMap) {
        if (descriptorFileType == null) return null;
        String fileUri = descriptorFileType.getValue();
        String mimeType = descriptorFileType.getMimeType();
        if (isRemoteUri(fileUri)) {
            DescriptorFile descriptorFile = new DescriptorFile();
     //       descriptorFile.setContent(null);
            descriptorFile.setFileUri(fileUri);
            descriptorFile.setMimeType(mimeType);
            return descriptorFile;
        } else {
            byte[] content = (byte[]) contentMap.get(fileUri);
            Blob contentBlob;
            if (content != null) {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(content), content.length);
            } else {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(new byte[0]), 0);
            }
            DescriptorFile descriptorFile = new DescriptorFile();
        //    descriptorFile.setContent(contentBlob);
            descriptorFile.setFileUri(fileUri);
            descriptorFile.setMimeType(mimeType);

           // descriptorFile.setBytes(content);
            return descriptorFile;
        }

    }

    private static boolean isRemoteUri(String uri) {
        return (uri != null && (uri.startsWith("http://") || uri.startsWith("ftp://") || uri.startsWith("https://")));
    }


    static private ContentFile translateContentFile(org.jvending.provisioning.stocking.par.ContentFile contentFileType,
    			Map<String, byte[]> contentMap) {
        if (contentFileType == null) return null;
        String fileUri = contentFileType.getValue();
        String mimeType = contentFileType.getMimeType();
        /*
        byte[] content = contentMap.get(fileUri);
        Blob contentBlob = null;
        if (content != null) {
            contentBlob = Hibernate.createBlob(new ByteArrayInputStream(content), content.length);
        } else {
            contentBlob = Hibernate.createBlob(new ByteArrayInputStream(new byte[0]), 0);
        }
*/
        logger.info("Content File: URI = " + fileUri + ", Mime-Type = " + mimeType);
        ContentFile contentFile = new ContentFile();
    //    contentFile.setContent(contentBlob);
        contentFile.setFileUri(fileUri);
        contentFile.setMimeType(mimeType);
      //  contentFile.setBytes(content);
        return contentFile;
    }

    static private List<DeviceRequirement> translateDeviceRequirements(List<org.jvending.provisioning.stocking.par.DeviceRequirement> deviceRequirements) {
        if (deviceRequirements == null) return null;
        List<DeviceRequirement> list = new ArrayList<DeviceRequirement>();

        for (org.jvending.provisioning.stocking.par.DeviceRequirement deviceRequirementType : deviceRequirements) {
            String value = deviceRequirementType.getRequirementValue();
            String name = deviceRequirementType.getRequirementName();
            DeviceRequirement requirement = new DeviceRequirement();
            requirement.setName(name);
            requirement.setValue(value);
            logger.finest("Device Requirement: Name = " + name + ", Value = " + value);
            list.add(requirement);
        }
        return list;
    }

    static private List<CatalogProperty> 
    	translateCatalogProperties(List<org.jvending.provisioning.stocking.par.CatalogProperty> catalogProperties) {
        if (catalogProperties == null) return null;
        List<CatalogProperty> list = new ArrayList<CatalogProperty>();

        for (org.jvending.provisioning.stocking.par.CatalogProperty catalogPropertyType : catalogProperties) {
            String value = catalogPropertyType.getPropertyValue();
            String name = catalogPropertyType.getPropertyName();
            CatalogProperty property = new CatalogProperty();
            property.setName(name);
            property.setValue(value);
            logger.finest("Property: Name = " + name + ", Value = " + value);
            list.add(property);
        }
        return list;
    }

    static private List<Copyright> translateCopyrights(List<org.jvending.provisioning.stocking.par.Copyright> copyrights, 
    		Map<String, byte[]> contentMap) {
        if (copyrights == null) return null;
        List<Copyright> list = new ArrayList<Copyright>();

        for (org.jvending.provisioning.stocking.par.Copyright copyrightType : copyrights) {
            String value = copyrightType.getValue();
            String uri = copyrightType.getUri();
            if (uri != null) {
                byte[] copyrightValue = (byte[]) contentMap.get(uri);
                if (copyrightValue != null) value = new String(copyrightValue);
            }
            String locale = copyrightType.getLocale();
            Copyright copyright = new Copyright();
            copyright.setCopyright(value);
            copyright.setLocale(locale);
            copyright.setUri(uri);
            logger.finest("Copyright: Copyright = " + value + ", Locale = " + locale + ", URI = " + uri);
            list.add(copyright);
        }
        return list;
    }

    static private UserDescription translateUserDescriptions(UserDescriptions userDescriptionsType, Map<String, byte[]> contentMap) {
        if (userDescriptionsType == null) return null;
        UserDescription userDescription = new UserDescription();
        userDescription.setDescriptions(translateDescriptions(userDescriptionsType.getDescription()));
        userDescription.setDisplayNames(translateDisplayNames(userDescriptionsType.getDisplayName()));
        userDescription.setIcons(translateIcons(userDescriptionsType.getIcon(), contentMap));
        return userDescription;
    }

    static private ToolDescription translateToolDescriptions(ToolDescriptions toolDescriptionsType, Map<String, byte[]> contentMap) {
        if (toolDescriptionsType == null) return null;
        ToolDescription toolDescription = new ToolDescription();
        toolDescription.setDescriptions(translateDescriptions(toolDescriptionsType.getDescription()));
        toolDescription.setDisplayNames(translateDisplayNames(toolDescriptionsType.getDisplayName()));
        toolDescription.setIcons(translateIcons(toolDescriptionsType.getIcon(), contentMap));
        return toolDescription;

    }

    static private List<Description> translateDescriptions(List<org.jvending.provisioning.stocking.par.Description> descriptions) {
        if (descriptions == null) return null;
        List<Description> list = new ArrayList<Description>();

        for (org.jvending.provisioning.stocking.par.Description descriptionType : descriptions ) {
            String value = descriptionType.getValue();
            String locale = descriptionType.getLocale();
            Description description = new Description();
            description.setDescription(value);
            description.setLocale(locale);
            logger.finest("Description: File Name = " + value + ", Locale = " + locale);
            list.add(description);
        }
        return list;
    }


    static private List<DisplayName> translateDisplayNames(List<org.jvending.provisioning.stocking.par.DisplayName> displayNames) {
        if (displayNames == null) return null;
        List<DisplayName> list = new ArrayList<DisplayName>();

        for (org.jvending.provisioning.stocking.par.DisplayName displayNameType : displayNames ) {
            String value = displayNameType.getValue();
            String locale = displayNameType.getLocale();
            DisplayName displayName = new DisplayName();
            displayName.setDisplayName(value);
            displayName.setLocale(locale);
            logger.finest("Display Name: File Name = " + value + ", Locale = " + locale);
            list.add(displayName);
        }
        return list;
    }

    static private List<Icon> translateIcons(List<org.jvending.provisioning.stocking.par.Icon> iconTypes,
    		Map<String, byte[]> contentMap) {
        if (iconTypes == null || contentMap == null) return null;
        List<Icon> icons = new ArrayList<Icon>();

        for (org.jvending.provisioning.stocking.par.Icon iconType : iconTypes ) {
            String fileUri = iconType.getValue();
            String mimeType = iconType.getMimeType();
            String locale = iconType.getLocale();
            byte[] content = (byte[]) contentMap.get(fileUri);
            Blob contentBlob = null;
            if (content != null) {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(content), content.length);
            } else {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(new byte[0]), 0);
            }

            logger.finest("Icon: File Name = " + fileUri + ", Mime-Type = " + mimeType +
                    ", Locale = " + locale);
            Icon icon = new Icon();
     //       icon.setFile(contentBlob);
            icon.setFileUri(fileUri);
            icon.setLocale(locale);
            icon.setMimeType(mimeType);
            icons.add(icon);
        }
        return icons;
    }


    static private List<Preview> translatePreviews(List<org.jvending.provisioning.stocking.par.Preview> previewTypes, 
    		Map<String, byte[]> contentMap) {
        if (previewTypes == null || contentMap == null) return null;
        List<Preview> previews = new ArrayList<Preview>();

        for (org.jvending.provisioning.stocking.par.Preview previewType : previewTypes ) {
            String fileUri = previewType.getValue();
            String mimeType = previewType.getMimeType();
            String locale = previewType.getLocale();
            byte[] content = (byte[]) contentMap.get(fileUri);
            Blob contentBlob = null;
            if (content != null) {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(content), content.length);
            } else {
                contentBlob = Hibernate.createBlob(new ByteArrayInputStream(new byte[0]), 0);
            }

            logger.info("Preview: File Name = " + fileUri + ", Mime-Type = " + mimeType +
                    ", Locale = " + locale);
            
            Preview preview = new Preview();
       //     preview.setFile(contentBlob);
            preview.setFileUri(fileUri);
            preview.setLocale(locale);
            preview.setMimeType(mimeType);
            previews.add(preview);
        }
        return previews;
    }

    static private VendorInfo translateVendorInfo(org.jvending.provisioning.stocking.par.VendorInfo vendorInfoType) {
        if (vendorInfoType == null) return null;
        VendorInfo vendorInfo = new VendorInfo();
        org.jvending.provisioning.stocking.par.VendorName vendorNameType = vendorInfoType.getVendorName();
        if (vendorNameType != null) {
            vendorInfo.setVendorName(vendorNameType.getValue());
            vendorInfo.setVendorNameLocale(vendorNameType.getLocale());
        }

        org.jvending.provisioning.stocking.par.VendorUrl vendorUrlType = vendorInfoType.getVendorUrl();
        if (vendorUrlType != null) {
            vendorInfo.setVendorUrl(vendorUrlType.getValue());
            vendorInfo.setVendorUrlLocale(vendorUrlType.getLocale());
        }

        org.jvending.provisioning.stocking.par.VendorDescription vendorDescriptionType = vendorInfoType.getVendorDescription();
        if (vendorDescriptionType != null) {
            vendorInfo.setVendorDescription(vendorDescriptionType.getValue());
            vendorInfo.setVendorDescriptionLocale(vendorDescriptionType.getLocale());
        }

        return vendorInfo;
    }
}
