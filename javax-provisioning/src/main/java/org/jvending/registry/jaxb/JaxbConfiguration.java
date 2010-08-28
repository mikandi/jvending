/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 * JVending
 * Copyright (c) 2005 Shane Isbell.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by
 *        JVending (http://jvending.sourceforge.net/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The name "JVending" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact shane.isbell@gmail.com.
 *
 * 5. Products derived from this software may not be called "JVending"
 *    nor may "JVending" appear in their names without prior written
 *    permission of Shane Isbell.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 *
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
