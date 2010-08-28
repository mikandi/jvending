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
package javax.provisioning;

import java.util.List;
import java.util.Set;

/**
 * Interface representing the capabilities of a device. The names of the
 * standard capabilities are held in the Constants class.
 *
 * @see Constants
 */

public interface Capabilities {

    List<String> getCapability(String name);

    Set<String> getCapabilityNames();

}