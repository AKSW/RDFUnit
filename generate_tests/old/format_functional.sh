#!/bin/sh
OUTPUT=functional/functional."$NAME".tex

echo -n "" > $OUTPUT
echo -n "" > /tmp/gen
#curl http://mappings.dbpedia.org/server/ontology/dbpedia.owl
for i in `ls -1 functional | grep '.*csv'`
do
	FILE=functional/$i
	DNAME=$(echo "$i" | sed 's/\.csv//')
	cat $FILE | awk  '{print $2 " & "$3" & "$4" & "$1" "}' | sort  > functional/$DNAME".tex"

done


#sed -i 's/^ &/0&/;s/00 / /;s/^\./0\./' 
#sort -n  -r -o /tmp/gen /tmp/gen
# /tmp/gen > $OUTPUT
#sort  -r -o $OUTPUT $OUTPUT



