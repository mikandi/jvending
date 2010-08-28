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
package org.jvending.provisioning.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jvending.registry.PropertyRepository;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */

public final class MimeTypeRepository extends PropertyRepository {

    private static Logger logger = Logger.getLogger("MimeType");

    public String getMimeTypeFromExtension(String extension) {
        return properties.getProperty(extension);
    }

    public String getMimeTypeFromUri(String fileName) {
        try {
            String[] fileTokens = (fileName != null) ? fileName.split("[.]") : null;
            if (fileTokens == null || fileTokens.length < 2) return null;
            String fileExtension = fileTokens[fileTokens.length - 1].trim();
            return getMimeTypeFromExtension(fileExtension);
        } catch (Exception e) {
            logger.log(Level.INFO, "JV-1304-001: Could not get the mime-type: File Name = " + fileName, e);
            return null;
        }
    }
}