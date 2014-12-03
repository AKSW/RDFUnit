package org.aksw.rdfunit.sources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderException;

/**
 * <p>SchemaSource class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:51 PM
 * @version $Id: $Id
 */
public class SchemaSource extends Source {

    protected final String schema;
    protected final RDFReader schemaReader;

    /**
     * <p>Constructor for SchemaSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param schemaReader a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public SchemaSource(String prefix, String uri, RDFReader schemaReader) {
        this(prefix, uri, uri, schemaReader);
    }

    /**
     * <p>Constructor for SchemaSource.</p>
     *
     * @param prefix a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @param schema a {@link java.lang.String} object.
     * @param schemaReader a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public SchemaSource(String prefix, String uri, String schema, RDFReader schemaReader) {
        super(prefix, uri);
        this.schema = schema;
        this.schemaReader = schemaReader;
    }

    /**
     * <p>Constructor for SchemaSource.</p>
     *
     * @param source a {@link org.aksw.rdfunit.sources.SchemaSource} object.
     */
    public SchemaSource(SchemaSource source) {
        super(source);
        this.schema = source.getSchema();
        this.schemaReader = source.schemaReader;
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Schema;
    }

    /** {@inheritDoc} */
    @Override
    protected QueryExecutionFactory initQueryFactory() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        try {
            schemaReader.read(model);
        } catch (RDFReaderException e) {
            log.error("Cannot load ontology: " + getSchema() + " Reason: " + e.getMessage(), e);
        }
        return new QueryExecutionFactoryModel(model);
    }

    /**
     * <p>Getter for the field <code>schema</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSchema() {
        return schema;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getPrefix() + " (" + getSchema() + ")";
    }

}
