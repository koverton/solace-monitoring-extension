#!/bin/bash -x
#
# Run this script to install this Solace SEMP parser in your local MVN repository.
# These versions are referenced in the default Maven pom.xml for this project.
#
# To parse other versions of the SEMP v1 (XML-based) protocol, see https://github.com/koverton/semp_jaxb

mvn install:install-file -Dfile=semp_jaxb_8_2_0-8.2.1.4.jar     -DpomFile=semp_jaxb_8_2_0-8.2.1.4.pom

mvn install:install-file -Dfile=semp_jaxb_8_6VMR-8.6.0.1010.jar -DpomFile=semp_jaxb_8_6VMR-8.6.0.1010.pom

