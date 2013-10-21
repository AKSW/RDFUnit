Databugger
==========

This repository contains the *Databugger* -- a tool for test-driven quality evaluation of Linked Data quality. Further background information about the underlying *Test Driven Data Quality Methodology* can be looked up in our [submission](http://svn.aksw.org/papers/2014/WWW_Databugger/public.pdf) for World Wide Web Conference 2014. This methodology defines 16 data quality test patterns which are SPARQL query templates expressing certain common error conditions. After having instantiated such patterns for a concrete dataset possible errors of the corresponding kind can be detected. An example would be the following pattern:

```
SELECT ?s WHERE {
            ?s %%P1%% ?v1 .
            ?s %%P2%% ?v2 .
            FILTER ( ?v1 %%OP%% ?v2 ) }
```
This pattern expresses the error condition where the values of two properties (`%%P1%%`, `%%P2`) are not in a specific ordering with respect to an operator (`%%OP%%` in { <, <=, >, >=, =, != }). A possible instantiation for this pattern, applicable to the [DBpedia](http://dbpedia.org) would be
```
SELECT ?s WHERE {
            ?s dbo:birthDate ?v1 .
            ?s dbo:deathDate ?v2 .
            FILTER ( ?v1 > ?v2 ) }
```
referring to the case where an individual died before it was born.
The Databugger tool provides a vocabulary to define such pattern instantiations called *data quality test cases*. Apart from a manual instantiations some of the test patterns can also be instantiated automatically. These test cases are then specific to a given schema and can be re-used.
To run a data quality assessment of a certain SPARQL endpoint the following steps have to be done:
1. Create manual data quality test cases (optional)
2. Get/create data quality test cases for schemas re-used in the considered dataset
3. Enrich the schema of the considered dataset (This is just in case a light-weight ontology/schema is used that defines only a few schema/ontology constraints that could be used for pattern instantiation. The enrichment process will try to infer constraints for the dataset based on the actual data using the [DL-Learner](http://dl-learner.org/Projects/DLLearner) tool.) (optional)
4. Run the automatic pattern instantiation for the dataset to be assessed
5. Run the actual assessment based on the tests created

To do so, you first have to clone this repository and install the software using the Maven 3 build tool as follows:
```console
user@host develop $ git clone https://github.com/AKSW/Databugger.git
user@host develop $ cd Databugger/
user@host Databugger $ mvn clean install
```

### 1) Create manual data quality test cases

TODO

### 2) Get/create data quality test cases for schemas

If there are already data quality test cases for a schema used in the dataset, these can be copied to the `data/tests/Schema/<schema-path>`. The schema-path is the schema URI having stripped off the protocol identifier. So for the OWL schema (`http://www.w3.org/2002/07/owl#`) the directory structure would be `data/tests/Schema/www.w3.org/2002/07/owl/`. Inside this directory a file named `<schema-prefix>.tests.Schema.ttl` is expected.
Currently Databugger already provides schema test cases for the following schemas:
- `http://purl.org/dc/elements/1.1/`
- `http://purl.org/dc/terms/`
- `http://www.w3.org/2002/07/owl#`
- `http://www.w3.org/2003/01/geo/wgs84_pos#`
- `http://www.w3.org/2004/02/skos/core#`
- `http://www.w3.org/ns/prov#`
- `http://xmlns.com/foaf/0.1/`

### 3) Enrich the schema of the considered dataset

The enrichment of the schema is not necessary per se, but may lead to better results in cases where the schema/ontology of the considered dataset is in some sense light-weight, meaning that there are only a few constraints that can be used for automatic pattern instantiation.
Since the enrichment is run by an external tool, the [DL-Learner](http://dl-learner.org/Projects/DLLearner), we refer to the [project site](http://dl-learner.org/wiki/SVNRun) for further details how to run the enrichment.
An example to invoke the DL-Learner could be
```console
user@host interfaces $ mvn exec:java -e -Dexec.mainClass="org.dllearner.cli.Enrichment" -Dexec.args="-e http://dbpedia.org/sparql -g http://dbpedia.org -f rdf/xml -o enrichment_dbpediaorg.xml -s enrichment_dbpediaorg.owl -l -1 -t 0.9"
```

### 4) Run the automatic pattern instantiation

The automatic pattern instantiation will be performed automatically if Databugger is run. The results of the instantiation will be cached and can be re-used once they were created.
The tests will be found in the directory `data/tests/schema/<schema-path>/`

### 5) Run the assessment

Databugger is invoked using the start script given in the bin/ directory of the repository. There are several call parameters that can be looked up calling
```console
user@host Databugger $ bin/databugger -h
```
To start databugger using
- the *SPARQL endpoint* `http://dbpedia.org/sparql`
- of the *dataset* with the general *URI* `http://dbpedia.org/`
- with the *schema id* `dbpedia.org`
- referring to the *graph* `http://dbpedia.org/ontology`
- that uses the *schemas* `owl`, `dbo`, `foaf`, `dcterms`, `dc`, `skos`, `geo`, `prov`
the command to invoke would be
```console
user@host Databugger $ bin/databugger -e http://dbpedia.org/sparql -d http://dbpedia.org/ -i dbpedia.org -g http://dbpedia.org/ontology -s owl,dbo,foaf,dcterms,dc,skos,geo,prov
```
Running the command above will create a directory `data/results/` containing the test results as TURTLE file.
