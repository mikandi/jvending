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