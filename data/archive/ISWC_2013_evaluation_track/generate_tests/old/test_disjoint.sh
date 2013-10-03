#!/bin/sh
. "../$1"
OUTPUT=disjoint/disjoint."$NAME".tex
LANG=$(echo "$NAME" | sed 's/dbpedia.org//g')

echo -n "" > $OUTPUT
echo -n "" > /tmp/gen
#curl http://mappings.dbpedia.org/server/ontology/dbpedia.owl
for i in `cut -f1,3 -d '>' disjoint_classes1.nt| sed 's/<//g;s/>//'| sort -u`
do

		CLASS1=$(echo "$i" | sed 's/ .*//g')
		CLASS2=$(echo "$i" | sed 's/. *//g')
		
		#PREV=`curl $ENDPOINT  --data-urlencode  default-graph-uri=$DEFAULTGRAPH -d format=csv --data-urlencode query="SELECT COUNT(?s) { ?s <$i> ?c. } "  | tail -1 `
		ERROR=`curl $ENDPOINT  --data-urlencode  default-graph-uri=$DEFAULTGRAPH -d format=csv --data-urlencode query="SELECT COUNT(?grpdEntry) { { SELECT COUNT(?s) AS ?grpdEntry{ ?s <$i> ?c.  }   GROUP BY ?s  HAVING count(?c) > 1} } "  | tail -1 `

		#echo "$RATE & $PNAME & $PREV & $ERROR \\" 
		RATE=`echo "scale=4\n($ERROR/$PREV) * 100" | bc `
		echo "$RATE & $PNAME & $PREV & $ERROR" >> /tmp/gen

done


sed -i 's/^ &/0&/;s/00 / /;s/^\./0\./' 
sort -n  -r -o /tmp/gen /tmp/gen
awk -F "&" '{print $2 " & "$3" & "$4" & "$1""}' /tmp/gen > $OUTPUT




