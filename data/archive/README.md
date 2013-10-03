Databugger
==========

This repository contains scripts and queries used to create some stats and figures for the ISWC paper submission [*"Test-driven Data Quality Evaluation for SPARQL Endpoints"*](http://svn.aksw.org/papers/2013/ISWC_Databugger/public.pdf). The aim of this paper submission was to present a methodology for quality assessment of linked data resources.
According to this methodology query templates, so called *data quality test patterns*, are set up to describe general conditions of erroneous data. One simple example is the condition that one certain literal value has to be greater than or less than another certain literal value. In a further step, these data quality test patterns are instantiated to be used in the context of a real data source. Referring to the example above, such an instantiation, a *test pattern binding*, could be the condition that a birth date always has to be less than (i.e. before) a death date.
After such instantiations were collected the resulting queries will have to be executed on the data set it was instantiated for.

All the scripts, source code and queries used in the evaluation and development of the above mentioned formalism are contained in the directory `ISWC_2013_evaluation_track`. Its content is described below.


### `endpoints/`

The actual data sources to query via the test pattern bindings are defined in the `endpoints` directory. The information needed to run queries on a chosen endpoint are provided via UNIX environment variables set in the corresponding files.


### `initial_hardcoded_tests/`

The first step on the way towards this formalism was to gather actual errors or data quality issues. These were collected for the [DBpedia](http://dbpedia.org) by evaluating corresponding posts on DBpedia mailing lists like https://lists.sourceforge.net/lists/listinfo/dbpedia-discussion or https://lists.sourceforge.net/lists/listinfo/dbpedia-developers as well as web sites like http://stackoverflow.com or http://answers.semanticweb.com . The findings from these sources were tried to be expressed in SPARQL queries that can be found in the `initial_hardcoded_tests` folder.


### `run_test.sh`

To be able to run single queries the shell script `run_test.sh` is provided. To execute it the following parameters have to be given:

1. the path to an endpoint definition file, e.g. `endpoints/dbpedia.org`
2. the path to a SPARQL query file, e.g. `initial_hardcoded_tests/dbpedia-owl_city.sparql`
3. the maximum number of result triples

An example call with the corresponding result could be:
```bash
user@host ISWC_2013_evaluation_track $ ./run_test.sh endpoints/dbpedia.org initial_hardcoded_tests/semantic_birthdate_gt_deathdate.sparql 2
QUERY:
***************
# tests whether birthdate is larger than deathdate

select * { 
     ?s dbpedia-owl:birthDate ?birth . 
     ?s  dbpedia-owl:deathDate ?death .  
     FILTER ( ?birth > ?death )
} LIMIT 2 
***************

<sparql xmlns="http://www.w3.org/2005/sparql-results#" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd">
 <head>
  <variable name="s"/>
  <variable name="birth"/>
  <variable name="death"/>
 </head>
 <results distinct="false" ordered="true">
  <result>
   <binding name="s"><uri>http://dbpedia.org/resource/Tom_Lanning</uri></binding>
   <binding name="birth"><literal datatype="http://www.w3.org/2001/XMLSchema#date">1907-04-22</literal></binding>
   <binding name="death"><literal datatype="http://www.w3.org/2001/XMLSchema#date">0167-11-04</literal></binding>
  </result>
  <result>
   <binding name="s"><uri>http://dbpedia.org/resource/Richard_Lewis_(bishop_of_Llandaff)</uri></binding>
   <binding name="birth"><literal datatype="http://www.w3.org/2001/XMLSchema#date">1821-03-27</literal></binding>
   <binding name="death"><literal datatype="http://www.w3.org/2001/XMLSchema#date">0190-01-24</literal></binding>
  </result>
 </results>
</sparql>
```


### `generate_tests/`

Having written these initial tests, it was found out that in some cases test pattern bindings can be created automatically. So in `generate_tests` one can find the source code and the results of the approaches to generate such queries for disjoint OWL classes (`owl:disjointWith`) and functional properties (`owl:FunctionalProperty`).


### `spreadsheet_tests/`

To capture, structure and categorize data quality test patterns and test pattern bindings a spreadsheet was set up. The actual structures created correspond to the directory structure of the `spreadsheet_tests/queries` folder. Inside one can find category folders (the abbreviations are explained in the [paper](http://svn.aksw.org/papers/2013/ISWC_Databugger/public.pdf)) containing several pattern binding example folders. In these pattern binding example folders there are two SPARQL query files: One to get the count of all resources that could be affected (`prev.sparql`) and another to count the actual errors (`err.sparql`).
To run all these tests the shell script `run_tests.sh` has to be executed. `run_tests.sh` uses the endpoint definitions e.g. from `endpoints` and executes all the SPARQL queries found in a directory structure introduced above. To run the tests one has to provide the path to a folder containing SPARQL endpoint definitions (e.g. `endpoints`), the path to the aforementioned query folder structure (e.g. `spreadsheet_tests/queries`), and a path to write the results to. Calling the `run_tests.sh` script without any parameters, the following help message will be shown:
```bash
user@host spreadsheet_tests $ ./run_tests.sh 

usage: ./run_tests.sh <path_to_directory_of_sparql_endpoint_settings_files> <path_to_sparql_file_directory_hierarchy> <result_output_path>

the sparql file directory hierarchy should be of the structure

<template_dir>/
    <instanciation_dir>/
        *sparql

example call: ./run_tests.sh ../endpoints ./queries /tmp/results

```
Additionally gnuplot files to plot the query results and a script to execute them are provided.

