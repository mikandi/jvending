package test.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.provisioning.matcher.MatcherException;
import javax.provisioning.matcher.VersionMatcher;

import junit.framework.TestCase;

public class VersionMatcherTest extends TestCase {

    private List<VersionMatcher> versionMatchers;

    public VersionMatcherTest(String name) {
        super(name);
    }

    public void testVersionMatcher() {
        Iterator i = versionMatchers.iterator();
        while (i.hasNext()) {
            VersionMatcher matcher = (VersionMatcher) i.next();
            testMatchValues(matcher);
        }
    }

    public void setUp() {
        VersionMatcher ttt = new VersionMatcher();
        VersionMatcher ttf = new VersionMatcher();
        VersionMatcher tft = new VersionMatcher();
        VersionMatcher tff = new VersionMatcher();
        VersionMatcher ftt = new VersionMatcher();
        VersionMatcher ftf = new VersionMatcher();
        VersionMatcher fft = new VersionMatcher();
        VersionMatcher fff = new VersionMatcher();

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
            fail("Could not set up matchers");
        }

        versionMatchers = new ArrayList<VersionMatcher>();

        versionMatchers.add(ttt);
        versionMatchers.add(ttf);
        versionMatchers.add(tft);
        versionMatchers.add(tff);
        versionMatchers.add(ftt);
        versionMatchers.add(ftf);
        versionMatchers.add(fft);
        versionMatchers.add(fff);
    }

    private HashMap<String, String> getParamMap(String allMustMatch, String caseInsensitive, String wildCardsOn) {
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildCardsOn", wildCardsOn);
        return initParams;
    }

    private float matchValue(String requirement, String capability, VersionMatcher matcher) {
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

    private float matchCapabilities(String requirement, List<String> capabilities, VersionMatcher matcher) {
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

    private float matchCapability(String req, String cap, VersionMatcher matcher) {
        List<String> capabilities = new ArrayList<String>();
        capabilities.add(cap);
        return matchCapabilities(req, capabilities, matcher);
    }

    private void testMatchValues(VersionMatcher matcher) {

        float f = matchCapability("MIDP/1.0+", "MIDP/2.0", matcher);
        assertTrue(matcher.getAttributeName() + "MIDP/1.0+", f == 1f);

        f = matchCapability("Android/3+", "Android/3", matcher);
        assertTrue(matcher.getAttributeName() + "Android/3+", f == 1f);
        
        f = matchCapability("MIDP/1.0+", "MIDP/1.0", matcher);
        assertTrue(matcher.getAttributeName() + "MIDP/1.0+", f == 1f);

        f = matchCapability("J2SE/1.2+", "MIDP/1.0", matcher);
        assertTrue(matcher.getAttributeName() + "J2SE/1.2+", f == 0f);

        f = matchCapability("A/1.0", "A/1.0", matcher);
        assertTrue(matcher.getAttributeName() + ":1.0", f == 1f);

        f = matchCapability("A/1-0", "A/1-0", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1_0", "A/1_0", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.0.a", "A/1.0.a", matcher);
        assertTrue(matcher.getAttributeName() + ":1.0.a", f == 1f);

        f = matchCapability("A/1.0.a+", "A/1.0.b", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1_0_a+", "A/1_0_b", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1-0-a+", "A/1-0-b", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.0.b+", "A/1.0.a", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1_0_b+", "A/1_0_a", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1-0-b+", "A/1-0-a", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.0.a+", "A/1.1.a", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.1.a+", "A/1.0.a", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.2+", "A/1.2.1", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.0", "A/2.0", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.0+", "A/2.0", matcher);
        assertTrue(matcher.getAttributeName() + "- greater than", f == 1f);

        f = matchCapability("A/1.0+", "A/0.6", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.2*", "A/1.2.2", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.2*", "A/1.3", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.2.0.0*", "A/1.2", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.2.a*", "A/1.2.a.1", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1_2_a*", "A/1_2_a_1", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1-2-a*", "A/1-2-a-1", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.1.0", "A/1.1.0", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.1", "A/1.1.0", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);

        f = matchCapability("A/1.1.1", "A/1.1", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/1.1", "A/1.1.1", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);


        f = matchCapability("A/1.2.0.0*", "A/1.1.4", matcher);
        assertTrue(matcher.getAttributeName(), f == 0f);

        f = matchCapability("A/dsafadkj*", "A/1.1.4", matcher);
        assertTrue(matcher.getAttributeName() + ":dsafadkj*", f == 0f);

        f = matchCapability("A/1.100+", "A/1.110", matcher);
        assertTrue(matcher.getAttributeName(), f == 1f);
    }
}
