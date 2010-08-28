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
package javax.provisioning.matcher;

import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * <p>Class implementing the version string matching algorithm defined by Appendix A of the JNLP 1.0.1 (JSR 56)
 * specification.</p>
 * <p>The syntax for the requirement and capability strings is:</p>
 * <pre>
 * requirement ::= name ( “/” version-string )?
 * capability ::= name ( “/” version-id )?
 * name ::= name-char+
 * name-char ::= Any character other than “/” or “,”
 * </pre>
 * <p>The definitions of version-id and version-string are based on their definitions in Appendix A of the JNLP 1.0.1
 * specification (http://java.sun.com/products/javawebstart/download-spec.html). The following text is adapted
 * from that document.</p>
 * <p>A version-id is an exact version. A version string can match one or more version-ids. The syntax of a version-id is:</p>
 * <pre>
 * version-id ::= value ( separator value )*
 * value ::= version-char ( version-char )*
 * version-char ::= Any letter or digit character
 * separator ::= “.” | “-” | “_”
 * </pre>
 * <p>A version string is a list of version-ids separated with spaces. Each version-id can be postfixed with a + to
 * indicate a greater-than-or-equal match, or a “*” to indicated a prefix match. If there is no postfix, an exact match
 * is required. The syntax of version-strings is:</p>
 * <pre>
 * version-string ::= element ( “ ” element)*
 * element ::= version-id modifier?
 * modifier ::= “+” | “*”
 * </pre>
 * <p>A version-id can be described as a tuple of values. A version-id is broken in parts for each separator
 * ( . , - , or _). For example, “1.3.0-rc2-w” becomes (1,3,0,rc2,w), and “1.2.2-001” becomes (1,2,2,001).</p>
 * <p/>
 * <p>When testing for an exact match of two tuples, if they are not the same length, the shorter is first extended with
 * additional zero elements to the length of the longer. Then there is a match if and only if all the elements match
 * pairwise. Elements are compared numerically if they can both be parsed as Java ints; otherwise a string
 * comparison is used.</p>
 * <p/>
 * <p>When the + modifier is used, once again the shorter tuple is padded with zeros to the length of the longer. Then
 * the elements are compared pairwise, left-to-right. Elements are compared numerically if they can both be parsed
 * as Java ints; otherwise they are compared lexicographically. If we denote the requirement tuple as (r1,r2,...) and
 * the capability tuple as (c1,c2,...), then there is a match if and only if:</p>
 * <p/>
 * <pre>
 * • r1 is less than c1, or
 * • r1 equals c1, and either the tuple-length is 1, or (r2,...) matches (c2,...) recursively.
 * </pre>
 * <p>When the * modifier is used, a prefix matching algorithm is used. If the client tuple is shorter than the
 * requirement tuple, it is first padded with zeros make it the same length. There is a match if and only if:</p>
 * <pre>
 * • r1 equals c1, and
 * • the requirement tuple length is 1, or (r2,...) matches (c2,...) recursively.
 * </pre>
 *
 * @author Shane Isbell
 * @since 1.2a
 */


public class VersionMatcher extends RequirementMatcher {

    private static Logger logger = Logger.getLogger("VersionMatcher");

    private static Pattern versionIdMatch = Pattern.compile("[\\p{Alnum}[._-]]*[+*]?");

    /**
     * Constant denoting no modifier
     */
    private final static int nullModifier = 0;

    /**
     * Constant for '+' modifier
     */
    private final static int plusModifier = 1;

    /**
     * Constant for '*' modifier
     */
    private final static int starModifier = 2;

    /**
     * Default constructor
     */
    public VersionMatcher() {
        super();
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {

        String[] requirementTokens = tokenizeString(requirement);
        if (requirementTokens.length != 2) return 0.0f;//illegal value

        String reqName = requirementTokens[0];
        String[] requirementVersionTokens = tokenizeVersion(requirementTokens[1]);
        for (String s: capabilities ) {
            String[] capabilityTokens = tokenizeString(s);
            if (capabilityTokens.length != 2) return 0.0f;//illegal value
            if (!reqName.equals(capabilityTokens[0])) return 0.0f;

            String[] capabilityVersionTokens = tokenizeVersion(capabilityTokens[1]);
            if (isVersionAMatch(requirementVersionTokens, capabilityVersionTokens)) return 1.0f;
        }
        return 0.0f;
    }

    /**
     * Pads an array with zeros
     *
     * @param value the <code>String</code> array.
     * @param size  size that the value array needs to be expanded.
     * @return new String array with padded values
     */
    private String[] padArray(String[] value, int size) {
        int valueSize = value.length;
        int padSize = Math.abs(valueSize - size);

        String[] newValue = new String[size];

        System.arraycopy(value, 0, newValue, 0, valueSize);
        for (int i = 0; i < padSize; i++) {
            newValue[i + valueSize] = "0";
        }
        return newValue;
    }

    private int getModifier(String[] value) {
        String lastValue = value[value.length - 1].trim();
        char lastChar = lastValue.charAt(lastValue.length() - 1);
        if (lastChar == '+')
            return plusModifier;
        else if (lastChar == '*')
            return starModifier;
        else return nullModifier;
    }

    private boolean isVersionAMatch(String[] requirement, String[] capability) {
        int capSize = capability.length;
        int reqSize = requirement.length;

        int reqModifier = getModifier(requirement);
        if (reqModifier == plusModifier) {
            requirement[reqSize - 1] = requirement[reqSize - 1].replace('+', ' ').trim();
        } else if (reqModifier == starModifier) {
            requirement[reqSize - 1] = requirement[reqSize - 1].replace('*', ' ').trim();
        }

        if (reqSize < capSize && reqModifier != starModifier) {
            requirement = padArray(requirement, capSize);
        } else if (capSize < reqSize) {
            capability = padArray(capability, reqSize);
        }

        switch (reqModifier) {
            case nullModifier:
                return testExactMatch(requirement, capability);
            case plusModifier:
                return testGreaterThanMatch(requirement, capability);
            case starModifier:
                return testPrefixMatch(requirement, capability);
            default:
                return false;
        }
    }

    private boolean testExactMatch(String[] requirement, String[] capability) {
        int reqSize = requirement.length;
        int capSize = capability.length;
        if (reqSize != capSize) 
        	{
        	return false;
        	}

        for (int i = 0; i < reqSize; i++) {
            if (!requirement[i].equals(capability[i])) {
            	return false;
            }
        }
        return true;
    }

    private boolean testGreaterThanMatch(String[] requirement, String[] capability) {
        int reqSize = requirement.length;
        for (int i = 0; i < reqSize; i++) {
            if (isNumber(requirement[i]) && isNumber(capability[i])) {
                try {
                    Integer req = new Integer(requirement[i]);
                    Integer cap = new Integer(capability[i]);

                    int compare = cap.compareTo(req);
                    if (compare < 0) return false;
                    if (compare > 0) return true;
                } catch (NumberFormatException e) {
                    //this should never happen: already done check
                }
            } else {
                for (int j = 0; j < reqSize - 1; j++) {
                    char req = requirement[i].charAt(j);
                    char cap = capability[i].charAt(j);
                    if (req < cap) return true;
                    if (req > cap) return false;
                }
            }
        }
        return true;
    }

    private boolean isNumber(String number) {
        try {
            new Integer(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean testPrefixMatch(String[] requirement, String[] capability) {
        int reqSize = requirement.length;
        for (int i = 0; i < reqSize; i++) {
            if (!requirement[i].equals(capability[i])) return false;
        }
        return true;
    }


    private String[] tokenizeVersion(String version) throws MatcherException {
        if (!isVersionId(version)) {
            logger.warning("Invalid Version Id: ID = " + version);
            throw new MatcherException("VersionMatcher", "Invalid Version Id: ID = " + version);
        }
        return version.split("[._-]");
    }

    private boolean isVersionId(String version) {
        return (version == null) ? false : versionIdMatch.matcher(version).matches();
    }

    private String[] tokenizeString(String value) {
        return value.split("/");
    }
}