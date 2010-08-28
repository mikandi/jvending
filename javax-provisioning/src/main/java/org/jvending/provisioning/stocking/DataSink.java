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
package org.jvending.provisioning.stocking;

import java.io.IOException;
import java.io.InputStream;

import org.jvending.provisioning.stocking.handler.StockingHandlerConfig;

/**
 * Provides a service for stocking PAR files into the bundle repository. An implementation of this interface will
 * be the final sink, after 0 or more filters are applied, for the PAR file. The <code>DataSink</code> is determined
 * within the stocking-handlers.xml file.
 *
 * @author Shane Isbell
 * @since 1.3a
 */

public interface DataSink {

    /**
     * Stocks a PAR file within the bundle repository.
     *
     * @param inputStream  PAR file
     * @return par file id
     * @throws IOException
     */
    long addParFile(InputStream inputStream) throws IOException;

    /**
     * Initializes the <code>DataSink</code>
     *
     * @param config
     */
    void init(StockingHandlerConfig config);

    /**
     * Accessor for the <code>DataSink</code> name.
     *
     * @return datasink name
     */
    String getDataSinkName();

}