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
package javax.provisioning;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * The BundleType class enumerates the different type categorisations of a bundle descriptor.
 *
 * @author Shane Isbell
 * @version 1.0
 */

public class BundleType implements Serializable {

	private static final long serialVersionUID = -2738675262369563819L;

	public static final BundleType ANIMATION = new BundleType("ANIMATION");

    public static final BundleType APPLICATION = new BundleType("APPLICATION");

    public static final BundleType AUDIO = new BundleType("AUDIO");

    public static final BundleType BOOKMARK = new BundleType("BOOKMARK");

    public static final BundleType ICON = new BundleType("ICON");

    public static final BundleType IMAGE = new BundleType("IMAGE");

    public static final BundleType LIBRARY = new BundleType("LIBRARY");

    public static final BundleType LOGO = new BundleType("LOGO");

    public static final BundleType MARKUP = new BundleType("MARKUP");

    public static final BundleType MEDIA_MESSAGE = new BundleType("MEDIA_MESSAGE");

    public static final BundleType RINGTONE = new BundleType("RINGTONE");

    public static final BundleType SCREENSAVER = new BundleType("SCREENSAVER");

    public static final BundleType SKIN = new BundleType("SKIN");

    public static final BundleType SPEECH = new BundleType("SPEECH");

    public static final BundleType TEXT = new BundleType("TEXT");

    public static final BundleType TEXT_MESSAGE = new BundleType("TEXT_MESSAGE");

    public static final BundleType UNCLASSIFIED = new BundleType("UNCLASSIFIED");

    public static final BundleType V_CALENDAR = new BundleType("V_CALENDAR");

    public static final BundleType V_CARD = new BundleType("V_CARD");

    public static final BundleType VIDEO = new BundleType("VIDEO");

    public static final BundleType WALLPAPER = new BundleType("WALLPAPER");

    private String name;

    private static final BundleType[] PRIVATE_VALUES =
            {ANIMATION, APPLICATION, AUDIO, BOOKMARK, ICON, IMAGE, LIBRARY, LOGO, MARKUP,
                    MEDIA_MESSAGE, RINGTONE, SCREENSAVER, SKIN, SPEECH, TEXT, TEXT_MESSAGE, UNCLASSIFIED,
                    V_CALENDAR, V_CARD, VIDEO, WALLPAPER};

    private static final Set<String> SET_NAMES;

    static {
        Set<String> PRIVATE_SET_NAMES = new HashSet<String>();
        int length = PRIVATE_VALUES.length;
        for (int i = 0; i < length; i++) {
            PRIVATE_SET_NAMES.add(PRIVATE_VALUES[i].name);
        }
        SET_NAMES = Collections.unmodifiableSet(PRIVATE_SET_NAMES);
    }


    private BundleType(String name) {
        this.name = name;
    }

    public static BundleType getBundleType(String name) {
        int length = PRIVATE_VALUES.length;
        for (int i = 0; i < length; i++) {
            if (name.equals(PRIVATE_VALUES[i].name)) return PRIVATE_VALUES[i];
        }
        return null;
    }

    public static Set<String> getBundleTypeNames() {
        return SET_NAMES;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BundleType))
            return false;

        return (name.equals(o.toString()));
    }

    public int hashCode() {
        return name.hashCode();
    }
}