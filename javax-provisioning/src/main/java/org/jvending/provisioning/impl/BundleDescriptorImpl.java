/*
 *   JVending
 *   Copyright (C) 2004  Shane Isbell
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


package org.jvending.provisioning.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.BundleType;
import javax.provisioning.Deliverable;
import javax.provisioning.ProvisioningException;

import org.hibernate.Session;
import org.jvending.provisioning.config.DeliverableRepository;
import org.jvending.provisioning.config.MimeTypeRepository;
import org.jvending.provisioning.deliverable.BaseDeliverable;
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
import org.jvending.provisioning.model.clientbundle.VendorInfo;

/**
 * Implementation of the BundleDescriptor
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public final class BundleDescriptorImpl implements BundleDescriptor, Comparable<Object> {

    private ClientBundle clientBundle;

    private List<CatalogProperty> catalogProperties;

    private List<DeviceRequirement> deviceRequirements;

    private List<Preview> previews;

    private BundleType bundleType;

    private DescriptorFile descriptorFile;

    private List<Copyright> copyrights;

    private ContentFile contentFile;

    private List<ContentFile> contentFilesFromDescriptor;

    private VendorInfo vendorInfo;

    private List<Description> descriptions;

    private List<DisplayName> displayNames;

    private List<Icon> icons;

    private Set<Locale> locales;

    private long uploadTime;

    private long parId;

    private String bundleId;

    private Set<String> catalogPropertyNames;

    private DeliverableRepository deliverableRepository;

    private static Logger logger = Logger.getLogger("BundleDescriptorImpl");//23

    private static Locale defaultLocale = Locale.getDefault();

    private MimeTypeRepository mimeTypeRepository;

    private String contentDeliveryUri;
    
    private long eventId;

    public BundleDescriptorImpl() {
    }

    @SuppressWarnings("unchecked")
	public BundleDescriptorImpl(ClientBundle clientBundle) {

        if (clientBundle == null) {//sanity check: should not happen
            logger.warning("JV-1502-001: Client bundle is null. Problem with query.");
        }

        this.clientBundle = clientBundle;
        parId = clientBundle.getParId();
        bundleId = clientBundle.getBundleId();
        descriptorFile = clientBundle.getDescriptorFile();
        contentFile = clientBundle.getContentFile();
        vendorInfo = clientBundle.getVendorInfo();
        uploadTime = clientBundle.getEventId().getTime();
      	//Lazy initializations
        //bundleType
        //copyrights
        //previews
        //deviceRequirements
        //catalogProperties
        //descriptions
        //displayNames
        //icons
        //contentFilesFromDescriptor
         
        if(bundleId == null) throw new IllegalArgumentException("BundleId null");
        this.eventId = clientBundle.getEventId().getTime();
    }
    
    public void close() {
    	clientBundle.close();
    }
    
    public Session getSession() {
    	return clientBundle.getSession();
    }

    public void setDeliverableRepository(DeliverableRepository deliverableRepository) {
        this.deliverableRepository = deliverableRepository;
    }

    public void setMimeTypeRepository(MimeTypeRepository mimeTypeRepository) {
        this.mimeTypeRepository = mimeTypeRepository;
    }

    public void setContentDeliveryUri(String contentDeliveryUri) {
        this.contentDeliveryUri = contentDeliveryUri + "/ContentAccessor";
    }

    public String getBundleID() {
        return bundleId;
    }

    public BundleType getBundleType() {
    	if(bundleType == null) {
    		bundleType = BundleType.getBundleType(clientBundle.getBundleType());
    	}
        return bundleType;
    }

    public void setBundleType(BundleType bundleType) {
        this.bundleType = bundleType;
        clientBundle.setBundleType(bundleType.toString());
    }

    public String getCatalogProperty(String key) {
    	if(key == null) return null;
    	if(catalogProperties == null) {
    		catalogProperties = clientBundle.getCatalogProperties();
    	}
        for ( CatalogProperty property : catalogProperties) {
            if (property != null && key.equals(property.getName()))
                return property.getValue();
        }
        return null;
    }

    public Set<String> getCatalogPropertyNames() {
    	if(catalogPropertyNames == null) {
    		catalogPropertyNames = getCatalogPropertyNamesPrivate();
    	}
        return catalogPropertyNames;
    }

    public Deliverable getContentFile() {
        if (contentFile == null || descriptorFile != null) 
        {
        	logger.info("The content file is null Bundle ID:" + bundleId);
        	return null;
        }
        String fileUri = contentFile.getFileUri();

        try {
            URL url = (!isRemoteUri(fileUri)) ?
                    new URL(contentDeliveryUri + "?id=" + getBundleID() +
                            "&type=content") :
                    new URL(fileUri);
            URI uri = new URI(fileUri);

            String mimeType = contentFile.getMimeType();
            if (mimeType == null) {
                mimeType = mimeTypeRepository.getMimeTypeFromUri(fileUri);
                if (mimeType == null) {
                    logger.fine("JV-1502-002: Can not find a mime-type for this file: URI = " + fileUri);
                    return null;
                }
            }
            return new BaseDeliverable(url, uri, mimeType);
        } catch (MalformedURLException e) {
            logger.log(Level.WARNING, "JV-1502-004: Content file URL is malformed: Bundle Id = " + getBundleID(), e);
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "JV-1502-005: Content file URI is malformed: Bundle Id = " + getBundleID(), e);
        }
        return null;
    }

    public String getContentID() {
        return clientBundle.getContentId();
    }

    public void setContentID(String contentId) {
        clientBundle.setContentId(contentId);
    }

    public String getCopyright(Locale locale) {
    	if(copyrights == null) {
    		copyrights = clientBundle.getCopyrights();
    	}
    	
        for ( Copyright copyright : copyrights) {
            String loc = copyright.getLocale();
            if (locale == null || (loc != null && loc.equalsIgnoreCase(locale.toString())))
                return copyright.getCopyright();
        }
        return "";
    }

    public String getDescription(Locale locale) {
    	if(descriptions == null) {
    		descriptions = clientBundle.getUserDescription().getDescriptions();
    	}
        for (Description description : descriptions ) {
            String loc = description.getLocale();
            if (locale == null || (loc != null && loc.equalsIgnoreCase(locale.toString())))
                return description.getDescription();
        }
        return "";
    }

    public Deliverable getDescriptorFile() {
        if (descriptorFile == null) return null;
        String fileUri = descriptorFile.getFileUri();
        // if(fileUri.trim().startsWith("/")) fileUri = fileUri.substring(1);

        try {
            URL url = (!isRemoteUri(fileUri)) ?
                    new URL(contentDeliveryUri + "?id=" + getBundleID() +
                            "&type=descriptor") :
                    new URL(fileUri);
            URI uri = new URI(fileUri);

            String mimeType = descriptorFile.getMimeType();
            if (mimeType == null) {
                mimeType = mimeTypeRepository.getMimeTypeFromUri(fileUri);
                if (mimeType == null) {
                    logger.info("JV-1502-23: Could not find mime-type for DescriptorFile");
                    return null;
                }
            }
            return deliverableRepository.getDeliverableFor(
                    url, uri, mimeType,
                    createDescriptorContentFiles());
        } catch (MalformedURLException e) {
            logger.info("JV-1502-007: Descriptor file URL is malformed: Bundle Id = " + getBundleID());
        } catch (URISyntaxException e) {
            logger.log(Level.WARNING, "JV-1502-008: Descriptor file URI is malformed: Bundle Id = " + getBundleID() +
                    " File URI = " + fileUri, e);
        } catch (ProvisioningException e) {
            logger.info("JV-1502-006: Deliverable is null, Message = " + e.getMessage());
        }

        return null;
    }

    //List of Deliverables
    private List<Deliverable> createDescriptorContentFiles() {
        ArrayList<Deliverable> list = new ArrayList<Deliverable>();
        if(contentFilesFromDescriptor == null) {
        	 contentFilesFromDescriptor = clientBundle.getContentFilesFromDescriptor();
        }
        for (ContentFile contentFile : contentFilesFromDescriptor ) {
            String fileUri = contentFile.getFileUri();
            try {
                URL url = (!isRemoteUri(fileUri)) ?
                        new URL(contentDeliveryUri + "?id=" + getBundleID() +
                                "&type=descriptorContent&fileName=" + fileUri) :
                        new URL(fileUri);
                URI uri = new URI(fileUri);

                list.add(new BaseDeliverable(url, uri, contentFile.getMimeType()));
            } catch (MalformedURLException e) {
                logger.log(Level.WARNING, "JV-1502-009: Malformed URL:", e);
                return Collections.unmodifiableList(new ArrayList<Deliverable>());
            } catch (URISyntaxException e) {
                logger.log(Level.WARNING, "JV-1502-010: Descriptor file URI is malformed: Bundle Id = "
                        + getBundleID(), e);
                return Collections.unmodifiableList(new ArrayList<Deliverable>());
            }
        }
        return Collections.unmodifiableList(list);
    }

    public String getDisplayName(Locale locale) {
    	if(displayNames == null) {
    		displayNames = clientBundle.getUserDescription().getDisplayNames();
    	}
        for (DisplayName displayName : displayNames) {
        	if(displayName == null) return "";
            String loc = displayName.getLocale();
            if (locale == null || (loc != null && loc.equalsIgnoreCase(locale.toString())))
                return displayName.getDisplayName();
        }
        return "";
    }


    public List<Deliverable> getIcons(Locale locale) {
        List<Deliverable> list = new ArrayList<Deliverable>();
        if(icons == null) {
        	icons = clientBundle.getUserDescription().getIcons();
        }
        for (Icon icon : icons) {
            String loc = icon.getLocale();
            if (locale == null || (loc != null && loc.equalsIgnoreCase(locale.toString()))) {
                logger.finest("JV-1502-011: Attempting to create deliverable for icon: Locale = " + locale);
                String fileUri = icon.getFileUri();
                try {
                    String mimeType = icon.getMimeType();
                    if (mimeType == null) {
                        mimeType = mimeTypeRepository.getMimeTypeFromUri(fileUri);
                    }

                    URL url = (!isRemoteUri(fileUri)) ?
                            new URL(contentDeliveryUri + "?id=" + getBundleID() +
                                    "&type=icon&fileName=" + fileUri) :
                            new URL(fileUri);
                    URI uri = new URI(fileUri);

                    if (url == null) logger.finest("JV-1502-012: Cannot find icon URL.");
                    list.add(new BaseDeliverable(url, uri, mimeType));
                } catch (MalformedURLException e) {
                    logger.info("JV-1502-014: Icon file URL is malformed: Bundle Id = " + getBundleID());
                } catch (URISyntaxException e) {
                    logger.log(Level.WARNING, "JV-1502-015: Icon file URI is malformed: Bundle Id = "
                            + getBundleID(), e);
                }
            }
        }
        return list;
    }

    public Set<Locale> getLocales() {
        if (locales == null) locales = getLocalesInternal();
        return locales;
    }

    public long getParFileID() {
        return parId;
    }

    //List of deliverables
    public List<Deliverable> getPreviews(Locale locale) {
        List<Deliverable> list = new ArrayList<Deliverable>();
		if(previews == null) {
			 previews = clientBundle.getPreviews();
		}
        for (Preview preview : previews) {
            String loc = preview.getLocale();
            if (locale == null || (loc != null && loc.equalsIgnoreCase(locale.toString()))) {
                logger.finest("JV-1502-016: Attempting to create deliverable for preview: Locale = " + locale);
                String fileUri = preview.getFileUri();
                try {
                    String mimeType = preview.getMimeType();
                    if (mimeType == null) {
                        mimeType = mimeTypeRepository.getMimeTypeFromUri(fileUri);
                    }

                    URL url = (!isRemoteUri(fileUri)) ?
                            new URL(contentDeliveryUri + "?id=" + getBundleID() +
                                    "&type=preview&fileName=" + fileUri) :
                            new URL(fileUri);
                    URI uri = new URI(fileUri);

                    if (url == null) logger.finest("JV-1502-017: Cannot find preview URL.");
                    list.add(new BaseDeliverable(url, uri, mimeType));
                } catch (MalformedURLException e) {
                    logger.info("JV-1502-019: Preview file URL is malformed: Bundle Id = " + getBundleID());
                } catch (URISyntaxException e) {
                    logger.log(Level.WARNING, "JV-1502-020: Preview file URI is malformed: Bundle Id = "
                            + getBundleID(), e);
                }
            }
        }
        return list;
    }

    //list of Strings
    public List<String> getRequirement(String name) {
        if (name == null) return null;
        List<String> list = new ArrayList<String>();
		if(deviceRequirements == null) {
			deviceRequirements = clientBundle.getDeviceRequirements();
		}
        for (DeviceRequirement requirement :deviceRequirements) {
            if (requirement.getName().equals(name))
                list.add(requirement.getValue());
        }
        return (list.size() == 0) ? null : Collections.unmodifiableList(list);
    }

    public Set<String> getRequirementNames() {
        Set<String> set = new HashSet<String>();
		if(deviceRequirements == null) {
			deviceRequirements = clientBundle.getDeviceRequirements();
		}
        for (DeviceRequirement requirement :deviceRequirements) {
            set.add(requirement.getName());
        }
        return Collections.unmodifiableSet(set);
    }

    //TODO: Implementation: performance is key here
    public URL getResource(String resourceSpec) throws IOException {
        return null;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public String getVendor(Locale locale) {
        if (vendorInfo == null) return "";
        if (locale == null) locale = defaultLocale;
        String name = vendorInfo.getVendorName();
        String vendorLocale = vendorInfo.getVendorNameLocale();
        if (vendorLocale == null || vendorLocale.equals(""))
            return (name != null) ? name : "";
        return (locale.equals(new Locale(vendorLocale))) ? name : "";
    }

    public String getVendorDescription(Locale locale) {
        if (vendorInfo == null) return "";
        if (locale == null) locale = defaultLocale;
        String description = vendorInfo.getVendorDescription();
        String vendorLocale = vendorInfo.getVendorDescriptionLocale();
        if (vendorLocale == null)
            return (description != null) ? description : "";
        return (locale.equals(new Locale(vendorLocale))) ? description : "";
    }

    public String getVendorURL(Locale locale) {
        if (vendorInfo == null) return "";
        if (locale == null) locale = defaultLocale;
        String url = vendorInfo.getVendorUrl();
        String vendorLocale = vendorInfo.getVendorUrlLocale();
        if (vendorLocale == null)
            return (url != null) ? url : "";
        return (locale.equals(new Locale(vendorLocale))) ? url : "";
    }

    public String getVersion() {
        return clientBundle.getVersion();
    }

    public void setVersion(String version) {
        clientBundle.setVersion(version);
    }

    public String toString() {
        return "Client Bundle Id = " + getBundleID() + "\r\n" +
                "Content Id = " + getContentID() + "\r\n" +
                "Vendor = " + getVendor(null) + "\r\n" +
                "Version = " + getVersion() + "\r\n" +
                "Vendor Description = " + getVendorDescription(null) + "\r\n" +
                "Vendor Url = " + getVendorURL(null) + "\r\n" +
                "Display Name = " + getDisplayName(null) + "\r\n" +
                "Copyright = " + getCopyright(null) + "\r\n" +
                "Description = " + getDescription(null) + "\r\n" +
                "Upload Time = " + getUploadTime() + "\r\n" +
                "Bundle Type = " + getBundleType();
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BundleDescriptor))
            return false;

        BundleDescriptor descriptor = (BundleDescriptor) o;

        if (!descriptor.getContentID().equals(getContentID())) return false;

        String thisVersion = getVersion();
        String otherVersion = descriptor.getVersion();

        if (thisVersion != null && !VersionMatcher.isVersionId(thisVersion)) {
            logger.warning("JV-1502-021: Bundle does not have a valid version: Id = " + getBundleID() +
                    ", Version = " + thisVersion);//should not happen
            thisVersion = null;
        }

        if (otherVersion != null && !VersionMatcher.isVersionId(otherVersion)) {
            logger.warning("JV-1502-022: Bundle does not have a valid version: Id = " +
                    descriptor.getBundleID() +
                    ", Version = " + otherVersion);//should not happen
            otherVersion = null;
        }

        if (thisVersion == null && otherVersion == null)
            return (getUploadTime() == descriptor.getUploadTime());

        if (thisVersion != null && otherVersion != null)
            return thisVersion.equals(otherVersion);

        return false;
    }

    public int hashCode() {
        int result = 43;
        result = 3 * result + getContentID().hashCode();
        if (getVersion() == null || !VersionMatcher.isVersionId(getVersion()))
            result = 3 * result + (int) (getUploadTime() ^ (getUploadTime() >>> 32));
        else
            result = 3 * result + getVersion().hashCode();

        return result;
    }

    public int compareTo(Object o) throws ClassCastException {
        BundleDescriptor descriptor = (BundleDescriptor) o;
        int cid = getContentID().compareTo(descriptor.getContentID());
        if (cid != 0) return cid;

        int compareValue = VersionMatcher.compareVersion(getVersion(), descriptor.getVersion(),
                getUploadTime(), descriptor.getUploadTime());
/*
        logger.finest("Bundle Id = " + getBundleID() + 
                      ", Version = " + getVersion() + 
                      ", Upload Time = " + getUploadTime() + 
                      ", Bundle Id2 = " + descriptor.getBundleID() +
                      ", Version 2 = " + descriptor.getVersion() + 
                      ", Upload Time2 = " + descriptor.getUploadTime() +
                      " Compare = " + compareValue);
*/
        return compareValue;
    }

    public String getContentFileName() {
        return (contentFile != null) ? contentFile.getFileUri() : null;
    }

    public String getDescriptorFileName() {
        return (descriptorFile != null) ? descriptorFile.getFileUri() : null;
    }

    String getDescriptorMimeType() {
        return (descriptorFile != null) ? descriptorFile.getMimeType() : null;
    }

    public ClientBundle getClientBundle() {
        return clientBundle;
    }

    private boolean isRemoteUri(String uri) {
        return (uri != null && (uri.startsWith("http://") || uri.startsWith("ftp://") || uri.startsWith("https://")));
    }

    private Set<String> getCatalogPropertyNamesPrivate() {
        Set<String> set = new HashSet<String>();
    	if(catalogProperties == null) {
    		catalogProperties = clientBundle.getCatalogProperties();
    	}
        for (CatalogProperty property : catalogProperties ) {
        	if(property != null && property.getName() != null) {
        		set.add(property.getName());
        	}        
        }
        return Collections.unmodifiableSet(set);
    }

    private Set<Locale> getLocalesInternal() {//TODO: optimize
        Set<Locale> set = new HashSet<Locale>();
        if(copyrights == null) {
        	copyrights = clientBundle.getCopyrights();
        }
        if (copyrights != null) {
            for (Copyright copyright : copyrights) {
                String locale = copyright.getLocale();
                if (locale != null) set.add(new Locale(locale));
            }
        }

    	if(descriptions == null) {
    		descriptions = clientBundle.getUserDescription().getDescriptions();
    	}
        if (descriptions != null) {
            for ( Description description : descriptions) {
                String locale = description.getLocale();
                if (locale != null)
                    set.add(new Locale(locale));
            }
        }
    	if(displayNames == null) {
    		displayNames = clientBundle.getUserDescription().getDisplayNames();
    	}
        if (displayNames != null) {
            for (DisplayName displayName : displayNames) {
                String locale = displayName.getLocale();
                if (locale != null)
                    set.add(new Locale(locale));
            }
        }

		if(previews == null) {
			 previews = clientBundle.getPreviews();
		}
        if (previews != null) {
            for (Preview preview : previews) {
                String locale = preview.getLocale();
                if (locale != null)
                    set.add(new Locale(locale));
            }
        }
        if (vendorInfo != null) {
            String locale = vendorInfo.getVendorDescriptionLocale();
            if (locale != null) set.add(new Locale(locale));
        }

        set.add(null);
        if(icons == null) {
        	icons = clientBundle.getUserDescription().getIcons();
        }
        if (icons != null) {
            for (Icon icon : icons) {
                String locale = icon.getLocale();
                if (locale != null) set.add(new Locale(locale));
            }
        }
/*
        if (previews != null) {
            for (Preview preview : previews) {
                String locale = preview.getLocale();
                if (locale != null) set.add(new Locale(locale));
            }
        }
*/
        return Collections.unmodifiableSet(set);
    }

    private static class VersionMatcher {

        private static Pattern versionIdMatch = Pattern.compile("[\\p{Alnum}[._-]]*");

        private VersionMatcher() {
        }

        // x0 is higher order, where x0 < x1
        static int compareVersion(String version, String version2, long upload, long upload2) {
            if (version != null && (!isVersionId(version) || version.trim().equals(""))) version = null;
            if (version2 != null && (!isVersionId(version2) || version2.trim().equals(""))) version2 = null;

            if (version == null && version2 == null) {
                if (upload < upload2)
                    return -1;
                if (upload > upload2)
                    return 1;
                return 0;
            }
            if (version == null && version2 != null) return -1;
            if (version != null && version2 == null) return 1;

            String[] versionTokens = version.split("[._-]");
            String[] version2Tokens = version2.split("[._-]");
            if (testExactMatch(versionTokens, version2Tokens))
                return 0;
            else if (testGreaterThanEqualMatch(versionTokens, version2Tokens))
                return -1;
            return 1;
        }

        static boolean isVersionId(String version) {
            return version != null && versionIdMatch.matcher(version).matches();
        }

        static private boolean testExactMatch(String[] version, String[] version2) {
            int vSize = version.length;
            int v2Size = version2.length;
            if (vSize != v2Size) return false;

            for (int i = 0; i < vSize; i++) {
                if (!version[i].equals(version2[i])) return false;
            }
            return true;
        }

        private static String[] padArray(String[] value, int size) {
            int valueSize = value.length;
            int padSize = Math.abs(valueSize - size);

            String[] newValue = new String[size];

            System.arraycopy(value, 0, newValue, 0, valueSize);
            for (int i = 0; i < padSize; i++) {
                newValue[i + valueSize] = "0";
            }
            return newValue;
        }

        static private boolean testGreaterThanEqualMatch(String[] version, String[] version2) {
            int vSize = version.length;
            int v2Size = version2.length;
            int maxSize = Math.max(vSize, v2Size);

            if (vSize < v2Size)
                version = padArray(version, v2Size);
            else if (vSize > v2Size)
                version2 = padArray(version2, vSize);

            for (int i = 0; i < maxSize; i++) {
                if (isNumber(version[i]) && isNumber(version2[i])) {
                    try {
                        Integer v = new Integer(version[i]);
                        Integer v2 = new Integer(version2[i]);
                        int compare = v2.compareTo(v);
                        if (compare < 0) return false;
                        if (compare > 0) return true;
                    } catch (NumberFormatException e) {
                        //this should never happen: already done check
                    }
                } else {
                    int m = version[i].compareTo(version[i]);
                    if (m == 1) return true;
                    if (m == -1) return false;
                }
            }
            return true;
        }

        static private boolean isNumber(String number) {
            try {
                new Integer(number);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

	public long getEventID() {
		return eventId;
	}

}
