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


run_prev_query() {
    local output_dir=$OUTPUT_PATH/$(basename $DEFAULTGRAPH)/$1
    local output_file=$output_dir/$PREV_RESULT_FILE

    echo "QUERY:
***************
$CURRENT_PREV_QUERY
***************
"
    echo "result will be written to $output_file"
    echo 
    
    if [ ! -d $output_dir ]; then mkdir -p $output_dir; fi
    curl  --data-urlencode query="$CURRENT_PREV_QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH" -d format=csv "$ENDPOINT" > $output_file
}


run_err_query() {
    local output_dir=$OUTPUT_PATH/$(basename $DEFAULTGRAPH)/$1
    local output_file=$output_dir/$ERR_RESULT_FILE

    echo "QUERY:
***************
$CURRENT_ERR_QUERY
***************
"
    echo "result will be written to $output_file"
    echo 
    
    if [ ! -d $output_dir ]; then mkdir -p $output_dir; fi
    curl  --data-urlencode query="$CURRENT_ERR_QUERY"  --data-urlencode default-graph-uri="$DEFAULTGRAPH" -d format=csv "$ENDPOINT" > $output_file
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
                        CURRENT_PREV_QUERY="$(cat $instance_dir/$PREV_QUERY_FILE)"
                        run_prev_query $path
                    fi
                    if [ -s $instance_dir/$ERR_QUERY_FILE ]
                    then
                        # must be global, since it contains line breaks and
                        # so cannot be passed as a function parameter
                        CURRENT_ERR_QUERY="$(cat $instance_dir/$ERR_QUERY_FILE)"
                        run_err_query $path
                        local err_query=$(cat $instance_dir/$ERR_QUERY_FILE)
                    fi
                fi
            done
        fi
    done
}


query_endpoints() {
    for settings_file in $SETTINGS_DIR/*
    do
        . ./$SETTINGS_DIR/$settings_file
        run_queries
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

exit 0


