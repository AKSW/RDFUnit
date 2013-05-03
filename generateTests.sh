#!/bin/sh
FOLDER=autotests
ENDPOINT=http://dbpedia.org/sparql

echo "" > $FOLDER/functional.tex
for i in `curl http://mappings.dbpedia.org/server/ontology/dbpedia.owl  | rapper -I - - file | grep FunctionalProperty | cut -f1 -d '>' | sed 's/<//' | sort -u `
do
		PNAME=$(echo "$i" | sed 's/.*\///g')
		PREV=`curl $ENDPOINT --data-urlencode  default-graph="http://dbepdia.org" -d format=csv --data-urlencode query="SELECT COUNT(?s) { ?s <$i> ?c. } "  | tail -1 `
		ERROR=`curl $ENDPOINT --data-urlencode  default-graph="http://dbepdia.org" -d format=csv --data-urlencode query="SELECT COUNT(?grpdEntry) { SELECT COUNT(?s) AS ?grpdEntry{ ?s <$i> ?c.  }   GROUP BY ?s  HAVING count(?c) > 1 } "  | tail -1 `
		 

		RATE=`echo "scale=4\n($ERROR/$PREV) * 100" | bc `
		echo "$RATE & $PNAME & $PREV & $ERROR \\" >> $FOLDER/functional.tex
		

done
sed -i 's/^ &/0&/;s/00 / /;s/^\./0\./' $FOLDER/functional.tex
sort -n -r -o $FOLDER/functional.tex $FOLDER/functional.tex
 
