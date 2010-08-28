package org.jvending.provisioning.stocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jvending.provisioning.model.clientbundle.CatalogProperty;
import org.jvending.provisioning.model.clientbundle.DeviceRequirement;


public class ApkParserModel {

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
						cp.setName(t[0] + "-" + token[0].trim());
						cp.setValue(perm);
						properties.add(cp);							
					}		
				}

			} else if(line.startsWith("launchable activity name=")) {
				String[] token = line.split("'");
				String perm = token[1].trim().replace("'", "");
				CatalogProperty cp = new CatalogProperty();
				cp.setName("activity-name");
				cp.setValue(perm);
				properties.add(cp);				

			}
			else if(line.startsWith("uses-library:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidLibrary");
				requirement.setValue(perm);
				requirements.add(requirement);	
			}
			else if(line.startsWith("uses-feature:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				if(perm.startsWith("android.hardware")) {
					requirement.setName("HardwarePlatform.AndroidFeature");	
				} else {
					requirement.setName("SoftwarePlatform.AndroidFeature");
				}
				
				requirement.setValue(perm);
				requirements.add(requirement);	
			}	
			else if(line.startsWith("uses-gl-es")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();

				requirement.setName("SoftwarePlatform.Android-Gl-Es");	

				requirement.setValue(perm);
				requirements.add(requirement);	
			}
			else if(line.startsWith("densities:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("HardwarePlatform.AndroidDensity");
				requirement.setValue(perm);
				requirements.add(requirement);	
			}				
			else if(line.startsWith("supports-screens:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("HardwarePlatform.AndroidScreenSizes");
				requirement.setValue(perm);
				requirements.add(requirement);	
			}			
			else if(line.startsWith("uses-permission:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidPermission");
				requirement.setValue(perm);
				requirements.add(requirement);	
			}
			else if( line.startsWith("sdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidSdkVersion");
				requirement.setValue("Android/" + perm + "+");
				requirements.add(requirement);	
			}	else if( line.startsWith("minSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidMinSdkVersion");
				requirement.setValue("Android/" + perm + "+");
				requirements.add(requirement);	
			}
			else if( line.startsWith("maxSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidMaxSdkVersion");
				requirement.setValue("Android/" + perm);
				requirements.add(requirement);	
			}
			else if( line.startsWith("targetSdkVersion:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidTargetSdkVersion");
				requirement.setValue("Android/" + perm);
				requirements.add(requirement);	
			}	
			else if( line.startsWith("locales:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("SoftwarePlatform.AndroidLocales");
				requirement.setValue(perm);
				requirements.add(requirement);	
			}				
			else if(line.contains(":")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName("Android." + token[0].trim());
				requirement.setValue(perm);
				requirements.add(requirement);
		}
		}
	}
/*
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
						cp.setName(t[0] + "-" + token[0].trim());
						cp.setValue(perm);
						properties.add(cp);							
					}		
				}

			} else if(line.startsWith("launchable activity name=")) {
				String[] token = line.split("'");
				String perm = token[1].trim().replace("'", "");
				CatalogProperty cp = new CatalogProperty();
				cp.setName("activity-name");
				cp.setValue(perm);
				properties.add(cp);				

			}
			else if(line.startsWith("uses-permission:") || line.startsWith("supports-screens:") 
					|| line.startsWith("uses-library:") || line.startsWith("sdkVersion:") 
					|| line.startsWith("densities:")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName(token[0].trim());
				requirement.setValue(perm);
				requirements.add(requirement);	
			}
			else if(line.contains(":")) {
				String[] token = line.split(":");
				String perm = token[1].trim().replace("'", "");
				DeviceRequirement requirement = new DeviceRequirement();
				requirement.setName(token[0].trim());
				requirement.setValue(perm);
				requirements.add(requirement);
		}
		}}
		*/
}
