#!/bin/sh


USAGE="""
usage: $0 <path_to_directory_of_sparql_endpoint_settings_files> <path_to_sparql_file_directory_hierarchy> <result_output_path>

the sparql file directory hierarchy should be of the structure

<template_dir>/
    <instanciation_dir>/
        *sparql

example call: $0 ../endpoints ./queries /tmp/results
"""
PREV_QUERY_FILE="prev.sparql"
PREV_RESULT_FILE="prev.result"
ERR_QUERY_FILE="err.sparql"
ERR_RESULT_FILE="err.result"
PREFIX_FILE="prefixes.sparql"

run_prev_query() {
    local output_dir=$OUTPUT_PATH/$(basename $DEFAULTGRAPH)/$1
    local output_file=$output_dir/$PREV_RESULT_FILE

    echo "QUERY:
***************
$CURRENT_PREV_QUERY
***************

result will be written to $output_file

"

    if [ ! -d $output_dir ]; then mkdir -p $output_dir; fi
    curl  --data-urlencode query="$CURRENT_PREV_QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH" -d format=csv "$ENDPOINT" | tail -n 1 > $output_file
    sleep 1
}


run_err_query() {
    local output_dir=$OUTPUT_PATH/$(basename $DEFAULTGRAPH)/$1
    local output_file=$output_dir/$ERR_RESULT_FILE

    echo "QUERY:
***************
$CURRENT_ERR_QUERY
***************

result will be written to $output_file

"  

    if [ ! -d $output_dir ]; then mkdir -p $output_dir; fi
    curl  --data-urlencode query="$CURRENT_ERR_QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH" -d format=csv "$ENDPOINT" | tail -n 1  > $output_file
    sleep 1
}


run_queries() {
    for template_dir in $SPARQL_DIRS/*
    do
        if [ -d $template_dir ]
        then
            for instance_dir in $template_dir/*
            do
                if [ -d $instance_dir ]
                then
                    local path=$(basename $template_dir)/$(basename $instance_dir)
                    if [ -s $instance_dir/$PREV_QUERY_FILE ]
                    then
                        # must be global, since it contains line breaks and
                        # so cannot be passed as a function parameter
                        CURRENT_PREV_QUERY="$(cat $PREFIX_FILE $instance_dir/$PREV_QUERY_FILE)"
                        run_prev_query $path
                    fi
                    if [ -s $instance_dir/$ERR_QUERY_FILE ]
                    then
                        # must be global, since it contains line breaks and
                        # so cannot be passed as a function parameter
                        CURRENT_ERR_QUERY="$(cat $PREFIX_FILE $instance_dir/$ERR_QUERY_FILE)"
                        run_err_query $path
                        # local err_query=$(cat $PREFIX_FILE $instance_dir/$ERR_QUERY_FILE)
                    fi
                fi
            done
        fi
    done
}

aggregate_results() {
	results_file=$OUTPUT_PATH/results.tsv
	echo "#template\tinstance\tendpoint\terror\tprevelance" > $results_file
	for endpoint_dir in $OUTPUT_PATH/*
	do
		if [ -d $endpoint_dir ]
		then
			endpoint=$(basename $endpoint_dir)
			for template_dir in $endpoint_dir/*
			do
				if [ -d $template_dir ]
				then
					template=$(basename $template_dir)
					for instance_dir in $template_dir/*
					do
						if [ -d $instance_dir ]
						then
							instance=$(basename $instance_dir)
							
							local_prev_res=0
							if [ -s $instance_dir/$PREV_RESULT_FILE ]
							then
								local_prev_res=$(cat $instance_dir/$PREV_RESULT_FILE)
							fi
							
							local_err_res=0
							if [ -s $instance_dir/$ERR_RESULT_FILE ]
							then
								local_err_res=$(cat $instance_dir/$ERR_RESULT_FILE)
							fi
							
							echo "$template\t$instance\t$endpoint\t$local_err_res\t$local_prev_res" >> $results_file
						fi
					done
				fi
			done
		fi
	done
}


query_endpoints() {
	timing_file=$OUTPUT_PATH/timing.tsv
	echo "#endpoint\tmiliseconds" > $timing_file

    for settings_file in $SETTINGS_DIR/*
    do
        . ./$SETTINGS_DIR/$settings_file

        timeStart=$(date +%s%N | cut -b1-13)
        run_queries
        timeEnd=$(date +%s%N | cut -b1-13)

        endpoint=$(basename $settings_file)
        timeTotal=$((timeEnd-timeStart))
        echo "$endpoint\t$timeTotal" >> $timing_file
    done
}

if [ -z $3 ]
then
    echo "$USAGE"
    exit 1
fi

SETTINGS_DIR=$1
SPARQL_DIRS=$2
OUTPUT_PATH=$3

query_endpoints
aggregate_results
exit 0


}