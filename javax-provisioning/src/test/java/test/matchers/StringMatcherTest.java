package test.matchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.provisioning.matcher.MatcherException;
import javax.provisioning.matcher.StringMatcher;

import junit.framework.TestCase;

public class StringMatcherTest extends TestCase {

    public StringMatcherTest(String name) {
        super(name);
    }

    public void testStringMatcher() {
        testExact("J2SE/1.5", "true", "true", "true");
        testExact("J2SE/1.5", "true", "true", "false");
        testExact("J2SE/1.5", "true", "false", "true");
        testExact("J2SE/1.5", "true", "false", "false");

        testExact("J2SE/1.5", "false", "true", "true");
        testExact("J2SE/1.5", "false", "true", "false");
        testExact("J2SE/1.5", "false", "false", "true");
        testExact("J2SE/1.5", "false", "false", "false");

        testExact("J2SE/1.5", "garbage", "true", "true");
        testExact("J2SE/1.5", "true", "garbage", "true");
        testExact("J2SE/1.5", "true", "true", "garbage");

        testExact("", "true", "true", "true");
        testExact("", "true", "true", "false");
        testExact("", "true", "false", "true");
        testExact("", "true", "false", "false");

        testExact("", "false", "true", "true");
        testExact("", "false", "true", "false");
        testExact("", "false", "false", "true");
        testExact("", "false", "false", "false");

        testExact(null, "true", "true", "true");
        testExact(null, "true", "true", "false");
        testExact(null, "true", "false", "true");
        testExact(null, "true", "false", "false");

        testExact(null, "false", "true", "true");
        testExact(null, "false", "true", "false");
        testExact(null, "false", "false", "true");
        testExact(null, "false", "false", "false");

        testNotExact("J2SE/1.5", "J2SE/1.4", "true", "true", "true");
        testNotExact("J2SE/1.5", "J2SE/1.4", "true", "true", "false");
        testNotExact("J2SE/1.5", "J2SE/1.4", "true", "false", "true");
        testNotExact("J2SE/1.5", "J2SE/1.4", "true", "false", "false");

        testNotExact("J2SE/1.5", "J2SE/1.4", "false", "true", "true");
        testNotExact("J2SE/1.5", "J2SE/1.4", "false", "true", "false");
        testNotExact("J2SE/1.5", "J2SE/1.4", "false", "false", "true");
        testNotExact("J2SE/1.5", "J2SE/1.4", "false", "false", "false");

        testNotExact("J2SE/1.5", null, "true", "true", "true");
        testNotExact("J2SE/1.5", null, "true", "true", "false");
        testNotExact("J2SE/1.5", null, "true", "false", "true");
        testNotExact("J2SE/1.5", null, "true", "false", "false");

        testNotExact("J2SE/1.5", null, "false", "true", "true");
        testNotExact("J2SE/1.5", null, "false", "true", "false");
        testNotExact("J2SE/1.5", null, "false", "false", "true");
        testNotExact("J2SE/1.5", null, "false", "false", "false");

        testCaseDifferent("J2SE/1.5", "J2sE/1.5", "false", "true", "false");
        testCaseDifferent("J2SE/1.5", "J2sE/1.5", "false", "false", "false");
        testCaseDifferent("J2SE/1.5", "J2sE/1.5", "false", "true", "true");
        testCaseDifferent("J2SE/1.5", "J2sE/1.5", "false", "false", "true");

        testCaseDifferent("J2SE/1.5", "J2.E/1.5", "false", "true", "true");
        testCaseDifferent("J2SE/1.5", "J2[A-Z]*E/1.5", "false", "true", "true");
        testCaseDifferent("J2SE/1.5", "J2[a-z]*E/1.5", "false", "true", "true");
    }

    private void testNotExact(String reqValue, String capValue, String allMustMatch, String caseInsensitive, String wildcardsOn) {
        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildcardsOn", wildcardsOn);

        List<String> capabilities = new ArrayList<String>();
        capabilities.add(capValue);

        List<String> requirements = new ArrayList<String>();
        requirements.add(reqValue);

        float matchValue = matchString(requirements, capabilities, initParams);
        String testInfo = "CapValue = " + capValue +
                ", ReqValue = " + reqValue +
                ", allMustMatch = " + allMustMatch +
                ", caseInsensitive = " + caseInsensitive +
                ", wildcardsOn = " + wildcardsOn;
        assertTrue(testInfo, matchValue == 0.0f);
    }

    private void testCaseDifferent(String reqValue, String capValue, String allMustMatch, String caseInsensitive, String wildcardsOn) {

        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildcardsOn", wildcardsOn);

        List<String> capabilities = new ArrayList<String>();
        capabilities.add(capValue);

        List<String> requirements = new ArrayList<String>();
        requirements.add(reqValue);

        float matchValue = matchString(requirements, capabilities, initParams);
        String testInfo = "CapValue = " + capValue +
                ", ReqValue = " + reqValue +
                ", allMustMatch = " + allMustMatch +
                ", caseInsensitive = " + caseInsensitive +
                ", wildcardsOn = " + wildcardsOn;
        if (caseInsensitive != null && caseInsensitive.equals("true")) assertTrue(testInfo, matchValue == 1.0f);
        else assertFalse(testInfo, matchValue == 1.0f);
    }

    private void testExact(String value, String allMustMatch, String caseInsensitive, String wildcardsOn) {

        HashMap<String, String> initParams = new HashMap<String, String>();
        initParams.put("allMustMatch", allMustMatch);
        initParams.put("caseInsensitive", caseInsensitive);
        initParams.put("wildcardsOn", wildcardsOn);

        List<String> capabilities = new ArrayList<String>();
        capabilities.add(value);

        List<String> requirements = new ArrayList<String>();
        requirements.add(value);

        float matchValue = matchString(requirements, capabilities, initParams);
        String testInfo = "Value = " + value + ", allMustMatch = " + allMustMatch +
                ", caseInsensitive = " + caseInsensitive +
                ", wildcardsOn = " + wildcardsOn;
        assertTrue(testInfo, matchValue == 1.0f);
    }

    private float matchString(List<String> requirements, List<String> capabilities, Map<String, String> initParams) {
        try {
            String attributeName = "SoftwarePlatform.JavaPlatform";
            StringMatcher stringMatcher = new StringMatcher();
            stringMatcher.init(attributeName, initParams);
            float matchValue = stringMatcher.match(requirements, capabilities);
            return matchValue;
        } catch (MatcherException e) {
            e.printStackTrace();
        }
        return -1f;
    }

}
