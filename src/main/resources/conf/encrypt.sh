#!/usr/bin/env bash

if [ "$#" -lt 1 ]; then
    echo "USAGE: $0 <hash-key>"
    echo ""
    echo "        You will be prompted for a password"
    echo ""
    exit 0
fi

# Make sure we execute from the directory this script is in
cd `dirname $0`
hashkey=$*

echo -n Enter password to encrypt:
read -s password
echo ""

java -cp lib/appd-exts-commons*.jar com.appdynamics.extensions.crypto.Encryptor "$hashkey" $password
