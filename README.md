RDFUnit - RDF Unit Testing Suite
==========

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.aksw.rdfunit/rdfunit-parent/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.aksw.rdfunit/rdfunit-parent)
[![Build Status](https://travis-ci.org/AKSW/RDFUnit.svg?branch=master)](https://travis-ci.org/AKSW/RDFUnit)
[![Coverity Scan Build Status](https://scan.coverity.com/projects/2650/badge.svg?flat=1)](https://scan.coverity.com/projects/2650)
[![Coverage Status](https://coveralls.io/repos/AKSW/RDFUnit/badge.svg?branch=master&service=github)](https://coveralls.io/github/AKSW/RDFUnit?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/02907c27b76141709e5a6e9682fc836c)](https://www.codacy.com/app/jimkont/RDFUnit)
[![codebeat badge](https://codebeat.co/badges/fc781acc-0a9f-4796-9d33-28d1ffb3b019)](https://codebeat.co/projects/github-com-aksw-rdfunit)
[![Project Stats](https://www.openhub.net/p/RDFUnit/widgets/project_thin_badge.gif)](https://www.ohloh.net/p/RDFUnit)



**Homepage**: http://rdfunit.aksw.org <br/>
**Documentation**: https://github.com/AKSW/RDFUnit/wiki  <br/>
**Slack #rdfunit**: https://dbpedia-slack.herokuapp.com/ <br/>
**Mailing list**: https://groups.google.com/d/forum/rdfunit (rdfunit [at] googlegroups.com)  <br/>
**Presentations**: http://www.slideshare.net/jimkont  <br/>
**Brief Overview**: https://github.com/AKSW/RDFUnit/wiki/Overview


RDFUnit is implemented on top of the [Test-Driven Data Validation Ontology](http://rdfunit.aksw.org/ns/core#) and designed to read and produce RDF that complies to that ontology only.
The main components that RDFUnit reads are
[TestCases (manual & automatic), TestSuites](https://github.com/AKSW/RDFUnit/wiki/TestCases),
[Patterns & TestAutoGenerators](https://github.com/AKSW/RDFUnit/wiki/Patterns-Generators).
RDFUnit also strictly defines the results of a TestSuite execution along with [different levels of result granularity](https://github.com/AKSW/RDFUnit/wiki/Results).

### Contents
 * [Basic Usage](#basic-usage)
 * [Using Docker](#using-docker)
 * [Supported Schemas](#supported-schemas)
 * [Acknowledgements](#acknowledgements)


### Basic usage

See [RDFUnit from Command Line](https://github.com/AKSW/RDFUnit/wiki/CLI) or `bin/rdfunit -h` for (a lot) more options but the simplest setting is as follows:

```console
$ bin/rdfunit -d <local-or-remote-location-URI>
```

What RDFUnit will do is:

1. Get statistics about all properties & classes in the dataset
1. Get the namespaces out of them and try to dereference all that exist in [LOV](http://lov.okfn.org)
1. Run our [Test Generators](https://github.com/AKSW/RDFUnit/wiki/Patterns-Generators) on the schemas and generate RDFUnit Test cases
1. Run the RDFUnit test cases on the dataset
1. You get a results report in html (by default) but you can request it in [RDF](http://rdfunit.aksw.org/ns/core#) or even multiple serializations with e.g.  `-o html,turtle,jsonld`
  * The results are by default aggregated with counts, you can request different levels of result details using `-r {status|aggregate|shacl|shacllite}`. See [here](https://github.com/AKSW/RDFUnit/wiki/Results) for more details.

You can also run:
```console
$ bin/rdfunit -d <dataset-uri> -s <schema1,schema2,schema3,...>
```

Where you define your own schemas and we pick up from step 3. You can also use prefixes directly (e.g. `-s foaf,skos`) we can get everything that is defined in [LOV](http://lov.okfn.org).

### Using Docker

A Dockerfile is provided to create a Docker image of the CLI of RDFUnit.

To create the Docker image:

```console
$ docker build -t rdfunit .
```

It is meant to execute a rdfunit command and then shutdown the container. If the output of rdfunit on stdout is not enough or you want to include files in the container, a directory could be mounted via Docker in order to create the output/result there or include files.

Here an example of usage:

```console
$ docker run --rm -it rdfunit -d https://awesome.url/file -r aggregate
```

This creates a temporary Docker container which runs the command, prints the results on stdout and stops plus removes itself. For further usage of CLI visit https://github.com/AKSW/RDFUnit/wiki/CLI.

### Supported Schemas

RDFUnit supports the following types of schemas

1. **OWL** (using CWA): We pick the most commons OWL axioms as well as schema.org. (see [[1]](https://github.com/AKSW/RDFUnit/labels/OWL),[[2]](https://github.com/AKSW/RDFUnit/issues/20) for details
1. **SHACL**: Full SHACL is almost available except for [a few SHACL constructs](https://github.com/AKSW/RDFUnit/issues/62). Whatever constructs we support can also run directly on SPARQL Endpoints
1. IBM **Resource Shapes**: The progress is tracked [here](https://github.com/AKSW/RDFUnit/issues/23) but as soon as SHACL becomes stable we will drop support for RS
1. **DSP** (Dublin Core Set Profiles): The progress is tracked [here](https://github.com/AKSW/RDFUnit/issues/22) but as soon as SHACL becomes stable we will drop support for DSP

Note that you can mix all of these constraints together and RDFUnit will validate the dataset against all of them.

### Acknowledgements

The first version  of RDFUnit (formely known as Databugger) was developed by AKSW as part of the PhD thesis of Dimitris Kontokostas. 
A lot of additional work for improvement, requirements & refactoring was performed through the [EU funded project ALIGNED](http://aligned-project.eu). Through the project, a lot of project partners provided feedback and contributed code like e.g.  Wolters Kluwers Germany and Semantic Web Company that are also users of RDFUnit.

There are also many [code contributors](https://github.com/AKSW/RDFUnit/graphs/contributors) as well as people submitted bug reports or provided constructive feedback.

In addition, RDFUnit used [Java profiler (JProfiler)](http://www.ej-technologies.com/products/jprofiler/overview.html) for optimizations
