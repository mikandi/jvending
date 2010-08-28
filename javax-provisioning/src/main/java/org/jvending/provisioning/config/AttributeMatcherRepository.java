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