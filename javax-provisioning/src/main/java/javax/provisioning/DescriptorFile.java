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

import java.net.URL;
import java.util.List;

/**
 * Class representing a descriptor file that could be delivered to a client.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public abstract class DescriptorFile extends Deliverable {

    protected DescriptorFile(URL url, String contentType) {
        super(url, contentType);
    }

    public abstract String getAppProperty(String name);

    public abstract List<Deliverable> getContentFiles();

    public abstract void setAppProperty(String name, String value);

}