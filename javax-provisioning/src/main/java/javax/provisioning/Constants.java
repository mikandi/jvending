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

package javax.provisioning;

/**
 * This interface defines the names of the core attributes that must be supported as both bundle requirements
 * and device capabilities. For information on the standard matching algorithms used for the core attributes,
 * see the documentation for the match algorithm classes in the javax.provisioning.matcher package.
 */

public interface Constants {

    public static final String HardwarePlatform_BitsPerPixel = "HardwarePlatform.BitsPerPixel";

    public static final String HardwarePlatform_DeviceIdentifier = "HardwarePlatform.DeviceIdentifier";

    public static final String HardwarePlatform_ScreenSize = "HardwarePlatform.ScreenSize";

    public static final String SoftwarePlatform_CcppAccept = "SoftwarePlatform.CcppAccept";

    public static final String SoftwarePlatform_CcppAccept_Charset = "SoftwarePlatform.CcppAccept-Charset";

    public static final String SoftwarePlatform_CcppAccept_Encoding = "SoftwarePlatform.CcppAccept-Encoding";

    public static final String SoftwarePlatform_CcppAccept_Language = "SoftwarePlatform.CcppAccept-Language";

    public static final String SoftwarePlatform_JavaPackage = "SoftwarePlatform.JavaPackage";

    public static final String SoftwarePlatform_JavaPlatform = "SoftwarePlatform.JavaPlatform";

    public static final String SoftwarePlatform_JavaProtocol = "SoftwarePlatform.JavaProtocol";

}