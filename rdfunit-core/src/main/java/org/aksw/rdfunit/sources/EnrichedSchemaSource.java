package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RDFReader;

/**
 * <p>EnrichedSchemaSource class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:53 PM
 * @version $Id: $Id
 */
public class EnrichedSchemaSource extends SchemaSource {

    /**
     * <p>Constructor for EnrichedSchemaSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param schemaReader a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public EnrichedSchemaSource(String prefix, String uri, RDFReader schemaReader) {
        super(prefix, uri, schemaReader);
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
