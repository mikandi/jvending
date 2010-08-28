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
