package org.jvending.provisioning.dao.impl;

import java.io.IOException;
import java.sql.Blob;
import java.util.List;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jvending.provisioning.dao.BaseProvisioningDAO;
import org.jvending.provisioning.dao.ContentAccessorDAO;
import org.jvending.provisioning.model.clientbundle.ClientBundle;
import org.jvending.provisioning.model.clientbundle.ContentFile;
import org.jvending.provisioning.model.clientbundle.Icon;
import org.jvending.provisioning.model.clientbundle.Preview;
import org.jvending.provisioning.model.clientbundle.UserDescription;
import org.jvending.registry.RepositoryRegistry;

public final class ContentAccessorDAOImpl extends BaseProvisioningDAO implements ContentAccessorDAO {

    private static Logger logger = Logger.getLogger("DAO");

    private String dataSource = "hibernate3";

    public ContentAccessorDAOImpl(String className, String id) {
        super(className, id);
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {

    }

    @SuppressWarnings("unchecked")
	public byte[] getDescriptorFor(String id) throws IOException {
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select clientbundle.descriptorFile.content from ClientBundle as clientbundle " +
                    "where bundle_id='" + id + "'";
            Query query = session.createQuery(queryStatement);
            List<Blob> descriptors = query.list();
            Blob blob = (Blob) descriptors.get(0);
            return blob.getBytes(1L, (int) blob.length());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
	public byte[] getContentFor(String id) throws IOException {
        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select clientbundle.contentFile.content from ClientBundle as clientbundle " +
                    "where bundle_id='" + id + "'";
            Query query = session.createQuery(queryStatement);
            List<Blob> descriptors = query.list();
            Blob blob = (Blob) descriptors.get(0);
            return blob.getBytes(1L, (int) blob.length());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
	public byte[] getIconFor(String bundleId, String fileName) throws IOException {
        if (bundleId == null || fileName == null) throw new IOException("Invalid Parameters");

        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select clientbundle from ClientBundle as clientbundle " +
                    "where bundle_id='" + bundleId + "'";
            Query query = session.createQuery(queryStatement);
            ClientBundle bundle = (ClientBundle) query.list().get(0);

            UserDescription description = bundle.getUserDescription();
            if (description == null) return null;

            List<Icon> icons = description.getIcons();
            if (icons == null || icons.size() == 0) return null;

            for (Icon icon : icons) {
                String iconFileName = (String) icon.getFileUri();
                if (iconFileName == null) continue;
                if (iconFileName.equals(fileName)) {
                	return null;
               //     Blob blob = icon.getFile();
                //    return blob.getBytes(1L, (int) blob.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public byte[] getPreviewFor(String id, String fileName) throws IOException {
        if (id == null || fileName == null) return null;

        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select ClientBundle from ClientBundle as ClientBundle " +
                    "where bundle_id='" + id + "'";
            Query query = session.createQuery(queryStatement);
            ClientBundle bundle = (ClientBundle) query.list().get(0);

            List<Preview> previews = bundle.getPreviews();
            if (previews == null || previews.size() == 0) return null;

            for (Preview preview : previews) {
                String fileUri = (String) preview.getFileUri();
                if (fileUri == null) continue;
                if (fileUri.equals(fileName)) {
                	return null;
                 //   Blob blob = preview.getFile();
                 //   return blob.getBytes(1L, (int) blob.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public byte[] getDescriptorContentFor(String id, String fileName) throws IOException {
        if (id == null || fileName == null) return null;

        Session session = null;
        try {
            session = getSessionFactoryByName(dataSource).openSession();
            String queryStatement = "select ClientBundle from ClientBundle as ClientBundle " +
                    "where bundle_id='" + id + "'";
            Query query = session.createQuery(queryStatement);
            ClientBundle bundle = (ClientBundle) query.list().get(0);

            List<ContentFile> contentFiles = bundle.getContentFilesFromDescriptor();
            if (contentFiles == null || contentFiles.size() == 0) {
                logger.finest("No content available from descriptor: Bundle Id = " + id +
                        ", File Name = " + fileName);
                return null;
            }

            for (ContentFile contentFile : contentFiles) {
                String fileUri = contentFile.getFileUri();
                if (fileUri == null) continue;
                if (fileUri.equals(fileName)) {
                	return null;
                 //   Blob blob = contentFile.getContent();
                //    return blob.getBytes(1L, (int) blob.length());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException();
        } finally {
            try {
                if (session != null) session.close();
            } catch (HibernateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
