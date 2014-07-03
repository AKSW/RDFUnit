RDFUnit
==========

**Note**:We are porting this documentation in the [github wiki](https://github.com/AKSW/RDFUnit/wiki) and some parts are more up-to-date there

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

1. (automatic) RDFUnit automatically instantiates test cases for the given schemas used in the evaluation
2. (optional improvement) Get/Create manual data quality test cases for the used schemas
3. (optional improvement) Get/create manual data quality test cases for the evaluated dataset
4. (optional improvement) Enrich the schema of the considered dataset (This is just in case a light-weight ontology/schema is used that defines only a few schema/ontology constraints that could be used for pattern instantiation.
   The enrichment process will try to infer constraints for the dataset based on the actual data using the [DL-Learner](http://dl-learner.org/Projects/DLLearner) tool.)
5. Run the actual assessment based on the test cases created in the previous step


See the wiki for execution information


### Create manual data quality test cases

We plan to provide an interface to ease the manual pattern instantiation.
At the moment you can store your manually generated queries in the respective `Manual` subfolder.

### Get/create data quality test cases for schemas

RDFUnit already resolves all schema prefixes with the LOV dataset and can automatically download and generate tests for all 361 LOV vocabularies.
If your vocabulary you want to use is not there you can place it in the RDFUnitUtils.fillSchemaService() accordingly.


