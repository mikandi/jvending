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
package org.jvending.registry.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Unmarshaller for a JAXB file.
 *
 * @author Shane Isbell
 * @since 1.1.0
 */

public class JaxbConfiguration {
    private static Logger logger = Logger.getLogger("Configuration");

    private JaxbConfiguration() {
    }

    /**
     *  Unmarshalls a JAXB Config file. Validation set to true by default.
     *
     * @param is InputStream of the JAXB XML file.
     * @param packageName for the JAXB Binding
     * @return unmarshalled JAXB object
     * @throws IOException
     */
    public static Object parse(InputStream is, String packageName) throws IOException {
        return parse(is, packageName, true);
    }

    /**
     * Unmarshalls a JAXB Config file
     * @param is InputStream of the JAXB XML file.
     * @param packageName for the JAXB Binding
     * @param isValidating
     * @return unmarshalled JAXB object
     * @throws IOException
     */
    public static Object parse(InputStream is, String packageName, boolean isValidating) throws IOException {
         if (is == null) {
            logger.severe("JV-101-001: Could not load config file: Package Name = " + packageName);
            throw new IOException("InputStream is null");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Object o =  unmarshaller.unmarshal(is);
            return ((JAXBElement<?>) o).getValue();
        } catch (JAXBException e) {
        	e.printStackTrace();
            logger.log(Level.SEVERE, "JV-101-002: Could not load config file Package Name = " + packageName, e);
            throw new IOException("JV-101-002: Critical - Problem with Schema! " + packageName);
        }
    }

    /**
     * Marshals object to the stream
     * @param object
     * @param outputStream
     * @param packageName
     * @throws IOException
     */
    public static synchronized void marshal(Object object, OutputStream outputStream, String packageName)
            throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(object, outputStream);
        } catch (JAXBException e) {
            logger.log(Level.SEVERE, "JV-101-003: Could not unmarshal object: Package Name = " + packageName, e);
            throw new IOException("JV-101-003: Could not unmarshal object: Package Name = " + packageName);
        }
    }
}
