package org.jvending.provisioning.stocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jvending.provisioning.stocking.par.CatalogProperty;
import org.jvending.provisioning.stocking.par.DeviceRequirement;

public class ApkParser {
	
	private List<DeviceRequirement> requirements;
	
	private List<CatalogProperty> properties;

	
	public List<DeviceRequirement> getRequirements() {
		return requirements;
	}


	public List<CatalogProperty> getProperties() {
		return properties;
	}


	public void read(InputStream is) throws IOException {
		if(is == null) throw new IOException("InputStream is null");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		requirements = new ArrayList<DeviceRequirement>();
		properties = new ArrayList<CatalogProperty>();
		String line = null;
		while((line =reader.readLine()) != null) {
			if(line.startsWith("package:") || line.startsWith("application:")) {
				String[] t = line.split(":");
				for(String t1 : t[1].trim().split(" ")) {
					String[] token = t1.split("=");
					if(token.length > 1) {
						String perm = token[1].trim().replace("'", "");
						CatalogProperty cp = new CatalogProperty();
						cp.setPropertyName(t[0] + "-" + token[0].trim());
						cp.setPropertyValue(perm);
						properties.add(cp);							
					}		
				}

			} else if(line.startsWith("launchable activity name=")) {
				String[] token = line.split("'");
				String perm = token[1].trim().replace("'", "");
				CatalogProperty cp = new CatalogProperty();
				cp.setPropertyName("activity-name");
				cp.setPropertyValue(perm);
				properties.add(cp);				

			}
			else if(line.startsWith("uses-library:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidLibrary");
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}
			else if(line.startsWith("uses-feature:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				if(perm.startsWith("android.hardware")) {
					requirement.setRequirementName("HardwarePlatform.AndroidFeature");	
				} else {
					requirement.setRequirementName("SoftwarePlatform.AndroidFeature");
				}
				
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}	
			else if(line.startsWith("uses-gl-es")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();

				requirement.setRequirementName("SoftwarePlatform.Android-Gl-Es");	

				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}
			else if(line.startsWith("densities:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("HardwarePlatform.AndroidDensity");
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}				
			else if(line.startsWith("supports-screens:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("HardwarePlatform.AndroidScreenSizes");
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}			
			else if(line.startsWith("uses-permission:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidPermission");
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}
			else if( line.startsWith("sdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidSdkVersion");
				requirement.setRequirementValue("Android/" + perm + "+");
				requirements.add(requirement);	
			}	else if( line.startsWith("minSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidMinSdkVersion");
				requirement.setRequirementValue("Android/" + perm + "+");
				requirements.add(requirement);	
			}
			else if( line.startsWith("maxSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidMaxSdkVersion");
				requirement.setRequirementValue("Android/" + perm);
				requirements.add(requirement);	
			}
			else if( line.startsWith("targetSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidTargetSdkVersion");
				requirement.setRequirementValue("Android/" + perm);
				requirements.add(requirement);	
			}	
			else if( line.startsWith("locales:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("SoftwarePlatform.AndroidLocales");
				requirement.setRequirementValue(perm);
				requirements.add(requirement);	
			}				
			else if(line.contains(":")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setRequirementName("Android." + token[0].trim());
				requirement.setRequirementValue(perm);
				requirements.add(requirement);
		}
		}
	}
}
