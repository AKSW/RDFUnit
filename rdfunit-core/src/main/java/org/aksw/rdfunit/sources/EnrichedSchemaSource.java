package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RdfReader;

/**
 * <p>EnrichedSchemaSource class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:53 PM
 * @version $Id: $Id
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

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.EnrichedSchema;
    }

    /** {@inheritDoc} */
    @Override
    public String getPrefix() {
        return "enriched-" + super.getPrefix();
    }

    /**
     * <p>enrichDataset.</p>
     */
    public void enrichDataset() {
        enrichDataset(0.9);
    }

    //TODO enrich
    // call default DLLearner's output
    /**
     * <p>enrichDataset.</p>
     *
     * @param confidence a double.
     */
    public void enrichDataset(double confidence) {

    }

}
