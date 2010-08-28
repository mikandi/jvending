package org.jvending.provisioning.dao;

import java.io.IOException;

/**
 * DAO for accessing the content.
 */
public interface ContentAccessorDAO {
    byte[] getDescriptorFor(String id) throws IOException;

    byte[] getContentFor(String id) throws IOException;

    byte[] getIconFor(String id, String fileName) throws IOException;

    byte[] getPreviewFor(String id, String fileName) throws IOException;

    byte[] getDescriptorContentFor(String id, String fileName) throws IOException;
}
