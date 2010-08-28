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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.provisioning.AdapterInfo;
import javax.provisioning.adapter.Adapter;
import javax.provisioning.adapter.AdapterConfig;
import javax.provisioning.adapter.AdapterContext;

import org.jvending.provisioning.config.AdapterRepository;
import org.jvending.provisioning.config.adapters.AdapterType;
import org.jvending.provisioning.config.adapters.DescriptorFile;
import org.jvending.provisioning.config.adapters.InitParam;

/**
 * @author Shane Isbell
 * @since 2.0.0
 */
public final class AdapterRepositoryWrapper {//TODO: Fix: Exposed as public for the stocking tool, create mediator
//load adaptercontext here

    private static Logger logger = Logger.getLogger("AdapterRepositoryWrapper");

    private Map<String, AdapterConfig> adapterConfigs;

    private List<AdapterInfo> adapterInfos;

    private Map<String, Adapter> adapters;

    private List<String> adapterNames;

    private AdapterContext adapterContext;

    public AdapterRepositoryWrapper(AdapterRepository adapterRepository, AdapterContext adapterContext) {
        if (adapterRepository == null) {
            logger.severe("JV-1503-001: The AdapterRepository is null");
            return;
        }

        if (adapterContext == null) {
            logger.severe("JV-1503-002: The AdapterContext is null");
            return;
        }

        this.adapterContext = adapterContext;
        adapterConfigs = new HashMap<String, AdapterConfig>();
        adapterInfos = new ArrayList<AdapterInfo>();
        adapters = new HashMap<String, Adapter>();
        adapterNames = new ArrayList<String>();

        List<AdapterType> adapterTypes = adapterRepository.getAdapters();
        for (AdapterType adapterType : adapterTypes) {
            String adapterName = adapterType.getAdapterName().trim();
            adapterNames.add(adapterName);
            long duration = 0L;
            try {
                duration = Long.parseLong(adapterType.getFulfillmentDuration().trim());
            } catch (NumberFormatException e) {
                logger.info("JV-1503-003: Problem with fulfillment duration. Setting to never expire.");
            }

            DescriptorFile df = adapterType.getDescriptorFile();
            String fileExtension = (df != null) ? df.getExtension().trim() : null;
            String mimeType = (df != null) ? df.getMimeType().trim() : null;
            String baseUri = adapterType.getBaseUri().trim();

            Map<String, String> initParam = toMap(adapterType.getInitParam());
            String className = adapterType.getAdapterClass().trim();

            AdapterConfig adapterConfig = AdapterFactory.createAdapterConfig(adapterName,
                    duration, fileExtension, mimeType, baseUri, initParam, className,
                    adapterContext
            );

            adapterConfigs.put(adapterName, adapterConfig);//TODO: Store only once
            adapterInfos.add((AdapterInfo) adapterConfig);
            //Spec: There must be only one instance of an adapter per JVM. This impl ok if the wrapper is only
            //invoked once. This impl makes it difficult, however, to destroy and adapter and then re init it.
            adapters.put(adapterName, getAdapterFor(className, adapterConfig));//could put in null adapter here.
            logger.info("JV-1503-004: Loaded adapter: ClassName = " + className + ", " + adapterConfig.toString());
        }
    }

    public List<String> getAdapterNames() {
        return Collections.unmodifiableList(adapterNames);
    }

    public AdapterConfig getAdapterConfigFor(String adapterName) {
        return (AdapterConfig) adapterConfigs.get(adapterName);
    }

    public AdapterInfo getAdapterInfoFor(String adapterName) {
        if (adapterName == null) return null;
        else adapterName = adapterName.trim();

        for ( AdapterInfo adapterInfo  : adapterInfos) {
            String adapterName2 = adapterInfo.getAdapterName();
            if (adapterName.equals(adapterName2)) return adapterInfo;
        }
        return null;
    }

    public Adapter getAdapterFor(String adapterName) {
        return (Adapter) adapters.get(adapterName);//could return null
    }

    public List<AdapterInfo> getAdapterInfos() {
        return Collections.unmodifiableList(adapterInfos);
    }

    public AdapterContext getAdapterContext() {
        return adapterContext;
    }


    private Map<String, String> toMap(List<InitParam> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (InitParam type : list) {
            String name = type.getParamName();
            String value = type.getParamValue();
            map.put(name.trim(), value.trim());
        }
        return map;
    }

    private Adapter getAdapterFor(String className, AdapterConfig adapterConfig) {
        Adapter adapter = null;
        try {
            Class<?> adapterClass = Class.forName(className);
            Constructor<?> adapterConstructor = adapterClass.getConstructor((Class[]) null);
            adapter = (Adapter) adapterConstructor.newInstance((Object[]) null);
            adapter.init(adapterConfig);//Spec, to initialize
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("JV-1503-005: Could not find or intialize adapter: " + className);
        }
        return adapter;
    }
}