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
package org.jvending.provisioning.match;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.provisioning.BundleDescriptor;
import javax.provisioning.MatchPolicy;


/**
 * Matches a bundle descriptor from the <code>BundleDescriptorRepository</code> by keyword.
 *
 * @author Shane Isbell
 * @since 1.3.9a
 */

public final class KeywordSearch implements MatchPolicy {

    private static Logger logger = Logger.getLogger("Keyword Search");

    private String text;

    private Pattern pattern;

    public KeywordSearch(String text) {
        this.text = text;
        if (text != null) pattern = Pattern.compile(text);
    }

    public float doMatch(BundleDescriptor bundleDescriptor) {
        if (bundleDescriptor == null) return 0f;
        if (text == null || text.equals("")) return 1.0f;

        String keywords = bundleDescriptor.getCatalogProperty("Keywords");
        if (keywords == null) return 0f;

        String[] keywordTokens = keywords.split("[,]");
        int length = keywordTokens.length;

        int matches = 0;
        for (int i = 0; i < length; i++) {
            Matcher matcher = pattern.matcher(keywordTokens[i].trim());
            if (matcher.matches() == true) matches++;
        }
        float match = (float) matches / length;
        logger.finest("Keyword Match: Value = " + match + ", Keywords = " + keywords + ", Text = " + text);

        return match;
    }

}