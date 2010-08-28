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

package org.jvending.provisioning.stocking.filter;

import java.util.List;
import java.util.logging.Logger;

import org.jvending.provisioning.stocking.par.CatalogProperty;
import org.jvending.provisioning.stocking.par.ClientBundleType;
import org.jvending.provisioning.stocking.par.ContentFile;
import org.jvending.provisioning.stocking.par.Copyright;
import org.jvending.provisioning.stocking.par.Description;
import org.jvending.provisioning.stocking.par.DescriptorFile;
import org.jvending.provisioning.stocking.par.DeviceRequirement;
import org.jvending.provisioning.stocking.par.DisplayName;
import org.jvending.provisioning.stocking.par.Icon;
import org.jvending.provisioning.stocking.par.Preview;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;
import org.jvending.provisioning.stocking.par.UserDescriptions;
import org.jvending.provisioning.stocking.par.VendorDescription;
import org.jvending.provisioning.stocking.par.VendorInfo;
import org.jvending.provisioning.stocking.par.VendorName;

/**
 * Filter that removes white space from the provisioning.xml file and that removes "/" from all content/icons references
 * within the xml file.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public final class FormatFilter implements StockingFilter {

    private static Logger logger = Logger.getLogger("FormatFilter");

    public void doFilter(FilterTask filterTask) {
        ProvisioningArchiveType archive = filterTask.getProvisioningArchive();

        logger.info("Starting the FormatFilter");
        if (archive == null) {
            logger.info("Archive is null. Cannot continue filter.");
            return;
        }

        for (ClientBundleType bundle : archive.getClientBundle() ) {
            formatClientBundle(bundle);
        }
    }

   private void formatClientBundle(ClientBundleType bundle) {
        bundle.setContentId(bundle.getContentId().trim());

        String version = bundle.getVersion();
        if (version != null) bundle.setVersion(version.trim());

        VendorInfo vendorInfoType = bundle.getVendorInfo();
        if (vendorInfoType != null) {
            VendorDescription vendorDescriptionType = vendorInfoType.getVendorDescription();
            if (vendorDescriptionType != null) {
                String value = vendorDescriptionType.getValue();
                if (value != null) {
                    vendorDescriptionType.setValue(value.trim());
                }
            }

            VendorName vendorNameType = vendorInfoType.getVendorName();
            if(vendorNameType != null) {
                String value = vendorNameType.getValue();
                if(value != null) {
                    vendorNameType.setValue(value.trim());
                }
            }
        }

        formatUserDescriptions(bundle.getUserDescriptions());

        DescriptorFile descriptorFileType = bundle.getDescriptorFile();
        if (descriptorFileType != null) formatDescriptorFile(descriptorFileType);

        ContentFile contentFileType = bundle.getContentFile();
        if (contentFileType != null) formatContentFile(contentFileType);

        String bundleType = bundle.getBundleType().trim();
        bundle.setBundleType(bundleType);

        formatDeviceRequirements(bundle.getDeviceRequirement());

        for (Preview type : bundle.getPreview()) {
            formatPreview(type);
        }

        for (Copyright type : bundle.getCopyright() ) {
            formatCopyright(type);
        }

        for ( CatalogProperty type : bundle.getCatalogProperty() ) {
            type.setPropertyName(type.getPropertyName().trim());
            type.setPropertyValue(type.getPropertyValue().trim());
        }
    }

    private void formatUserDescriptions(UserDescriptions userDescriptionsType) {
        if (userDescriptionsType == null) return;
        for (Icon iconType : userDescriptionsType.getIcon() ) {
            formatIcon(iconType);
        }

        for (DisplayName type : userDescriptionsType.getDisplayName() ) {
            formatDisplayName(type);
        }

        for (Description type : userDescriptionsType.getDescription() ) {
            formatDescription(type);
        }
    }

    private void formatDeviceRequirements(List<DeviceRequirement> deviceRequirements) {
        if (deviceRequirements == null) return;
        for ( DeviceRequirement type : deviceRequirements) {
            type.setRequirementName(type.getRequirementName().trim());
            type.setRequirementValue(type.getRequirementValue().trim());
        }
    }

    private void formatPreview(Preview type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            value = value.trim();
            if (value.startsWith("/")) value = value.substring(1);
            type.setValue(value);
        }
    }

    private void formatIcon(Icon type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            value = value.trim();
            if (value.startsWith("/")) value = value.substring(1);
            type.setValue(value);
        }
    }

    private void formatDisplayName(DisplayName type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            type.setValue(value.trim());
        }
    }

    private void formatDescription(Description type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            type.setValue(value.trim());
        }
    }

    private void formatCopyright(Copyright type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            type.setValue(value.trim());
        }
    }

    private void formatDescriptorFile(DescriptorFile type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            value = value.trim();
            if (value.startsWith("/")) value = value.substring(1);
            type.setValue(value);
        }
    }

    private void formatContentFile(ContentFile type) {
        if (type == null) return;
        String value = type.getValue();
        if (value != null) {
            value = value.trim();
            if (value.startsWith("/")) value = value.substring(1);
            type.setValue(value);
        }
    }

}