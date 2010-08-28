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