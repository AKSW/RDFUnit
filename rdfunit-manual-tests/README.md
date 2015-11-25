## RDFUnit Manual Tests module

Here we bundle all manual tests defined in RDFUnit that can be associated with specific vocabularies/ontologies or datasets. 
For example we include tests associated with SKOS that are currently not expressed by the skos rdfs defintion (e.g. `skos:prefLabel should have at most one value for every language`).

You can contribute to extend manual tests to existing schemas in `org/aksw/rdfunit/tests/Manual/*`

If you want to define manual tests for a new schema or dataset you have to 
 * create the corresponding file in the aforementioned folder and 
 * create an entry in `org.aksw.rdfunit.resources.ManualTestResources`. This will allow the JUnit tests to pick up and validate your test definitions.
 
We'd be more that happy to accept your pull requests ;)

### Notes
This module is used from `rdfunit-validate` module on a complete RDFUnit run configuration. 
`rdfunit-core` does not include resources of this module so if you are using RDFUnit as a library you will have to explicitely mark it as a dependency.