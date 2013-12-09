#!/bin/sh
. "../$1"
OUTPUT=functional/"$NAME".csv
LANG=$(echo "$NAME" | sed 's/dbpedia.org//g')

echo -n "" > $OUTPUT
echo -n "" > /tmp/gen
#curl http://mappings.dbpedia.org/server/ontology/dbpedia.owl
for i in `cat dbpedia.owl  | rapper -I - - file | grep FunctionalProperty | cut -f1 -d '>' | sed 's/<//' | sort -u `
do

		PNAME=$(echo "$i" | sed 's/.*\///g')
		
		PREV=`curl $ENDPOINT  --data-urlencode  default-graph-uri=$DEFAULTGRAPH -d format=csv --data-urlencode query="SELECT COUNT(?s) { ?s <$i> ?c. } "  | tail -1 `
		ERROR=`curl $ENDPOINT  --data-urlencode  default-graph-uri=$DEFAULTGRAPH -d format=csv --data-urlencode query="SELECT COUNT(?grpdEntry) { { SELECT COUNT(?s) AS ?grpdEntry{ ?s <$i> ?c.  }   GROUP BY ?s  HAVING count(?c) > 1} } "  | tail -1 `

		#echo "$RATE & $PNAME & $PREV & $ERROR \\" 
		RATE=`echo "scale=4\n($ERROR/$PREV) * 100" | bc `
		echo "$RATE $PNAME $PREV $ERROR" >> $OUTPUT

done
sort -n  -r -o $OUTPUT $OUTPUT
sed -i 's/^ /0 /;s/00 / /;s/^\./0\./' $OUTPUT



