# W3c Data Quality Vocabulary (DQV) Report

RDFUnit- W3c DQV provides an API to generate DQV reports from a [rut:TestExecutions](http://rdfunit.aksw.org/ns/core#TestExecution).
Scroll at the end for code samples.

## Usage & Code Samples

[org.aksw.rdfunit.dqv.RdfunitDqv](src/main/java/org/aksw/rdfunit/dqv/RdfunitDqv.java) contains example code

```
   # display a help message
   $bin/dqv-report -h
     
   # read a set of test executions from {input-file} and write a quality report in {output-file}
   $bin/dqv-report -i {input-file} -o {output-file}
```

## More Details

#### W3c DQV
The Data Quality Vocabulary (DQV) is foreseen as an extension to DCAT that provides the ability to publish, exchange and consume quality metadata, for every step of a dataset's lifecycle.
The W3C DQV integration will be based on the SHACL (`ShaclTestCaseResult`) reporting format.
From every `rut:TestExecution` we read part of the execution metadata and the list of SHACL `sh:ValidationResult`.

### TestExecution Metadata
At the moment we need only `rut:totalTriples` from the TestExecution in order to normalize the metrics 

```
  ex:testExecutionIRI a rut:TestExecution, prov:Activity; 
    # ...
    rut:totalTriples “10”^^xsd:Integer.
```

### SHACL validation instances
Each violation instance adheres to the SHACL vocabulary and is of type `sh:ValidationResult`. 
SHACL defines different properties that can be attached to a validation result. For example sh:message, sh:focusNode, sh:object, etc. For the purpose of the DQV report we focus only on sh:sourceConstraint which points to the constraint that lead to this violation. For example: 

```
  <v1> a sh:ValidationResult ;
    #...
    prov:wasGeneratedBy ex:testExecutionIRI ;
    sh:sourceConstraint ex:CardinalityConstraint .
```

SHACL is still in progress and it is not yet clear if SHACL will define persistent IRIs for each SHACL constraint. 
However, even if it did we do not want to be limited by the SHACL constraints and thus, we created a general constraint mapping framework. 
We define a general list of metrics and dimensions in http://rdfunit.aksw.org/ns/rdqv (in progress).
We provide a default set of mappings of `sh:sourceConstraint` values to `dqv:Metric` [here](src/main/resources/org/aksw/rdfunit/dqv/metricMappings.ttl).
Constraints that are not mapped are automatically mapped to `rdqv:UnclassifiedMatric` and results without an `sh:sourceConstraint` are mapped to `rdqv:UndefinedMetric`.
If there is need to, we will add additional APIs to override or extend these mappings.

### Example result
The aforementioned validation result would provide the following DQV quality report:

```
  ex:testExecutionIRI dqv:hasQualityMeasure ex:qm1.

  ex:qm1 dqv:computedOn ex:testExecutionIRI ;
    dqv:hasMetric ex:CardinalityMetric
    dqv:value "0.1"^^xsd:double . # 1 instance / 10 triples
```

