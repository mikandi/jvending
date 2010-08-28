/*
 *   JVending
 *   Copyright (C) 2006  Shane Isbell
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.provisioning.AdapterInfo;
import javax.provisioning.adapter.AdapterConfig;
import javax.provisioning.adapter.AdapterContext;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */

final class AdapterFactory {

  //  private static Logger logger = Logger.getLogger("AdapterFactory");

    private AdapterFactory() {
    }

    static AdapterConfig createAdapterConfig(String adapterName,
                                             long duration,
                                             String fileExtension,
                                             String mimeType,
                                             String baseURI,
                                             Map<String, String> initParam,
                                             String className,
                                             AdapterContext adapterContext) {
        return new AdapterConfigImpl(adapterName, duration, fileExtension, mimeType, baseURI, initParam, className,
                adapterContext);
    }

    //TODO: Consider eliminating this method. not using it.
    static AdapterInfo createAdapterInfo(String adapterName, long defaultDuration,
                                         String descriptorFileExtension, String descriptorFileMimeType) {
        return new AdapterInfoImpl(adapterName, defaultDuration, descriptorFileExtension, descriptorFileMimeType);
    }

    private static class AdapterConfigImpl implements AdapterConfig {

        private final String baseURI;

        private final String adapterName;

        private final long defaultFulfillmentDuration;

        private final String descriptorFileExtension;

        private final String descriptorFileMimeType;

        private final AdapterContext adapterContext;

        private final Map<String, String> initParam;

        AdapterConfigImpl(String adapterName, long duration,
                          String fileExtension, String mimeType, String baseURI, Map<String, String> initParam, String className,
                          AdapterContext adapterContext) {
            this.adapterName = adapterName;
            this.defaultFulfillmentDuration = duration;
            this.descriptorFileExtension = fileExtension;
            this.descriptorFileMimeType = mimeType;
            this.baseURI = baseURI;
            this.initParam = initParam;
            this.adapterContext = adapterContext;
        }

        public AdapterContext getAdapterContext() {
            return adapterContext;
        }

        public String getBaseURI() {
            return baseURI;
        }

        public String getInitParameter(String name) {
            return (String) initParam.get(name);
        }

        public Set<String> getInitParameterNames() {
            Set<String> set = initParam.keySet();
            return (set == null) ? new HashSet<String>() : new HashSet<String>(set);
        }

        public String getAdapterName() {
            return adapterName;
        }

        public long getDefaultFulfillmentDuration() {
            return defaultFulfillmentDuration;
        }

        public String getDescriptorFileExtension() {
            return descriptorFileExtension;
        }

        public String getDescriptorFileMimeType() {
            return descriptorFileMimeType;
        }

        public String toString() {
            return "Adapter Config: Name = " + adapterName +
                    ", Default Duration = " + defaultFulfillmentDuration +
                    ", Descriptor File Extension = " + descriptorFileExtension +
                    ", Descriptor File Mime Type = " + descriptorFileMimeType +
                    ", Base URI = " + baseURI;
        }
    }

    private static class AdapterInfoImpl implements AdapterInfo {

        private final String adapterName;

        private final long defaultDuration;

        private final String descriptorFileExtension;

        private final String descriptorFileMimeType;

        public AdapterInfoImpl(String adapterName, long defaultDuration,
                               String descriptorFileExtension, String descriptorFileMimeType
        ) {
            this.adapterName = adapterName;
            this.defaultDuration = defaultDuration;
            this.descriptorFileExtension = descriptorFileExtension;
            this.descriptorFileMimeType = descriptorFileMimeType;
        }

        public String getAdapterName() {
            return adapterName;
        }

        public long getDefaultFulfillmentDuration() {
            return defaultDuration;
        }

        public String getDescriptorFileExtension() {
            return descriptorFileExtension;
        }

        public String getDescriptorFileMimeType() {
            return descriptorFileMimeType;
        }

        public boolean equals(Object o) {
            if (o == this)
                return true;
            if (!(o instanceof AdapterInfo))
                return false;

            AdapterInfo adapterInfo = (AdapterInfo) o;

            boolean isAdapterNameEqual =
                    (adapterName != null && adapterName.equals(adapterInfo.getAdapterName())) ||
                            (adapterName != null && adapterName.equals(adapterInfo.getAdapterName()));

            boolean isDescriptorFileExtensionEqual =
                    (descriptorFileExtension != null && descriptorFileExtension.equals(adapterInfo.getDescriptorFileExtension())) ||
                            (descriptorFileExtension != null && descriptorFileExtension.equals(adapterInfo.getDescriptorFileExtension()));

            boolean isDescriptorFileMimeTypeEqual =
                    (descriptorFileMimeType != null && descriptorFileMimeType.equals(adapterInfo.getDescriptorFileMimeType())) ||
                            (descriptorFileMimeType != null && descriptorFileMimeType.equals(adapterInfo.getDescriptorFileMimeType()));


            boolean isDefaultDurationEqual = (defaultDuration == adapterInfo.getDefaultFulfillmentDuration());

            return (isAdapterNameEqual && isDescriptorFileExtensionEqual &&
                    isDescriptorFileMimeTypeEqual && isDefaultDurationEqual);
        }

        public int hashCode() {
            int result = 59;
            result = 7 * result + (adapterName != null ? adapterName.hashCode() : 0);
            result = 7 * result + (descriptorFileExtension != null ? descriptorFileExtension.hashCode() : 0);
            result = 7 * result + (descriptorFileMimeType != null ? descriptorFileMimeType.hashCode() : 0);
            result = 7 * result + (int) (defaultDuration ^ (defaultDuration >>> 32));
            return result;
        }

        public String toString() {
            return "Adapter Info: Name = " + adapterName +
                    ", Default Duration = " + defaultDuration +
                    ", Descriptor File Extension = " + descriptorFileExtension +
                    ", Descriptor File Mime Type = " + descriptorFileMimeType;
        }
    }
}
