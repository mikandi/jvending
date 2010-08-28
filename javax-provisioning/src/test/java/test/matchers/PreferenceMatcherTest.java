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
package test.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.provisioning.matcher.MatcherException;
import javax.provisioning.matcher.PreferenceMatcher;

import junit.framework.TestCase;

public class PreferenceMatcherTest extends TestCase {

    private List<PreferenceMatcher> preferenceMatchers;

    public PreferenceMatcherTest(String name) {
        super(name);
    }

    public void testPreferenceMatcher() {
        Iterator i = preferenceMatchers.iterator();
        while (i.hasNext()) {
            PreferenceMatcher matcher = (PreferenceMatcher) i.next();
            testMatchValues(matcher);
        }
    }

    public void setUp() {
        PreferenceMatcher ttt = new PreferenceMatcher();
        PreferenceMatcher ttf = new PreferenceMatcher();
        PreferenceMatcher tft = new PreferenceMatcher();
        PreferenceMatcher tff = new PreferenceMatcher();
        PreferenceMatcher ftt = new PreferenceMatcher();
        PreferenceMatcher ftf = new PreferenceMatcher();
        PreferenceMatcher fft = new PreferenceMatcher();
        PreferenceMatcher fff = new PreferenceMatcher();

        try {
            ttt.init("TTT", getParamMap("true", "true", "true"));
            ttf.init("TTF", getParamMap("true", "true", "false"));
            tft.init("TFT", getParamMap("true", "false", "true"));
            tff.init("TFF", getParamMap("true", "false", "false"));
            ftt.init("FTT", getParamMap("false", "true", "true"));
            ftf.init("FTF", getParamMap("false", "true", "false"));
            fft.init("FFT", getParamMap("false", "false", "true"));
            fff.init("FFF", getParamMap("false", "false", "false"));
        } catch (MatcherException e) {
            e.printStackTrace();
        }

        preferenceMatchers = new ArrayList<PreferenceMatcher>();

        preferenceMatchers.add(ttt);
        preferenceMatchers.add(ttf);
        preferenceMatchers.add(tft);
        preferenceMatchers.add(tff);
        preferenceMatchers.add(ftt);
        preferenceMatchers.add(ftf);
        preferenceMatchers.add(fft);
        preferenceMatchers.add(fff);
    }

    private HashMap<String, String> getParamMap(String allMustMatch, String caseInsensitive, String wildcardsOn) {
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildcardsOn", wildcardsOn);
        return initParams;
    }

    private float matchValue(String requirement, String capability, PreferenceMatcher matcher) {
        List<String> capabilities = new ArrayList<String>();
        capabilities.add(capability);

        List<String> requirements = new ArrayList<String>();
        requirements.add(requirement);

        try {
            float f = matcher.match(requirements, capabilities);
            return f;
        } catch (MatcherException e) {
            e.printStackTrace();
        }
        return -1f;
    }

    private float matchCapabilities(String requirement, List<String> capabilities, PreferenceMatcher matcher) {
        List<String> requirements = new ArrayList<String>();
        requirements.add(requirement);

        try {
            float f = matcher.match(requirements, capabilities);
            return f;
        } catch (MatcherException e) {
            e.printStackTrace();
        }
        return -1f;
    }

    private void testMatchValues(PreferenceMatcher matcher) {
        List<String> capabilities = new ArrayList<String>();
        capabilities.add("en");
        capabilities.add("fr");
        capabilities.add("ge");

        float f = matchCapabilities("en", capabilities, matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);
        f = matchCapabilities("fr", capabilities, matcher);
        assertTrue(matcher.getAttributeName(), f == 1 / 2f);
        f = matchCapabilities("ge", capabilities, matcher);
        assertTrue(matcher.getAttributeName(), f == 1 / 3f);

        capabilities = new ArrayList<String>();
        capabilities.add("e[a-z]*");
        capabilities.add("fr");
        capabilities.add("ge");
        if (matcher.getWildcardsOn()) {
            f = matchCapabilities("en", capabilities, matcher);
            assertTrue(matcher.getAttributeName(), f == 1f);
        }

        capabilities = new ArrayList<String>();
        capabilities.add("e[a-z]*");
        capabilities.add("[a-z]*");
        capabilities.add("ge");
        if (matcher.getWildcardsOn()) {
            f = matchCapabilities("fr", capabilities, matcher);
            assertTrue(matcher.getAttributeName(), f == 1 / 2f);
        }
    }
}
