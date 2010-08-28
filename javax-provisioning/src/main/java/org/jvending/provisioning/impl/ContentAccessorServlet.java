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
package org.jvending.provisioning.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jvending.provisioning.dao.ContentAccessorDAO;
import org.jvending.registry.hibernate.HibernateDAORegistry;

/**
 * Grabs content from a data source.
 *
 * @author Shane Isbell
 * @since 2.0.0
 */

public final class ContentAccessorServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1896742083540526333L;
	
	private static Logger logger = Logger.getLogger("ContentAccessorServlet");

    /**
     * Processes the http request and response for content. The required URL parameters are id, type and filename. ID is
     * the UUID assigned by the framework. Type is one of the following: descriptor, icon, preview, descriptorContent.
     * fileName is the original name of the content file as contained within the PAR file.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @throws IOException
     * @throws ServletException
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        HibernateDAORegistry hibernateRegistry = (HibernateDAORegistry)
                this.getServletContext().getAttribute("org.jvending.registry.hibernate.HibernateDAORegistry");

        String bundleId = request.getParameter("id");
        String type = request.getParameter("type");
        String fileName = request.getParameter("fileName");
        logger.info("Making content accessor request: ID = " + bundleId +
                ", Type = " + type);

        OutputStream os = null;

        ContentAccessorDAO contentDAO = (ContentAccessorDAO) hibernateRegistry.find("dao:content-accessor");
        if (contentDAO == null) {
            logger.severe("JV-Could not locate the ContentAccessorDAO");
        }
        byte[] content = null;
        try {
            if (type.equals("descriptor")) {
                logger.info("Getting descriptor from data source: Bundle Id = " +
                        bundleId);
                content = contentDAO.getDescriptorFor(bundleId);
            } else if (type.equals("content")) {
                logger.info("Getting content from data source: Bundle Id = " +
                        bundleId);
                content = contentDAO.getContentFor(bundleId);
            } else if (type.equals("icon")) {
                logger.info("Getting icon from data source: Bundle Id = " + bundleId);
                content = contentDAO.getIconFor(bundleId, fileName);
            } else if (type.equals("preview")) {
                logger.info("Getting preview from data source: Bundle Id = " + bundleId +
                        ", File Name = " + fileName);
                content = contentDAO.getPreviewFor(bundleId, fileName);
            } else if (type.equals("descriptorContent")) {
                logger.info("Getting descriptor content from data source: Bundle Id = " + bundleId +
                        ", File Name = " + fileName);
                content = contentDAO.getDescriptorContentFor(bundleId, fileName);
            }
            if (content == null) {
                logger.info("Could not obtain requested content: Id = " + bundleId +
                        ", type = " + type + ", File Name = " + fileName);
                return;
            }

            os = response.getOutputStream();
            os.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}