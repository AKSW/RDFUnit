#!/usr/bin/env bash

EP="de.dbpedia.org.tex el.dbpedia.org.tex es.dbpedia.org.tex fr.dbpedia.org.tex it.dbpedia.org.tex ja.dbpedia.org.tex live.dbpedia.org.tex live.nl.dbpedia.org.tex nl.dbpedia.org.tex pt.dbpedia.org.tex"
echo -n "" > functional.tex
for i in $EP 
do  
	join -t '&' -j 1 functional/dbpedia.org.tex functional/$i > functional.tex
	cp functional.tex functional/dbpedia.org.tex
done 
cat functional/dbpedia.org.tex | sed 's/$/\\\\/;s|</sparql>|err|g' > functional.tex
