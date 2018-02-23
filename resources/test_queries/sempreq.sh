#!/bin/bash

host=$1
file=$2

curl -X POST -u admin:admin http://$host:80/SEMP  -d @$file
