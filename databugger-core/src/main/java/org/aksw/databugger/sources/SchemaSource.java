package org.aksw.databugger.sources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.exceptions.TripleReaderException;
import org.aksw.databugger.tripleReaders.TripleReader;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

import java.io.File;
import java.io.FileInputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:51 PM
 */
public class SchemaSource extends Source {

    protected final String schema;
    protected final TripleReader schemaReader;

    public SchemaSource(String prefix, String uri, TripleReader schemaReader) {
        this(prefix, uri, uri, schemaReader);
    }

    public SchemaSource(String prefix, String uri, String schema, TripleReader schemaReader) {
        super(prefix, uri);
        this.schema = schema;
        this.schemaReader = schemaReader;
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
        } catch (TripleReaderException e) {
            log.error("Cannot load ontology: " + schema + " Reason: " +e.getMessage());
        }
        return new QueryExecutionFactoryModel(model);
    }
}
