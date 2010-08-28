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

import java.io.Serializable;

/**
 * MatcherExceptions are exceptions created by Matchers to indicate a failure in procressing that must be handled
 * by the provisioning server.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public class MatcherException extends Exception implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7748445847803357022L;
	
	private String matcherName;

    public MatcherException(String matcherName, String message) {
        super((matcherName + ":" + message));
        this.matcherName = matcherName;
    }

    public MatcherException(String matcherName, String message, Throwable cause) {
        super(message, cause);
        this.matcherName = matcherName;
    }

    public String getMatcherName() {
        return matcherName;
    }

}
