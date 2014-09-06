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
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:51 PM
 */
public class SchemaSource extends Source {

    protected final String schema;
    protected final RDFReader schemaReader;

    public SchemaSource(String prefix, String uri, RDFReader schemaReader) {
        this(prefix, uri, uri, schemaReader);
    }

    public SchemaSource(String prefix, String uri, String schema, RDFReader schemaReader) {
        super(prefix, uri);
        this.schema = schema;
        this.schemaReader = schemaReader;
    }

    public SchemaSource(SchemaSource source) {
        super(source);
        this.schema = source.getSchema();
        this.schemaReader = source.schemaReader;
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Schema;
    }

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

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return getPrefix() + " (" + getSchema() + ")";
    }

}
