#!/bin/bash -x

solace=192.168.31.50
# solace=192.168.31.60

reqfile=$1
respfile=`echo $1 | sed s/requests/responses/`

curl -X POST -u 'appd:appd' http://$solace:80/SEMP -d@$reqfile > $respfile

