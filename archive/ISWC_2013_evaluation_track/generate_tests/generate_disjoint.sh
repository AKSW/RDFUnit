#!/bin/sh

PAT=`cut -f1,3 -d '>' disjoint_classes1.nt | sed 's/$/>/' | xargs | sed 's/ / } UNION { ?s a /g;s/^/{?s a /;s/$/ } /' `
echo "SELECT COUNT(?s) { 
	{ $PAT } 
} "


FILTER=`cut -f1,3 -d '>' disjoint_classes1.nt | sed 's/^/ || (?c1 = /; s/> </> \&\& ?c2 = </;s/$/> ) /' | xargs | sed 's/^||/FILTER (/;s/$/) . /' `

echo "SELECT COUNT(?s) { 
	?s a ?c1 . 
	?s a ?c2 . 
	$FILTER 
} "

