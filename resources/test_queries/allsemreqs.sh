#!/bin/bash
cd `dirname $0`

if [ "$#" -lt 2 ]; then
	echo "	USAGE: $0 <host-port> <username>"
	echo ""
	exit 1
fi

mkdir -p responses

hostport=$1
user=$2

# Read Password
echo -n Password: 
read -s pass

for F in requests/*.xml; do
	infile=$F
	outfile=`echo $infile | sed 's/requests/responses/'`
	curl -X POST -u "$user:$pass" -s http://$hostport/SEMP -d @$infile > $outfile
done

fmt=`date +'%Y.%m.%d.%H.%M'`
tar zcf $fmt.tar.gz responses
