#!/usr/bin/env bash

cat results.tsv | sed 's/{/0/g' | cut -f 1-2 | sort -u | grep -v "^#" | sed 's/\t/ /g'| while read LINE;
do
	template=`echo $LINE | cat | cut -d " " -f 1`
	instance=`echo $LINE | cat | cut -d " " -f 2`
	
	outputTsvFile=tmp_$template$instance.tsv
	cat results.tsv | sort | grep -P "$template\t$instance" | cut -f 3-6> $outputTsvFile
	
	outputPngFile=image_errprev_$template$instance.png
	gnuplot -e "filename='$outputTsvFile'" results-error-prevalence.gpl > $outputPngFile
	
	outputPngFile=image_errate_$template$instance.png
	gnuplot -e "filename='$outputTsvFile'" results-error-rate.gpl > $outputPngFile
done

#rm tmp*.tsv
