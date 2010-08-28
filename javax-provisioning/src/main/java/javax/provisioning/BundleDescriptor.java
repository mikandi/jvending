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
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.jvending.provisioning.model.clientbundle.ClientBundle;

/**
 * Represents a single client bundle.
 *
 * @author Shane Isbell
 */

public interface BundleDescriptor {

    /**
     * Returns the content identifier for this bundle, as specified in the content-id element of the provisioning
     * descriptor. All bundles which are different versions of the same content have the same content identifier. Also,
     * there can be bundles with the same content identifier and the same version, but which are targetted at different
     * locales or at different types of client device, in which case those bundles would have different device
     * requirements.
     *
     * @return the content identifier for this bundle, as specified in the content-id element of the provisioning
     *         descriptor
     */
    String getContentID();
    
    /**
     * Not in spec
     */
    long getEventID();

    /**
     * Returns a unique id for this bundle. The provisioning framework is responsible for ensuring the uniqueness of
     * this ID.
     *
     * @return a unique id for this bundle
     */
    String getBundleID();

    /**
     * Returns the BundleType characterising this bundle.
     *
     * @return the BundleType characterising this bundle
     */
    BundleType getBundleType();

    /**
     * Returns the value of the catalog property for this bundle for the given name, or null if there is no property
     * with that name.
     *
     * @param key the catalog key value
     * @return the value of the catalog property for this bundle for the given name, or null if there is no property
     *         with that name.
     */
    String getCatalogProperty(String key);

    /**
     * Return the set of catalog property names for this bundle, or the empty set if there are none.
     *
     * @return the set of catalog property names for this bundle, or the empty set if there are none
     */
    Set<String> getCatalogPropertyNames();

    /**
     * Returns a <code>Deliverable</code> for the content file for this Bundle, or null if this bundle does not have
     * a content-file declaration
     *
     * @return a <code>Deliverable</code> for the content file for this Bundle, or null if this bundle does not have
     *         a content-file declaration
     */
    Deliverable getContentFile();

    /**
     * Returns the copyright notice for this bundle, or an empty string if no copyright notice was specified.
     *
     * @param locale the locale of the copyright
     * @return the copyright notice for this bundle, or an empty string if no copyright notice was specified
     */
    String getCopyright(Locale locale);

    /**
     * Returns a user description of this Bundle suitable for the given locale, or an empty string if none was specified.
     *
     * @param locale the locale of the description
     * @return a user description of this Bundle suitable for the given locale, or an empty string if none was specified
     */
    String getDescription(Locale locale);

    /**
     * Returns a <code>Deliverable</code> for the descriptor file of this bundle, or null if this bundle does not have
     * a descriptor-file declaration. For example, in the case of a J2ME MIDP application, this returns a URL to the
     * JAD file.
     *
     * @return a <code>Deliverable</code> for the descriptor file of this bundle, or null if this bundle does not have
     *         a descriptor-file declaration
     */
    Deliverable getDescriptorFile();

    /**
     * Returns a user display name for this bundle suitable for the given locale, or an empty string if none was
     * specified.
     *
     * @param locale the locale of the display name
     * @return a user display name for this bundle suitable for the given locale, or an empty string if none was
     *         specified
     */
    String getDisplayName(Locale locale);

    /**
     * Returns a List of Deliverable objects representing icons that could be displayed to the user, or the empty
     * list if there aren't any that are appropriate for the specified locale. Note that all the returned
     * Deliverables will have different contentTypes, at most one of which can be null.
     *
     * @param locale the locale of the icons
     * @return a List of <code>Deliverable</code> objects representing icons that could be displayed to the user, or
     *         the empty list if there aren't any that are appropriate for the specified locale
     */
    List<Deliverable> getIcons(Locale locale);

    /**
     * Returns a Set of all the Locales used by this BundleDescriptor.
     *
     * @return a Set of all the Locales used by this BundleDescriptor
     */
    public Set<Locale> getLocales();

    /**
     * Returns a number identifying the PAR file that contained this bundle.
     *
     * @return a number identifying the PAR file that contained this bundle
     */
    long getParFileID();

    /**
     * Returns a List of <code>Deliverable</code> objects representing previews that could be displayed to the user,
     * or the empty list if there aren't any that are appropriate for the specified locale. Note that all the
     * returned Deliverables will have different contentTypes, at most one of which can be null.
     *
     * @param locale the locale of the previews
     * @return a List of <code>Deliverable</code> objects representing previews that could be displayed to the user,
     *         or the empty list if there aren't any that are appropriate for the specified locale
     */
    List<Deliverable> getPreviews(Locale locale);

    /**
     * Returns the values of a particular requirement of this bundle as a List, or null if there is no requirement with
     * that name.
     *
     * @param name the name of the requirement
     * @return the values of a particular requirement of this bundle as a List, or null if there is no requirement with
     *         that name
     */
    public List<String> getRequirement(String name);

    /**
     * Returns a set of the requirement names for this bundle, or the empty set if there are none.
     *
     * @return a set of the requirement names for this bundle, or the empty set if there are none
     */
    public Set<String> getRequirementNames();

    /**
     * Returns an absolute URL for the specified resource. If the resourceSpec represents an absolute URI
     * (that is, it begins with a scheme such as "http:") then this is the same as calling new URL(resourceSpec).
     * <p/>
     * If the resourceSpec does not represent an absolute URI, it is interpreted as being relative to the root of the
     * PAR file in which this bundle was defined. In this case, an IOException will be thrown if the identified
     * resource was not in the PAR file.
     * For example, getResource("/META-INF/provisioning.xml") returns a URL that can be used to load the content of
     * the provisioning descriptor.
     *
     * @param resourceSpec
     * @return an absolute URL for the specified resource
     * @throws IOException
     */
    URL getResource(String resourceSpec) throws IOException;

    /**
     * Returns the approximate time that the bundle was added to the repository, measured in milliseconds since midnight
     * January 1, 1970 UTC.
     *
     * @return the approximate time that the bundle was added to the repository, measured in milliseconds since midnight
     *         January 1, 1970 UTC
     */
    long getUploadTime();

    /**
     * Returns the vendor name of this bundle, or an empty string if no vendor name was specified.
     *
     * @param locale the locale of the vendor
     * @return the vendor name of this bundle, or an empty string if no vendor name was specified
     */
    String getVendor(Locale locale);

    /**
     * Returns the vendor description of this bundle, or an empty string if no vendor description was specified.
     *
     * @param locale the locale of the vendor description
     * @return the vendor description of this bundle, or an empty string if no vendor description was specified
     */
    String getVendorDescription(Locale locale);

    /**
     * Returns the vendor URL of this bundle, or an empty string if no vendor URL was specified.
     *
     * @param locale the locale of the vendor URL
     * @return the vendor URL of this bundle, or an empty string if no vendor URL was specified
     */
    String getVendorURL(Locale locale);

    /**
     * Returns the version of this bundle, or an empty string if no version was specified.
     *
     * @return the version of this bundle, or an empty string if no version was specified
     */
    String getVersion();
    
    /**
     * Not part of spec
     */
    void close();
    
    ClientBundle getClientBundle();
    
    Session getSession();

}