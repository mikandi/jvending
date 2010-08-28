call mvn install:install-file -Dfile=../thirdparty/jaxb/jaxb-api-1.0.1.jar -DgroupId=javax.xml.bind -DartifactId=jaxb-api -Dversion=1.0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=../thirdparty/jaxb/jaxb-impl-1.0.1.jar -DgroupId=com.sun.xml.bind -DartifactId=jaxb-impl -Dversion=1.0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=../thirdparty/jaxb/jaxb-libs-1.0.1.jar -DgroupId=com.sun.msv -DartifactId=jaxb-libs -Dversion=1.0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=../thirdparty/jaxb/namespace-1.0.1.jar -DgroupId=javax.xml.namespace -DartifactId=namespace -Dversion=1.0.1 -Dpackaging=jar
call mvn install:install-file -Dfile=../thirdparty/jaxb/xsdlib-1.0.1.jar -DgroupId=com.sun.msv -DartifactId=xsdlib -Dversion=1.0.1 -Dpackaging=jar
