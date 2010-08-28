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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jvending.provisioning.stocking.DataSink;
import org.jvending.provisioning.stocking.ProviderContext;
import org.jvending.provisioning.stocking.filter.FilterTask;
import org.jvending.provisioning.stocking.filter.StockingFilter;
import org.jvending.provisioning.stocking.handler.StockingHandler;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

final class StockingHandlerImpl extends StockingHandler {

    private static Logger logger = Logger.getLogger("StockingHandlerImpl");

    public long addParFile(InputStream inputStream, ProviderContext providerContext) throws IOException {

        DataSink dataSink = stockingHandlerConfig.getDataSink();
        List<StockingFilter> filters = stockingHandlerConfig.getStockingFilters();

        if (inputStream == null) {
            logger.info("JV-1852-001: Could not store PAR file. The input stream was null");
            throw new IOException("JV-1852-001: Could not store PAR file. The input stream was null");
        }
        long parId = 0;

        if (dataSink == null) {
            logger.log(Level.WARNING, "JV-1852-002: Could not instantiate data sink. Aborting Stocking of Par File.");
            throw new IOException("JV-1852-002: Could not instantiate data sink. Aborting Stocking of Par File.");
        }

        if (filters.size() == 0) {
            parId = dataSink.addParFile(inputStream);
            logger.info("JV-1852-003: Completed stocking of PAR: Par ID = " + parId + ", Data Sink = "
                    + dataSink.getDataSinkName());
            return parId;
        }

        Map<String, byte[]> contentMap = StockingFactory.createContentMap(inputStream, stockingHandlerConfig.getInitParameter("par-file-output"));
        if (contentMap == null) {
            logger.info("JV-1852-004: Could not convert stream to map. Aborting Stocking of Par File: Par ID = "
                    + parId);
            throw new IOException("JV-1852-004: Could not convert stream to map. Aborting Stocking of Par File: Par ID = "
                    + parId);
        }
        ProvisioningArchiveType archive = getProvisioningArchive(contentMap);
        String filterID = UUID.randomUUID().toString();
        FilterTask filterTask = StockingFactory.createFilterTask(providerContext, stockingHandlerConfig,
                filterID, contentMap, archive );

        //process filters
        try {
            for (StockingFilter stockingFilter : filters) {
                stockingFilter.doFilter(filterTask);
                contentMap.put("META-INF/provisioning.xml", parToString(archive).getBytes());
            }

            byte[] parBytes = StockingFactory.createParFile(contentMap);
            parId = dataSink.addParFile(new ByteArrayInputStream(parBytes));
            logger.info("JV-1852-005: Completed stocking of PAR. Par ID = " + parId +
                    ", Data Sink = " + dataSink.getDataSinkName());
        } catch (IOException e) {
            logger.info("JV-1852-006: Stocking of PAR failed.");
            throw new IOException("JV-1852-006: Stocking of PAR failed.");
        }
        return parId;
    }

    private String parToString(ProvisioningArchiveType par) {
        StringWriter writer = new StringWriter();
        try {
            JAXBContext jc = JAXBContext.newInstance("org.jvending.provisioning.stocking.par");
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
            m.marshal(par, writer);
        } catch (JAXBException e) {
            logger.log(Level.INFO, "JV-1852-007: Problem with provisioning descriptor", e);
        }
        return writer.toString();
    }

    private ProvisioningArchiveType getProvisioningArchive(Map<String, byte[]> contentMap) throws IOException {
        if (contentMap == null) {
            logger.info("JV-1852-008: Content map is null.");
            throw new IOException("JV-1852-008: Content map is null.");
        }

        if (!contentMap.containsKey("META-INF/provisioning.xml")) {
            logger.info("JV-1852-009: Stocking failed. PAR file does not contain a provisioning descriptor.");
            throw new IOException("JV-1852-009: Stocking failed. PAR file does not contain a provisioning descriptor.");
        }

        byte[] content = (byte[]) contentMap.get("META-INF/provisioning.xml");
        logger.finest("JV-1852-010: BYTE SIZE = " + content.length);
        return (StockingFactory.createProvisioningArchive(new ByteArrayInputStream(content)));
    }
}
