/*
 *  JVending
 *  Copyright (C) 2004  Shane Isbell
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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