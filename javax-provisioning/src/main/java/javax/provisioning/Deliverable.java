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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class representing a file that could be delivered to a client. Subclasses may have methods that allow
 * the file to be modified.
 *
 * @author Shane Isbell
 * @version 1.0
 */


public class Deliverable {

    private static Logger logger = Logger.getLogger("Deliverable");

    private final URL url;

    private final String mimeType;

    /**
     * Constructor
     *
     * @param url      the URL of this deliverable
     * @param mimeType the mime-type of this deliverable
     */
    public Deliverable(URL url, String mimeType) {
        this.url = url;
        this.mimeType = mimeType;
    }

    /**
     * Client is responsible for closing inputstream on failure.
     */
    public InputStream getInputStream() throws IOException {
        if (url == null) throw new IOException("JV-1000-001: URL Value is Null");
        logger.finest("JV-1000-002: Obtaining Input Stream: " + toString());
        return getURL().openStream();
    }

    /**
     * Returns the mime-type for this deliverable.
     *
     * @return the mime-type for this deliverable
     */
    public String getMimeType() {
        return mimeType;
    }

    public URL getURL() {
        return url;
    }

    /**
     * Client is responsible for closing outputstream. This allows the client to append to the
     * outputstream;
     */

    public void writeContents(OutputStream os) throws IOException {
        InputStream is = getInputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = is.read(buffer)) >= 0) {
            os.write(buffer, 0, n);
        }
    }

    public String toString() {
        return "URL = " + url + ", Mime Type = " + mimeType;
    }
}
