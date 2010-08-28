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
package org.jvending.provisioning.deliverable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.Deliverable;

/**
 * JAD implementation of the DescriptorFile. If extending this file,
 *
 * @author Shane Isbell
 * @version 1.0
 */

public final class JadDescriptorFile extends BaseDescriptorFile {

    private static Logger logger = Logger.getLogger("JadDescriptorFile");

    private Properties jadProp;

    private final URL url;

    private final String mimeType;

    /**
     * Constructor
     *
     * @param url          the URL of the descriptor. This URL is an internal one and should not be given out to
     *                     the device.
     * @param uri          URI of the descriptor. This URI is the one from the provisioning descriptor.
     * @param mimeType     mime-type of the descriptors
     * @param contentFiles list of content files associated with this JAD descriptor.
     */
    public JadDescriptorFile(URL url, URI uri, String mimeType, List<Deliverable> contentFiles) {
        super(url, uri, mimeType, contentFiles);
        this.url = url;
        this.mimeType = mimeType;
    }

    /**
     * Returns the application property value with the given name.
     *
     * @param name
     * @return the application property value with the given name
     */
    public String getAppProperty(String name) {
        if (jadProp == null) loadProperties();
        return jadProp.getProperty(name);
    }

    /**
     * Sets application property on the JAD.
     *
     * @param name  the name of application property
     * @param value the value of application property
     */
    public void setAppProperty(String name, String value) {
        if (jadProp == null) loadProperties();
        jadProp.setProperty(name, value);
    }

    /**
     * Writes the JAD to the output stream. Class that invokes method is responsible for closing connection.
     *
     * @param os
     * @throws IOException
     */
    public final void writeContents(OutputStream os) throws IOException {
        if (jadProp == null) loadProperties();

        for (Enumeration<?> en = jadProp.propertyNames(); en.hasMoreElements();) {
            String key = (String) en.nextElement();
            String value = jadProp.getProperty(key);
            if (key != null && value != null) os.write((key + ": " + value + "\r\n").getBytes());
        }
    }

    public InputStream getInputStream() throws IOException {
        if (url == null) throw new IOException("JV-1251-001: URL Value is null");
        if (jadProp == null) loadProperties();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            writeContents(os);
        } catch (IOException e) {
            throw new IOException("JV-1251-002: Could not get InputStream.");
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                logger.info("JV-1251-003: Could not close connection.");
            }
        }
        return new ByteArrayInputStream(os.toByteArray());
    }

    private void loadProperties() {
        InputStream is = null;
        try {
            is = getURL().openStream();
            if (is == null) throw new IOException("JV-1251-004: URL Value is null");
            jadProp = new Properties();
            jadProp.load(is);
        } catch (IOException e) {
            logger.log(Level.INFO, "JV-1251-005:Could not load properties: URL = " + url +
                    ", Mime-Type = " + mimeType, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                logger.info("JV-1251-006: Could not close connection.");
            }
        }
    }
}