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
