find -name *.tests.Schema.ttl | xargs cat > tests.auto.ttl
find -name *.tests.Manual.ttl | xargs cat > tests.manual.ttl
find -name *.tests.EnrichedSchema.ttl | xargs cat > tests.enriched.ttl
find -name *.results.ttl | xargs cat > tests.ttl
