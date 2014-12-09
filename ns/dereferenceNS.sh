#!/bin/sh
# RDF/XML
rapper -i turtle -o rdfxml-abbrev core.ttl > core.rdf

# OWL/XML
wget "http://mowl-power.cs.man.ac.uk:8080/converter/convert?ontology=http://rdfunit.aksw.org/ns/core.ttl&format=OWL/XML"  -O core.owl

# HTML Display
wget http://www.essepuntato.it/lode/http://rdfunit.aksw.org/ns/core.rdf -O core.html
