#!/bin/bash
#
# Run this script to install this Solace SEMP parser in your local MVN repository.
# These versions are referenced in the default Maven pom.xml for this project.
#
# To parse other versions of the SEMP v1 (XML-based) protocol, see https://github.com/koverton/semp_jaxb

for JAR in *.jar; do
	POM=`echo $JAR | sed 's/jar/pom/'`
	if [ -e "$POM" ]; then
		echo "POM! $POM"
		mvn install:install-file -Dfile=$JAR     -DpomFile=$POM
	else
		echo "!!!!!!!!! MISSING???? $POM"
	fi
done
