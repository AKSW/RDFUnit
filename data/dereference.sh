#!/usr/bin/env bash

find -name *.tests.Schema.ttl | xargs cat > tests.auto.ttl
find -name *.tests.Manual.ttl | xargs cat > tests.manual.ttl
find -name *.tests.EnrichedSchema.ttl | xargs cat > tests.enriched.ttl
find -name *.results.ttl | xargs cat > tests.ttl

rapper -i turtle -o rdfxml patterns.ttl           > patterns.rdf
rapper -i turtle -o rdfxml testAutoGenerators.ttl > testAutoGenerators.rdf
rapper -i turtle -o rdfxml tests.ttl              > tests.rdf
