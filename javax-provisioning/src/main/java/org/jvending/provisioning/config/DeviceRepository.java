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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.provisioning.Capabilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jvending.provisioning.config.devices.Capability;
import org.jvending.provisioning.config.devices.DeviceMapping;
import org.jvending.provisioning.config.devices.DeviceType;
import org.jvending.provisioning.config.devices.DevicesType;
import org.jvending.provisioning.config.devices.MatchAll;
import org.jvending.provisioning.config.devices.MatchAny;
import org.jvending.provisioning.config.devices.MatchNot;
import org.jvending.provisioning.config.devices.ObjectFactory;
import org.jvending.provisioning.config.devices.RequestMapping;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.jaxb.JaxbConfiguration;


/**
 * The <code>DeviceRepository</code> class is responsible for looking up the
 * appropriate capabilities and adapters for a device.
 *
 * @author Shane Isbell
 * @since 1.2a
 */
public final class DeviceRepository implements Repository {

    private static Logger logger = Logger.getLogger("DeviceRepository");//last log 19

    private List<DeviceMappingInternal> deviceMappings;

    private List<DeviceType> devices;

    private DevicesType devicesCopy;

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
        if (properties == null) throw new IOException("JV-1303-001: Null properties values for the device repository");
        if (inputStream == null) throw new IOException("JV-1303-002: Null inputstream for the device repository");

        String _package = (String) properties.get("binding-package");
        byte[] inputBytes = copyStreamToBytes(inputStream);

        DevicesType device;
		try {
			device = (DevicesType) JaxbConfiguration.parse(new ByteArrayInputStream(copyBytes(inputBytes)), _package);
			devicesCopy = (DevicesType) JaxbConfiguration.parse(new ByteArrayInputStream(copyBytes(inputBytes)), _package);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

        List<DeviceMapping> deviceMapping = device.getDeviceMapping();
        devices = Collections.unmodifiableList(device.getDevice());

        DeviceMappingMapper deviceMappingMapper = new DeviceMappingMapper();
        deviceMappings = Collections.unmodifiableList(deviceMappingMapper.map(deviceMapping));

    }

    public Set<String> getDeviceIds() {
        Set<String> set = new HashSet<String>();
        for (Iterator i = deviceMappings.iterator(); i.hasNext();) {
            DeviceMapping mapping = (DeviceMapping) i.next();
            String id = mapping.getIdentifier();
            if (id != null) set.add(id.trim());
        }
        return Collections.unmodifiableSet(set);//safe
    }

    /**
     * Accessor capabilities of the device that matches the HTTP headers.
     *
     * @param requestMap <code>Map</code> containing the HTTP request headers
     * @return <code>Capabilities</code> of the device that matched the requestMap
     */
    public Capabilities getCapabilities(Map<String, String> requestMap) {
        if(requestMap == null)
        {
            throw new IllegalArgumentException("requestMap");
        }
        String id = null; //getDeviceId(requestMap);
       
        for (Iterator i = deviceMappings.iterator(); i.hasNext();) {
            DeviceMappingInternal mapping = (DeviceMappingInternal) i.next();
            DeviceMatch match = mapping.getDeviceMatch();
            int j = match.match(requestMap);
            if (j == 1) {
                id = mapping.getIdentifier();
                if(id != null) id = id.trim();
                break;
            }
        }
        
        logger.fine("JV-1303-003: DEVICE ID:: " + id);
        if (id == null) {
            logger.fine("JV-1303-004: Http Headers = " + requestMap);
            return null;
        }

        return getCapabilitiesFor(id.trim());
    }

    /**
     * Accessor for the capabilities of the device.
     *
     * @param deviceId the identifier of the device
     * @return Capabilities of the device for the given id
     */
    public Capabilities getCapabilitiesFor(String deviceId) {
        Map<String, List<String>> capabilityMap = getCapabilitiesForPrivate(deviceId);
        return (capabilityMap != null) ? new CapabilitiesImpl(capabilityMap) : null;
    }

    /**
     * Gets the names of the adapters that can handle the provisioning interaction for the device. If deviceId is
     * null or empty, returns all adapters names. If deviceId is not found, returns an empty list.
     *
     * @param deviceId the identifier of the device
     * @return <code>List</code> that contains <code>String</code> objects denoting the names of the adapters.
     */
    public List<String> getAdapterNamesFor(String deviceId) {
        if (deviceId == null || deviceId.trim().equals("")) {
            logger.finest("JV-1303-005: Device Id is Null. Returning empty list of adapter names.");
            return new ArrayList<String>();
        }
        //TO DO: Check this method for any null pointer exceptions
        for (DeviceType deviceType : devices) {
            List<String> id = deviceType.getIdentifier();

            for (Iterator j = id.iterator(); j.hasNext();) {
                String identifier = ((String) j.next()).trim();
                if (identifier.equals(deviceId.trim())) {
                    List<String> adapterNames = new ArrayList<String>();
                    for (String adapterName : deviceType.getAdapterName()  ) {
                        adapterNames.add(adapterName.trim());
                    }
                    return Collections.unmodifiableList(adapterNames);
                }
            }

        }
        logger.finest("JV-1303-006: Device Id not found: Device Id = " + deviceId.trim());
        return new ArrayList<String>();
    }


    String getDeviceMapping(String deviceId) {
        StringWriter writer = new StringWriter();

        for (Iterator<DeviceMappingInternal> i = deviceMappings.iterator(); i.hasNext();) {
            DeviceMappingInternal mapping = (DeviceMappingInternal) i.next();
            String id = mapping.getIdentifier();
            if (id != null && id.equals(deviceId)) {
                try {
                    JAXBContext jc = JAXBContext.newInstance("org.jvending.provisioning.config.devices");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    m.marshal(mapping, writer);
                    return writer.toString();
                } catch (JAXBException e) {
                    logger.info("JV-1303-007: Could not marshal device mapping: Device Id = " + deviceId);
                }
            }
        }
        return null;
    }


    public DeviceConfig getDeviceConfig() {
        return new DeviceConfigImpl();
    }

    private byte[] copyStreamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n = 0;
        while ((n = is.read(buffer)) >= 0) {
            os.write(buffer, 0, n);
        }
        return os.toByteArray();
    }

    private byte[] copyBytes(byte[] input) {
        byte[] output = new byte[input.length];
        System.arraycopy(input, 0, output, 0, output.length);
        return output;
    }

    //returns copy
    private Map<String, List<String>> getCapabilitiesForPrivate(String deviceId) {
        if (deviceId == null) return null;

        for (DeviceType deviceType : devices) {
            List<String> id = deviceType.getIdentifier();

            for (String identifier : id) {
                if (identifier != null && identifier.trim().equals(deviceId.trim())) {
                    return toMap(deviceType.getCapability());//TO DO: optimize
                }
            }
        }
        return null;
    }

    private Map<String, List<String>> toMap(List<Capability> capabilities) {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for ( Capability capabilityType : capabilities) {
            String name = capabilityType.getCapabilityName();
            name = (name != null) ? name.trim() : "";

            String value = capabilityType.getCapabilityValue();
            value = (value != null) ? value.trim() : "";

            String[] tokenValues = value.split("[,]");
            int size = tokenValues.length;
            List<String> values = new ArrayList<String>();
            for (int j = 0; j < size; j++) {
                values.add(tokenValues[j].trim());
            }
            map.put(name, values);
        }
        return Collections.unmodifiableMap(map);
    }

    private static class DeviceMappingMapper {
        List<DeviceMappingInternal> map(List<DeviceMapping> deviceMappings) throws IOException {
            List<DeviceMappingInternal> dMap = new ArrayList<DeviceMappingInternal> ();

            for (DeviceMapping deviceMappingType : deviceMappings) {
                DeviceMappingInternal deviceMapping;
                String id = deviceMappingType.getIdentifier();

                RequestMapping requestMappingType = deviceMappingType.getRequestMapping();
                if (requestMappingType != null) {
                    deviceMapping = new DeviceMappingInternal(id, new MatchTerminal(requestMappingType));
                    dMap.add(deviceMapping);
                    continue;
                }

                MatchAll matchAllType = deviceMappingType.getMatchAll();
                if (matchAllType != null) {
                    deviceMapping = new DeviceMappingInternal(id, new MatchAllInternal(matchAllType));
                    dMap.add(deviceMapping);
                    continue;
                }

                MatchAny matchAnyType = deviceMappingType.getMatchAny();
                if (matchAnyType != null) {
                    deviceMapping = new DeviceMappingInternal(id, new MatchAnyInternal(matchAnyType));
                    dMap.add(deviceMapping);
                    continue;
                }

                MatchNot matchNotType = deviceMappingType.getMatchNot();
                if (matchNotType != null) {
                    deviceMapping = new DeviceMappingInternal(id, new MatchNotInternal(matchNotType));
                    dMap.add(deviceMapping);
                    continue;
                }
            }
            return dMap;
        }
    }

    private static class MatchAllInternal extends DeviceMatch {

        List<RequestMapping> terminals;

        List<MatchNot> nots;

        List<MatchAny> anys;

        MatchAllInternal(MatchAll all) {
            List<Object> list = all.getRequestMappingOrMatchAnyOrMatchNot();

            terminals = new ArrayList<RequestMapping>();
            nots = new ArrayList<MatchNot>();
            anys = new ArrayList<MatchAny>();

            for (Object o : list) {
                if (o instanceof MatchNot)
                    nots.add( (MatchNot) o);
                if (o instanceof MatchAny)
                    anys.add( (MatchAny) o);
                if (o instanceof RequestMapping)
                    terminals.add( (RequestMapping) o);
            }
        }

        /**
         * Matches the request to a match-all rule.
         *
         * @param requestMap <code>Map</code> containing request headers.
         * @return 0 denotes no match, 1 denotes a match
         */
        int match(Map<String, String> requestMap) {
            int result = matchAllTerminals(terminals, requestMap);
            if (result == 0) return 0;

            result = matchAny(anys, requestMap);
            if (result == 0) return 0;

            result = matchNot(nots, requestMap);
            if (result == 0) return 0;

            return 1;
        }
    }

    private static class MatchAnyInternal extends DeviceMatch {

        List<RequestMapping> terminals;

        List<MatchNot> nots;

        List<MatchAll> alls;

        MatchAnyInternal(MatchAny any) {
            List<Object> list = any.getRequestMappingOrMatchAllOrMatchNot();

            terminals = new ArrayList<RequestMapping>();
            nots = new ArrayList<MatchNot>();
            alls = new ArrayList<MatchAll>();

            for (Object o : list) {
                if (o instanceof MatchNot)
                    nots.add( (MatchNot) o);
                if (o instanceof MatchAll)
                    alls.add( (MatchAll) o);
                if (o instanceof RequestMapping)
                    terminals.add( (RequestMapping) o);
            }
        }

        /**
         * Matches the request to a match-any rule.
         *
         * @param requestMap <code>Map</code> containing request headers.
         * @return 0 denotes no match, 1 denotes a match
         */
        int match(Map<String, String> requestMap) {
            int result = matchAnyTerminal(terminals, requestMap);
            if (result == 1) return 1;
            if (alls.size() > 0) {
                result = matchAll(alls, requestMap);
                if (result == 1) return 1;
            }

            if (nots.size() > 0) {
                result = matchNot(nots, requestMap);
                if (result == 1) return 1;
            }
            return 0;
        }
    }

    private static class MatchNotInternal extends DeviceMatch {

        RequestMapping requestMapping;

        MatchAll all;

        MatchAny any;

        MatchNotInternal(MatchNot not) {
            this.requestMapping = not.getRequestMapping();
            this.all = not.getMatchAll();
            this.any = not.getMatchAny();
        }

        /**
         * Matches the request to a match-not rule.
         *
         * @param requestMap <code>Map</code> containing request headers.
         * @return 0 denotes no match, 1 denotes a match
         */
        int match(Map<String, String> requestMap) {
            int result = matchTerminal(requestMapping, requestMap);
            if (result == 1) return 0;

            List<MatchAll> alls = new ArrayList<MatchAll>();
            alls.add(all);
            result = matchAll(alls, requestMap);
            if (result == 1) return 0;

            List<MatchAny> anys = new ArrayList<MatchAny>();
            anys.add(any);
            result = matchAny(anys, requestMap);
            if (result == 1) return 0;
            return 1;
        }
    }

    private static class MatchTerminal extends DeviceMatch {

        private final String terminalName;

        private final Pattern pattern;

        MatchTerminal(RequestMapping terminal) {
            pattern = Pattern.compile(terminal.getHeaderValue().trim());
            terminalName = terminal.getHeaderName().trim();
        }

        int match(Map<String, String> requestMap) {
            //Will not match any device, in this case
            if (requestMap == null || !requestMap.containsKey(terminalName)) {
                logger.finest("JV-1303-008: Terminal Name = " + terminalName + ", Map = " + requestMap);
                return 0;
            }

            String headerValue = (String) requestMap.get(terminalName);
            if (headerValue == null) return 0;
            headerValue = headerValue.trim();
            logger.finest("JV-1303-009: Terminal Name = " + terminalName + ", Header Value = " + headerValue);
            Matcher matcher = pattern.matcher(headerValue);
            return (matcher.matches()) ? 1 : 0;
        }
    }

    private static abstract class DeviceMatch {

        abstract int match(Map<String, String> requestMap);

        int matchTerminal(RequestMapping terminal, Map<String, String> requestMap) {
            MatchTerminal mt = new MatchTerminal(terminal);
            int x = mt.match(requestMap);
            logger.finest("JV-1303-010: Match terminal: Value = " + x);
            return x;
            // return mt.match(requestMap);
        }

        int matchAnyTerminal(List<RequestMapping> terminals, Map<String, String> requestMap) {
            int x = 0;
            for (Iterator i = terminals.iterator(); i.hasNext();) {
                x = matchTerminal((RequestMapping) i.next(), requestMap);
                logger.finest("JV-1303-011: Match any terminal: Value = " + x);
                if (x > 0) return 1;
            }
            return 0;
        }

        int matchAllTerminals(List<RequestMapping> terminals, Map<String, String> requestMap) {
            int x = 0;
            for (Iterator i = terminals.iterator(); i.hasNext();) {
                x = x + matchTerminal((RequestMapping) i.next(), requestMap);
            }
            return (x == terminals.size()) ? 1 : 0;
        }

        int matchNot(List <MatchNot>nots, Map<String, String> requestMap) {
            int x = 0;

            for (MatchNot not : nots) {
                MatchNotInternal ma = new MatchNotInternal(not);
                x = x + ma.match(requestMap);
            }
            return (x == nots.size()) ? 1 : 0;
        }

        int matchAll(List<MatchAll> alls, Map<String, String> requestMap) {
            int x = 0;

            for (MatchAll all : alls) {
                MatchAllInternal ma = new MatchAllInternal(all);
                x = x + ma.match(requestMap);
            }
            return (x == alls.size()) ? 1 : 0;
        }

        int matchAny(List<MatchAny> anys, Map<String, String> requestMap) {
            int x = 0;

            for (MatchAny any : anys) {
                MatchAnyInternal ma = new MatchAnyInternal(any);
                x = x + ma.match(requestMap);
                logger.finest("JV-1303-012: Match Any: Value = " + x);
                if (x > 0) return 1;
            }
            return 0;
        }
    }

    private static class DeviceMappingInternal {

        private String id;

        private DeviceMatch deviceMatch;

        DeviceMappingInternal(String id, DeviceMatch deviceMatch) {
            this.id = id;
            this.deviceMatch = deviceMatch;
        }

        String getIdentifier() {
            return id;
        }

        DeviceMatch getDeviceMatch() {
            return deviceMatch;
        }
    }

    private static class CapabilitiesImpl implements Capabilities {

        private final Map<String, List<String>>  capability;

        //map of capabilites for a specific device
        public CapabilitiesImpl(Map<String, List<String>> capability) {
            this.capability = new HashMap<String, List<String>>(capability);
        }

        //List of Strings: Values of capability
        public List<String> getCapability(String name) {
            if (name == null) return null;
            List<String> list = capability.get(name);
            return (list == null) ? null : Collections.unmodifiableList(list);
        }

        public Set<String> getCapabilityNames() {
            Set<String> c = capability.keySet();
            if (c == null) c = new TreeSet<String>();
            return Collections.unmodifiableSet(new TreeSet<String>(c));
        }

    }

    private class DeviceConfigImpl implements DeviceConfig {

        private ObjectFactory factory = new ObjectFactory();

        public DevicesType getDevices() {
            return devicesCopy;
        }

        public void writeDevicesXml(OutputStream outputStream) throws IOException {
            JaxbConfiguration.marshal(devicesCopy, outputStream, "org.jvending.provisioning.config.devices");
        }

        public void addDevice(String deviceId, String[] adapters, Map<String, String> capabilities, String userAgent) throws IOException {
            try {
                DeviceType device = createDevice(deviceId, adapters, capabilities);
                DeviceMapping deviceMapping = createDeviceMapping(deviceId, userAgent);
                if (device != null && deviceMapping != null) {
                    devicesCopy.getDeviceMapping().add(deviceMapping);
                    devicesCopy.getDevice().add(device);
                }
            } catch (JAXBException e) {
                logger.config("JV-1303-013: Failed to add device: Device Id = " + deviceId);
                throw new IOException();
            }
        }

        public boolean deleteDevice(String deviceId) {
            return deleteDeviceInternal(deviceId);
        }

        private DeviceType createDevice(String deviceId, String[] adapters, Map<String, String> capabilities) throws JAXBException {
            DeviceType device = factory.createDeviceType();
            device.getIdentifier().add(deviceId);
            int adapterSize = adapters.length;
            for (int i = 0; i < adapterSize; i++) {
                device.getAdapterName().add(adapters[i]);
            }
            List<Capability> cap = device.getCapability();
            for (Iterator i = capabilities.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) capabilities.get(key);
                Capability capability = factory.createCapability();
                capability.setCapabilityName(key);
                capability.setCapabilityValue(value);
                cap.add(capability);
            }
            return device;
        }

        private DeviceMapping createDeviceMapping(String deviceId, String userAgent) throws JAXBException {
            DeviceMapping deviceMapping = factory.createDeviceMapping();
            RequestMapping requestMapping = factory.createRequestMapping();
            requestMapping.setHeaderName("user-agent");
            requestMapping.setHeaderValue(userAgent);

            deviceMapping.setIdentifier(deviceId);
            deviceMapping.setRequestMapping(requestMapping);
            return deviceMapping;
        }

        private boolean deleteDeviceMapping(String deviceId) {
            if (deviceId == null) {
                logger.info("JV-1303-014: Device id is null. Nothing to delete");
                return false;
            }
            List<DeviceMapping> deviceMappings = devicesCopy.getDeviceMapping();

            for (Iterator<DeviceMapping> i = deviceMappings.iterator(); i.hasNext();) {
                DeviceMapping deviceMappingType = i.next();
                String id = deviceMappingType.getIdentifier();
                if (id != null && id.trim().equals(deviceId)) {
                    i.remove();
                    logger.config("JV-1303-015: Removed Device Mapping: Device Id = " + deviceId);
                    return true;
                }
            }
            logger.info("JV-1303-016: Could not find device mapping: Device Id = " + deviceId);
            return false;
        }

        private boolean deleteDeviceInternal(String deviceId) {
            if (deviceId == null) {
                logger.info("JV-1303-017: Device id is null. Nothing to delete");
                return false;
            }
            List<DeviceType> devices = devicesCopy.getDevice();

            for (Iterator<DeviceType> i = devices.iterator(); i.hasNext();) {
                DeviceType deviceType = i.next();
                List<String> ids = deviceType.getIdentifier();

                for (String id : ids ) {
                    if (id != null && id.trim().equals(deviceId)) {
                        if (deleteDeviceMapping(deviceId)) {
                            i.remove();
                            logger.config("JV-1303-018: Removed Device: Device Id = " + deviceId);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
            logger.info("JV-1303-019: Could not find device: Device Id = " + deviceId);
            return false;
        }
    }
}