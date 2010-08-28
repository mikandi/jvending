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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.jvending.provisioning.config.stockinghandlers.BlackListType;
import org.jvending.provisioning.config.stockinghandlers.MimeAcceptType;
import org.jvending.provisioning.config.stockinghandlers.MimeBlockType;
import org.jvending.provisioning.config.stockinghandlers.PolicyType;
import org.jvending.provisioning.config.stockinghandlers.WhiteListType;

final class StockingPolicyFactory {

    private static Logger logger = Logger.getLogger("StockingPolicyFactory");

    private StockingPolicyFactory() {
    }

    public static org.jvending.provisioning.stocking.StockingPolicy createStockingPolicy(PolicyType policyType) {
        return PolicyTranslator.translate(policyType);
    }

    private static class PolicyTranslator {

        private static int defaultRemoteMaxSize = 100;

        private static int defaultLocalMaxSize = 500;

        static org.jvending.provisioning.stocking.StockingPolicy translate(PolicyType policyType) {
            if (policyType == null) return null;

            int remoteMaxSize = (policyType.getRemoteMaxSize() != null) ?
                    policyType.getRemoteMaxSize().intValue() : defaultRemoteMaxSize;

            int localMaxSize = (policyType.getLocalMaxSize() != null) ?
                    policyType.getLocalMaxSize().intValue() : defaultLocalMaxSize;

            boolean hasRemoteFetch = policyType.isFetchContent();

            MimeAcceptType mimeAcceptType = policyType.getMimeAccept();
            List<String> mimeAccepts = (mimeAcceptType != null) ? translateMimeAccept(mimeAcceptType) : null;

            MimeBlockType mimeBlockType = policyType.getMimeBlock();
            List<String> mimeBlocks = (mimeBlockType != null) ? translateMimeBlock(mimeBlockType) : null;

            WhiteListType whiteListType = policyType.getWhiteList();
            List<String> whiteList = (whiteListType != null) ? translateWhiteList(whiteListType) : null;

            BlackListType blackListType = policyType.getBlackList();
            List<String> blackList = (blackListType != null) ? translateBlackList(blackListType) : null;

            org.jvending.provisioning.stocking.StockingPolicy stockingPolicy =
                    new StockingPolicyImpl(remoteMaxSize, localMaxSize, hasRemoteFetch,
                            mimeAccepts, mimeBlocks, whiteList, blackList);
            return stockingPolicy;
        }

        static List<String> translateWhiteList(WhiteListType uriList) {
            List<String> uris = (uriList.getUri() != null) ? uriList.getUri() : new ArrayList<String>();
            if (uris.size() == 0) {
                uris.add("[.]");
                logger.finest("JV-1851-001: Adding all values to white list");
                return uris;
            }
            return getStringList(uris);
        }

        static List<String> translateBlackList(BlackListType uriList) {
            List<String> uris = (uriList.getUri() != null) ? uriList.getUri() : new ArrayList<String>();
            return getStringList(uris);
        }

        static List<String> translateMimeAccept(MimeAcceptType mime) {
            List<String> mimeTypes = (mime != null) ? mime.getMimeType() : new ArrayList<String>();
            if (mimeTypes.size() == 0) {
                mimeTypes.add("[.]");
                logger.finest("JV-1851-002: Adding all values to mime types");
                return mimeTypes;
            }
            return getStringList(mimeTypes);
        }

        static List<String> translateMimeBlock(MimeBlockType mime) {
            List<String > mimeTypes = (mime != null) ? mime.getMimeType() : new ArrayList<String>();
            return getStringList(mimeTypes);
        }

        static List<String> getStringList(List<String> base) {
            List<String> returns = new ArrayList<String>();

            for ( String s : base ) {
                if (s != null) returns.add(s);
            }
            return returns;
        }

        private static class StockingPolicyImpl implements org.jvending.provisioning.stocking.StockingPolicy {

            private final int remoteMaxSize;

            private final int localMaxSize;

            private final boolean hasFetchContent;

            private final List<String> mimeAccept;

            private final List<String> mimeBlock;

            private final List<String> whiteList;

            private final List<String> blackList;

            StockingPolicyImpl(int remoteMaxSize, int localMaxSize, boolean hasFetchContent,
                               List<String> mimeAccept, List<String> mimeBlock, List<String> whiteList, List<String> blackList) {
                this.remoteMaxSize = remoteMaxSize;
                this.localMaxSize = localMaxSize;
                this.hasFetchContent = hasFetchContent;
                this.mimeAccept = (mimeAccept != null) ? Collections.unmodifiableList(mimeAccept) :
                        Collections.unmodifiableList(new ArrayList<String>());
                this.mimeBlock = (mimeBlock != null) ? Collections.unmodifiableList(mimeBlock) :
                        Collections.unmodifiableList(new ArrayList<String>());
                this.whiteList = (whiteList != null) ? Collections.unmodifiableList(whiteList) :
                        Collections.unmodifiableList(new ArrayList<String>());
                this.blackList = (blackList != null) ? Collections.unmodifiableList(blackList) :
                        Collections.unmodifiableList(new ArrayList<String>());
            }

            public int getRemoteMaxSize() {
                return remoteMaxSize;
            }

            public int getLocalMaxSize() {
                return localMaxSize;
            }

            public boolean hasFetchContent() {
                return hasFetchContent;
            }

            public List<String> getMimeAccept() {
                return mimeAccept;
            }

            public List<String> getMimeBlock() {
                return mimeBlock;
            }

            public List<String> getWhiteList() {
                return whiteList;
            }

            public List<String> getBlackList() {
                return blackList;
            }
        }
    }
}
