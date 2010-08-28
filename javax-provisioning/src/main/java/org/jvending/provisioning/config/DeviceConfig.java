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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.jvending.provisioning.config.devices.DevicesType;

/**
 * @author Shane Isbell
 * @since 1.3a
 */

public interface DeviceConfig {

    public DevicesType getDevices();

    public boolean deleteDevice(String deviceId);

    public void addDevice(String deviceId, String[] adapters, Map<String, String> capabilities, String userAgent) throws IOException;

    public void writeDevicesXml(OutputStream outputStream) throws IOException;

}