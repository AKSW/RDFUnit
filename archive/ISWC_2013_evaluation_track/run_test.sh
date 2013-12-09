#!/bin/sh
. "$1"
LIMIT=$3
QUERY=`cat $2`" LIMIT $LIMIT "

#QUERY=$(<config.txt)
echo "QUERY:
***************
$QUERY
***************
"

curl  --data-urlencode query="$QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH"  "$ENDPOINT" 
