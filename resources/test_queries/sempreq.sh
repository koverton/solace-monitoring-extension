#!/bin/bash
cd `dirname $0`

if [ "$#" -lt 2 ]; then
	echo "	USAGE: $0 <host-port> <request-file>"
	echo ""
	exit 1
fi

mkdir -p responses

hostport=$1
infile=$2
outfile=`echo $infile | sed 's/requests/responses/'`
sempver="soltr/7_2_2"

curl -X POST -u admin:admin http://$hostport/SEMP  -d @$infile > $outfile
