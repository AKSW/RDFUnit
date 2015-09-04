RDFUnit - RDF Unit Testing Suite
==========

[![Build Status](https://travis-ci.org/AKSW/RDFUnit.svg?branch=master)](https://travis-ci.org/AKSW/RDFUnit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.aksw/rdfunit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.aksw/rdfunit)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2650/badge.svg?flat=1)](https://scan.coverity.com/projects/2650)
[![Project Stats](https://www.openhub.net/p/RDFUnit/widgets/project_thin_badge.gif)](https://www.ohloh.net/p/RDFUnit)



**Homepage**: http://rdfunit.aksw.org <br/>
**Documentation**: https://github.com/AKSW/RDFUnit/wiki  <br/>
**Mailing list**: https://groups.google.com/d/forum/rdfunit (rdfunit [at] googlegroups.com)  <br/>
**Presentations**: http://www.slideshare.net/jimkont  <br/>
**Brief Overview**: https://github.com/AKSW/RDFUnit/wiki/Overview

RDFUnit is implemented on top of the [Test-Driven Data Validation Ontology](http://rdfunit.aksw.org/ns/core#) and designed to read and produce RDF that complies to that ontology only.
The main components that RDFUnit reads are 
[TestCases (manual & automatic), TestSuites](https://github.com/AKSW/RDFUnit/wiki/TestCases), 
[Patterns & TestAutoGenerators](https://github.com/AKSW/RDFUnit/wiki/Patterns-Generators). 
RDFUnit also strictly defines the results of a TestSuite execution along with [different levels of result granularity](https://github.com/AKSW/RDFUnit/wiki/Results).

### Basic usage

See [RDFUnit from Command Line](https://github.com/AKSW/RDFUnit/wiki/CLI) for (a lot) more options but the simplest setting is as follows:

```console
$ bin/rdfunit -d <local-or-remote-location-URI>
```

What RDFUnit will do is:

1. Get statistics about all properties & classes in the dataset
1. Get the namespaces out of them and try to dereference all that exist in [LOV](http://lov.okfn.org)
1. Run our [Test Generators](https://github.com/AKSW/RDFUnit/wiki/Patterns-Generators) on the schemas and generate RDFUnit Test cases
1. Run the RDFUnit test cases on the dataset
1. You get a results report in html (by default) but you can request it in [RDF](http://rdfunit.aksw.org/ns/core#) or even multiple serializations with e.g.  `-o html,turtle,jsonld`

You can also run:
```console
$ bin/rdfunit -d <dataset-uri> -s <schema1,schema2,schema3,...>
```

Where you define your own schemas and we pick up from step 3. You can also use prefixes directly (e.g. `-s foaf,skos`) we can get everything that is defined in [LOV](http://lov.okfn.org).


[![Java profiler](http://www.ej-technologies.com/images/product_banners/jprofiler_small.png)](http://www.ej-technologies.com/products/jprofiler/overview.html)
