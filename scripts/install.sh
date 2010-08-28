mvn install:install-file -Dfile=../thirdparty/commons-email-1.2.jar -DgroupId=org.apache.commons -DartifactId=commons-email -Dversion=1.2 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/javamail/dsn.jar -DgroupId=mail -DartifactId=dsn -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/javamail/imap.jar -DgroupId=mail -DartifactId=imap -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/javamail/mailapi.jar -DgroupId=mail -DartifactId=mailapi -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/javamail/imap.jar -DgroupId=mail -DartifactId=imap -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=../thirdparty/jaxb-ri-2.1/jaxb-api.jar -DgroupId=javax.xml.bind -DartifactId=jaxb-api -Dversion=2.1 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/jaxb-ri-2.1/jsr173_1.0_api.jar -DgroupId=javax.xml.bind -DartifactId=jsr173_api -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/jaxb-ri-2.1/activation.jar -DgroupId=javax.xml.bind -DartifactId=activation -Dversion=1.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/jaxb-ri-2.1/jaxb-impl.jar -DgroupId=com.sun.xml.bind -DartifactId=jaxb-impl -Dversion=2.1 -Dpackaging=jar
mvn install:install-file -Dfile=../thirdparty/jaxb-ri-2.1/jaxb-xjc.jar -DgroupId=com.sun.xml.bind -DartifactId=jaxb-xjc -Dversion=2.1 -Dpackaging=jar