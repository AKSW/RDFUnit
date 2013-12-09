set terminal png enhanced  size 640,300

set datafile separator "\t"

set datafile missing '-'

set xtics rotate by -45  offset character -0.9, 0, 0




plot 'results2.tsv' u 3:xticlabel(1)  title 'Endpoint', \
     ''             u 4 title 'Error',\
     ''             u 5 title 'Prevalence'

