Documentation located at http://code.google.com/p/jvending

Modules:
javax-provisioning: The J2EE Client Provisioning API.
provisioning-adapter: Adapters (generic, MIDP and JNLP) that are required for the JSR-124 spec.
provisioning-config: JAXB bindings for the configuration files and repositories for accessing them.
provisioning-dao: Contains the data access objects (that use Hibernate for the underlying implementation)
provisioning-impl: Core implementations of the javax.provisioning package.
provisioning-model: Hibernate generated Java bindings.
scripts: Install scripts for installing Sun's JAXB libraries within the local maven repo.
thirdparty: Libraries necessary for code generation

Error Codes for the logs:
1000 - 1099: javax-provisioning
1100 - 1199: provisioning
1200 - 1250: adapter
1251 - 1299: deliverable
1300 - 1399: config
1400 - 1499: dao
1500 - 1599: impl
1600 - 1699: model
1700 - 1799: portal
1800 - 1850: stocking
1851 - 1899: stocking-impl
1900 - 1999: taglib


= Building From Source =
For the latest work, you can checkout the trunk

  * http://jvending.googlecode.com/svn/trunk/

or one of the versioned tags:

  * http://jvending.googlecode.com/svn/tags/j2ee-client-provisioning-2.[x]

Next run the scripts/install.bat or scripts.sh, depending on your OS. This script installs the jaxb and jaxme libraries into your local maven repo.

Now execute
{{{
  mvn install
}}}

This will build jvending. You will find the WAR file that you need to deploy to tomcat or jetty at provisioning-portal/target/provisioning.war