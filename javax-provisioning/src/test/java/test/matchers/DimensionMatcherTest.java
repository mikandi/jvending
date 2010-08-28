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
import java.util.List;

import javax.provisioning.matcher.DimensionMatcher;
import javax.provisioning.matcher.MatcherException;

import junit.framework.TestCase;

public class DimensionMatcherTest extends TestCase {

    private List<DimensionMatcher> dimensionMatchers;

    public DimensionMatcherTest(String name) {
        super(name);
    }

    public void testDimensionMatcher() {
        for(DimensionMatcher matcher : dimensionMatchers) {
            testMatchValues(matcher);
        }
    }

    public void setUp() {
        DimensionMatcher ttt = new DimensionMatcher();
        DimensionMatcher ttf = new DimensionMatcher();
        DimensionMatcher tft = new DimensionMatcher();
        DimensionMatcher tff = new DimensionMatcher();
        DimensionMatcher ftt = new DimensionMatcher();
        DimensionMatcher ftf = new DimensionMatcher();
        DimensionMatcher fft = new DimensionMatcher();
        DimensionMatcher fff = new DimensionMatcher();

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

        dimensionMatchers = new ArrayList<DimensionMatcher>();

        dimensionMatchers.add(ttt);
        dimensionMatchers.add(ttf);
        dimensionMatchers.add(tft);
        dimensionMatchers.add(tff);
        dimensionMatchers.add(ftt);
        dimensionMatchers.add(ftf);
        dimensionMatchers.add(fft);
        dimensionMatchers.add(fff);
    }

    private HashMap<String, String> getParamMap(String allMustMatch, String caseInsensitive, String wildCardsOn) {
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildCardsOn", wildCardsOn);
        return initParams;
    }

    private float matchValue(String requirement, String capability, DimensionMatcher matcher) {
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

    private void testMatchValues(DimensionMatcher matcher) {

        float f = matchValue("96+ x 128+", "96+ x 128+", matcher);
        assertTrue("Same Dimension: Plus", f == 1f);

        f = matchValue("96+x128+", "96+ x 128+", matcher);
        assertTrue("Same Dimension: Plus", f == 1f);

        f = matchValue("96 x 128", "96 x 128", matcher);
        assertTrue("Same Dimension: Exact", f == 1f);

        f = matchValue("96+ x 128+", "100 x 128+", matcher);
        assertTrue("Capability Width Higher: No Capability Plus Sign", f == .98f);

        f = matchValue("96 x 128+", "100 x 128+", matcher);
        assertTrue("Capability Width Higher: No Plus Signs", f == 0f);

        f = matchValue("96 x 128+", "90 x 128+", matcher);
        assertTrue("Capability Width Lower: No Plus Signs", f == 0f);

        f = matchValue("106+ x 128+", "96+ x 128+", matcher);
        assertTrue("Requirement Width Higher", f == 0f);

        f = matchValue("86+ x 128+", "96+ x 128+", matcher);
        assertTrue("Requirement Width Lower", f == .9479166f);

        f = matchValue("96+ x 128+", "96+ x 108+", matcher);
        assertTrue("Capability Height Lower", f == 0f);

        f = matchValue("96+ x 128+", "92+ x 108+", matcher);
        assertTrue("Capability Dimension Lower", f == 0f);

        f = matchValue(null, null, matcher);
        assertTrue("Both Null", f == 1f);

        f = matchValue("-96+ x 128+", "42+ x 108+", matcher);
        assertTrue("Negative Requirement", f == 0f);

        f = matchValue("96+ x 128+", "-42 x 108+", matcher);
        assertTrue("Negative Capability", f == 0f);

        f = matchValue("96+ 128+", "-42 x 108+", matcher);
        assertTrue("No x Sign", f == 0f);

        f = matchValue("fdsf", "42+ x 108+", matcher);
        assertTrue("No Integer Value", f == 0f);

        f = matchValue("89a+ x 68dsaf+", "-42 x 108+", matcher);
        assertTrue("Garbage", f == 0f);

    }
}
