package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RdfReader;

/**
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:53 PM
 */
public class EnrichedSchemaSource extends SchemaSource implements Source{


    EnrichedSchemaSource(SourceConfig sourceConfig, RdfReader schemaReader) {
        super(sourceConfig, schemaReader);
    }

    EnrichedSchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader) {
        super(sourceConfig, schema, schemaReader);
    }

    EnrichedSchemaSource(SchemaSource source) {
        super(source);
    }


    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.EnrichedSchema;
    }


    @Override
    public String getPrefix() {
        return "enriched-" + super.getPrefix();
    }

    public void enrichDataset() {
        enrichDataset(0.9);
    }

    //TODO enrich
    // call default DLLearner's output
    public void enrichDataset(double confidence) {

    }

}
