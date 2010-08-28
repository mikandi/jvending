/*
 *  JVending
 *  Copyright (C) 2004  Shane Isbell
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

package javax.provisioning.matcher;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class implementing a string matching algorithm.
 *
 * @author Shane Isbell
 * @since 1.2a
 */


public class StringMatcher extends RequirementMatcher {

    private boolean isCaseInsensitive = false;

    private boolean isWildcardsOn = false;

    /**
     * Default constructor
     */
    public StringMatcher() {
        super();
    }

    public boolean getCaseInsensitive() {
        return isCaseInsensitive;
    }

    public boolean getWildcardsOn() {
        return isWildcardsOn;
    }

    public void init(String attributeName, Map<String, String> initParams) throws MatcherException {
        super.init(attributeName, initParams);

        String caseInsensitive = (String) initParams.get("caseInsensitive");
        if (caseInsensitive != null)
            isCaseInsensitive = (caseInsensitive.equals("true"));

        String wildcardsOn = (String) initParams.get("wildcardsOn");
        if (wildcardsOn != null)
            isWildcardsOn = (wildcardsOn.equals("true"));

    }

    protected boolean matchCapability(String reqValue, String capValue) {
        if (reqValue == null) return true;
        if (capValue == null) return false;

        if (isWildcardsOn) {
            Pattern pattern = (isCaseInsensitive) ?
                    Pattern.compile(capValue, Pattern.CASE_INSENSITIVE) :
                    Pattern.compile(capValue);

            Matcher matcher = pattern.matcher(reqValue);
            return matcher.matches();

        } else {
            if (!isCaseInsensitive) return (reqValue.equals(capValue));
            else return (reqValue.equalsIgnoreCase(capValue));
        }
    }

    protected float matchRequirement(String requirement, List<String> capabilities)
            throws MatcherException {
        if (requirement == null) return 1.0f;
        if (capabilities == null || capabilities.size() == 0) return 0.0f;

        for (String s : capabilities) {
            if (matchCapability(requirement, s)) return 1.0f;
        }
        return 0.0f;
    }

}