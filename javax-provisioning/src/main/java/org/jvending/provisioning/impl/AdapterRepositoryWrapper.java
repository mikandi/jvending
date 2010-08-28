/*
 *   JVending
 *   Copyright (C) 2005  Shane Isbell
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