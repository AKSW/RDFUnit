#!/bin/sh

rm -rf "04/c_"*
x=0

cat dbpedia.owl  | rapper -I - - file | grep FunctionalProperty | cut -f1 -d '>' | sed 's/<//' | sort -u | xargs | sed 's/ />, </g;s/^/</;s/ //g;s/$/>/'

for i in `cat dbpedia.owl  | rapper -I - - file | grep FunctionalProperty | cut -f1 -d '>' | sed 's/<//' | sort -u `
do


PNAME=$(echo "$i" | sed 's/.*\///g')

PREV="SELECT COUNT(?s) \n
	{ ?s <$i> ?c. } "
ERROR="SELECT COUNT(?grpdEntry) \n 	
{ { SELECT COUNT(?s) AS ?grpdEntry  \n	
{ ?s <$i> ?c.  }   \n 
GROUP BY ?s  HAVING count(?c) > 1 \n
	} } "

FILE="04/c__functional_""$x"_"$PNAME"/
mkdir -p $FILE
echo $PREV > $FILE"prev.sparql"
echo $ERROR > $FILE"err.sparql"
X=$((${X}+1))
done


