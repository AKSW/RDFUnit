#!/bin/sh
ENDPOINT=$1
TESTFILE=$2
LIMIT=$3
DEFAULTGRAPH=$4
QUERY=`cat $TESTFILE`

curl  --data-urlencode query="$QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH"  "$ENDPOINT" 
