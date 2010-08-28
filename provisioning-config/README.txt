adapters - JAXB bindings for adapters.xml
config - repositories that use the generated JAXB classes.
deliverables - JAXB bindings for deliverables.xml
devices - JAXB bindings for devices.xml
matchers - JAXB bindings for matchers.xml
stockinghandlers - JAXB bindings for stocking-handlers.xml

If you need to regenerate the JAXB bindings for the JSR-124 config files, you will need to download the JSR-124 spec/RI
and place the Matchers.xsd, Adapters.xsd or Devices.xsd in the respective resource directories. Then you can type
ant from the command line and the classes will generate.