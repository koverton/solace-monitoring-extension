#!/bin/bash

if [ "$#" -lt 2 ]; then
	echo "	USAGE: $0 <host-port> <request-file>"
	echo ""
	exit 1
fi

hostport=$1
file=$2

curl -X POST -u admin:admin http://$hostport/SEMP  -d @$file
