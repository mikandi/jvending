package org.jvending.provisioning.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jvending.provisioning.stocking.DataSink;
import org.jvending.provisioning.stocking.ProviderContext;
import org.jvending.provisioning.stocking.filter.FilterTask;
import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;
import org.jvending.provisioning.stocking.par.ProvisioningArchiveType;

final class StockingFactory {

    private static Logger logger = Logger.getLogger("StockingFactory");

    private StockingFactory() {
    }

    public static Map<String, byte[]> createContentMap(InputStream parFile, String tmpDirectory) throws IOException {
        String parFileName = saveParFile(parFile, tmpDirectory);
        return JarFileTranslator.translate(new JarFile(parFileName));
    }

    public static byte[] createParFile(Map<String, byte[]> contentMap) {//createParFile
        ByteArrayOutputStream stream = null;
        JarOutputStream jarStream = null;
        try {
            stream = new ByteArrayOutputStream();
            jarStream = new JarOutputStream(stream);

            for (String key : contentMap.keySet()) {
                byte[] value = contentMap.get(key);
                JarEntry entry = new JarEntry(key);
                jarStream.putNextEntry(entry);
                jarStream.write(value, 0, value.length);
                jarStream.closeEntry();
            }

        } catch (IOException e) {
            logger.log(Level.INFO, "JV-1850-001: Unable to create Par File: Content Map = " + contentMap, e);
        } finally {
            try {
                if (jarStream != null) jarStream.close();
            } catch (IOException e) {
                logger.log(Level.INFO, "JV-1850-002:Could not close connection", e);
            }
        }
        return stream.toByteArray();
    }

    public static DataSink createDataSink(String dataSinkClassName) {
        try {
            Class<?> c = Class.forName(dataSinkClassName);
            DataSink dataSink = (DataSink) c.newInstance();
            logger.finest("JV-1850-003: Instantiating data sink. Name = " + dataSinkClassName);
            return dataSink;
        } catch (Exception e) {
            logger.warning("JV-1850-004: Could not instantiate data sink: " +
                    dataSinkClassName);
        }
        return null;
    }

    public static FilterTask createFilterTask(ProviderContext providerContext,
                                              StockingHandlerConfig stockingHandlerConfig,
                                              String filterID,
                                              Map<String, byte[]> content,
                                              ProvisioningArchiveType provisioningArchive) {

        return new FilterTaskImpl(providerContext, stockingHandlerConfig, filterID, content, provisioningArchive);

    }

    public static ProvisioningArchiveType createProvisioningArchive(InputStream is) throws IOException {
        if (is == null) {
            logger.info("JV-1850-005: No provisioning descriptor");
            throw new IOException("JV-1850-005: No provisioning descriptor");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("org.jvending.provisioning.stocking.par");
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            //unmarshaller.setValidating(true);
            return (ProvisioningArchiveType) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            logger.log(Level.INFO, "JV-1850-006: Parsing exception for provisioning descriptor", e);
            throw new IOException("JV-1850-006: Parsing exception for provisioning descriptor");
        }
    }


    private static String saveParFile(InputStream inputStream, String tmpDirectory) throws IOException {
        if (tmpDirectory == null) throw new IOException("JV-1850-007: Temporary Directory does not exist.");
        long tempId = UUID.randomUUID().hashCode();

        File dir = new File(tmpDirectory);
        if (!dir.exists()) {
            logger.info("JV-1850-008: Created output directory: Directory = " + dir.toString());
            dir.mkdir();
        }

        String parFileName = (tmpDirectory != null) ? tmpDirectory + "/" + tempId + ".par" : tempId + ".par";
        File file = new File(parFileName);
        file.createNewFile();
        logger.info("JV-1850-009: Created Par File: " + file.toURI().toString());

        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = inputStream.read(buffer)) >= 0) {
            fos.write(buffer, 0, n);
        }
        fos.close();
        logger.info("JV-1850-010: Added PAR file to file system. File Name = " + parFileName);
        return parFileName;
    }

    private static class FilterTaskImpl implements FilterTask {

        private ProviderContext providerContext;

        private StockingHandlerConfig stockingHandlerConfig;

        private String filterID;

        private Map<String, byte[]> content;

        private ProvisioningArchiveType provisioningArchive;

        FilterTaskImpl(ProviderContext providerContext,
                       StockingHandlerConfig stockingHandlerConfig,
                       String filterID,
                       Map<String, byte[]> content,
                       ProvisioningArchiveType provisioningArchive) {
            this.providerContext = providerContext;
            this.stockingHandlerConfig = stockingHandlerConfig;
            this.filterID = filterID;
            this.content = content;
            this.provisioningArchive = provisioningArchive;
        }

        public ProviderContext getProviderContext() {
            return providerContext;
        }

        public StockingHandlerConfig getStockingHandlerConfig() {
            return stockingHandlerConfig;
        }

        public String getFilterID() {
            return filterID;
        }

        public Map<String, byte[]> getContent() {
            return content;
        }

        public ProvisioningArchiveType getProvisioningArchive() {
            return provisioningArchive;
        }
    }

    private static class JarFileTranslator {

        static Map<String, byte[]> translate(JarFile jarFile) throws IOException {
            HashMap<String, byte[]> contentMap = new HashMap<String, byte[]>();

            for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();) {
                JarEntry entry = (JarEntry) entries.nextElement();
                contentMap.put(entry.getName(), copyStreamToBytes(jarFile.getInputStream(entry)));
            }
            return contentMap;
        }

        static private byte[] copyStreamToBytes(InputStream is) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while ((n = is.read(buffer)) >= 0) {
                os.write(buffer, 0, n);
            }
            return os.toByteArray();
        }
    }

}
