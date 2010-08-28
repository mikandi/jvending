/*
 *  JVending
 *  Copyright (C) 2005  Shane Isbell
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