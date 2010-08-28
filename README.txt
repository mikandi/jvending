Documentation located at http://wiki.github.com/sisbell/jvending/

Modules:
javax-provisioning: The J2EE Client Provisioning API and implementation classes
provisioning-config: JAXB bindings for the configuration files and repositories for accessing them.
provisioning-model: Hibernate generated Java bindings.
provisioning-stocking-par: JAXB model bindings for the par decryption file
scripts: Install scripts for installing Sun's JAXB libraries within the local maven repo.
thirdparty: Libraries necessary for code generation


= Building From Source =
For the latest work, you can checkout the trunk

  *  git clone git@github.com:sisbell/jvending.git

Next run the scripts/install.bat or scripts.sh, depending on your OS. This script installs the jaxb and jaxme libraries into your local maven repo.

Now execute
{{{
  mvn install -Dcommand=true
}}}

This will build jvending. You will find the WAR file that you need to deploy to tomcat or jetty at provisioning-portal/target/catalog.war