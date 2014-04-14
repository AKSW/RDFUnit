RDFUnit
==========

This repository contains the *RDFUnit* -- a tool for test-driven quality evaluation of Linked Data quality.
Further background information about the underlying *Test Driven Data Quality Methodology* can be looked up in the following publications: [methodology (WWW2014)](http://svn.aksw.org/papers/2014/WWW_Databugger/public.pdf), [demo paper (WWW2014)](http://svn.aksw.org/papers/2014/WWW_Databugger_demo/public.pdf) and [ontology definition (ESWC2014)](http://svn.aksw.org/papers/2014/ESWC_NLP_Cleansing/public.pdf).
The results of the methodology paper are available [here](https://github.com/AKSW/RDFUnit/tree/master/data/archive/WWW_2014) .
This methodology defines a set of data quality test patterns which are SPARQL query templates expressing certain common error conditions.
After having instantiated such patterns for a concrete dataset possible errors of the corresponding kind can be detected. An example would be the following pattern:

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

The RDFUnit tool provides a [vocabulary](http://rdfunit.aksw.org/ns#) to define such pattern instantiations called *data quality test cases*.
Apart from manual instantiations some of the test patterns can also be instantiated automatically.
These test cases are then specific to a given schema and can be re-used.
For now we support the following axioms: 
`rdfs:domain`,
`rdfs:range`,
`owl:cardinality`,
`owl:minCardinality`,
`owl:maxCardinality`,
`owl:functionalProperty`,
`owl:disjointClass`,
`owl:propertyDisjointWith`,
`owl:complementOf`,
`owl:InverseFunctionalProperty`,
`owl:AsymmetricProperty` and `owl:IrreflexiveProperty`.
and we plan to extend them over time.

*Please note that this work is still in beta*

### Usage

To run a data quality assessment of a certain SPARQL endpoint the following steps have to be done:

1. Get/Create manual data quality test cases for the used schemas (optional)
2. Get/create manual data quality test cases for the evaluated dataset (optional)
3. Enrich the schema of the considered dataset (This is just in case a light-weight ontology/schema is used that defines only a few schema/ontology constraints that could be used for pattern instantiation. 
   The enrichment process will try to infer constraints for the dataset based on the actual data using the [DL-Learner](http://dl-learner.org/Projects/DLLearner) tool.) (optional)
4. Get/Create automatically instantiated test cases for the schemas used inthe evaluation (automatic)
5. Run the actual assessment based on the tests created

To do so, you first have to clone this repository and install the software using the Maven 3 build tool as follows:
```console
$ git clone https://github.com/AKSW/RDFUnit.git
$ cd RDFUnit/
$ mvn clean install

# argument help
$ bin/rdfunit -h

# Simple call (SPARQL)
$ bin/rdfunit -d <dataset-uri> -e <endpoint>  -g <graph1,graph2,...>  -s <schema1,schema2,schema3,...>

# with use of enriched ontnology
$ bin/rdfunit -d <dataset-uri> -e <endpoint>  -g <graph1,graph2,...>  -s <schema1,schema2,schema3,...> -p <enriched-schema-prefix>

# Simple call (Dereferencing)
$ bin/rdfunit -d <dataset-uri> -s <schema1,schema2,schema3,...>

# Simple call (Dereferencing when you want to keep the manual tests for a dataset)
$ bin/rdfunit -d <dataset-uri> -U <source-URI> -s <schema1,schema2,schema3,...>
```

To brief the options, you need to provide:
- of the *dataset* with the general *URI* `http://dbpedia.org`
- the *SPARQL endpoint* `http://dbpedia.org/sparql`
- referring to the *graph* `http://dbpedia.org`
- that uses the *schemas* `owl`, `dbo`, `foaf`, `dcterms`, `dc`, `skos`, `geo`, `prov`
- with the *enriched schema prefix* `dbo`



Note that all schemas are resolved using the LOV dataset and are downloaded automatically.
The framework automatically loads all associated tests (manual, automatic and enriched) that are defined (See next section) and at the moment uses files to store/retrieve them.
Future versions of the tool will work directly with a SPARQL endpoint.

An example call with already defined manual and enriched test cases is:
```console
$ bin/rdfunit -d http://dbpedia.org/ -e http://dbpedia.org/sparql -g http://dbpedia.org -s owl,dbo,foaf,dcterms,dc,skos,geo,prov -p dbo
```

### Data folder structure

the data folder consists of the following folders:
* `ontology` holds the rdfunit ontology
* `results` stores the results of the evaluation in RDF
* `tests` stores the test cases along with a cache of the schema source in turtle
    * `Schema` holds the automatically generated test cases are stored
    * `EnrichedSchema` holds the automatically generated test cases from an enriched schema
    * `Manual` here we define the manual test cases for a source. Note that a source can be either a schema or a dataset and we use their URI to destinguish them.


### Create manual data quality test cases

We plan to provide an interface to ease the manual pattern instantiation.
At the moment you can store your manually generated queries in the respective `Manual` subfolder.

### Get/create data quality test cases for schemas

RDFUnit already resolves all schema prefixes with the LOV dataset and can automatically download and generate tests for all 361 LOV vocabularies.
If your vocabulary you want to use is not there you can place it in the RDFUnitUtils.fillSchemaService() accordingly.

### 3) Enrich the schema of the considered dataset

The enrichment of the schema is not necessary per se, but may lead to better results in cases where the schema/ontology of the considered dataset is in some sense light-weight, meaning that there are only a few constraints that can be used for automatic pattern instantiation.

Since the enrichment is performed by an external tool (the [DL-Learner](http://dl-learner.org/Projects/DLLearner)) we refer to the [project site](http://dl-learner.org/wiki/SVNRun) for further details how to run it.

An example for the enrichment of the [DBpedia](http://dbpedia.org) dataset could be
```console
user@host interfaces $ mvn exec:java -e -Dexec.mainClass="org.dllearner.cli.Enrichment" -Dexec.args="-e http://dbpedia.org/sparql -g http://dbpedia.org -f rdf/xml -o enrichment_dbpediaorg.xml -s enrichment_dbpediaorg.owl -l -1 -t 0.9"
```
