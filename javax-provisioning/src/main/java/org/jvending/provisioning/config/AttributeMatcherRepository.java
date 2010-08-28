/*
 *   JVending
 *   Copyright (C) 2004  Shane Isbell
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


package org.jvending.provisioning.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.provisioning.matcher.AttributeMatcher;

import org.jvending.provisioning.config.matchers.InitParam;
import org.jvending.provisioning.config.matchers.MatcherType;
import org.jvending.provisioning.config.matchers.MatchersType;
import org.jvending.registry.Repository;
import org.jvending.registry.RepositoryRegistry;
import org.jvending.registry.jaxb.JaxbConfiguration;

/**
 * @author Shane Isbell
 * @since 1.3a
 */

public final class AttributeMatcherRepository implements Repository {

    private static Map<String, AttributeMatcher> matcherMap = new HashMap<String, AttributeMatcher>();

    private static Logger logger = Logger.getLogger("AttributeMatcherRepository");

    public AttributeMatcher getMatcherFor(String attributeName) {
        return (AttributeMatcher) matcherMap.get(attributeName);
    }

    public void load(InputStream inputStream, Hashtable<String, String> properties) throws IOException {
        if (properties == null)
            throw new IOException("JV-1301-001: Null properties values for the attribute matcher repository");
        if (inputStream == null)
            throw new IOException("JV-1301-002: Null inputstream for the attribute matcher repository");

        MatchersType matchers = (MatchersType) JaxbConfiguration.parse(inputStream, (String) properties.get("binding-package"));

        for (MatcherType matcher : matchers.getMatcher() ) {
            String attributeName = matcher.getAttributeName();
            List<InitParam> initParam = matcher.getInitParam();
            AttributeMatcher attributeMatcher = null;
            try {
                String matcherClassName = matcher.getMatcherClass();
                Class<?> matcherClass = Class.forName(matcherClassName);
                Constructor<?> matcherConstructor = matcherClass.getConstructor((Class[]) null);
                attributeMatcher = (AttributeMatcher) matcherConstructor.newInstance((Object[]) null);
                attributeMatcher.init(attributeName, toMap(initParam));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "JV-1301-003: Problem loading the attribute matcher repository", e);
                throw new IOException("JV-1301-004: Problem loading the attribute matcher repository");
            }
            matcherMap.put(attributeName, attributeMatcher);
        }
    }

    private Map<String, String> toMap(List<InitParam> initParam) {
        Map<String, String> map = new HashMap<String, String>();
        for (InitParam initParamType : initParam ) {
            String name = initParamType.getParamName();
            String value = initParamType.getParamValue();
            map.put(name.trim(), value.trim());
        }
        return map;
    }

    public void setRepositoryRegistry(RepositoryRegistry repositoryRegistry) {
        //don't need this
    }

}