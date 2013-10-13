package org.aksw.databugger.sources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:51 PM
 */
public class SchemaSource extends Source {

    protected final String schema;

    public SchemaSource(String prefix, String uri) {
        this(prefix, uri, uri);

    }

    public SchemaSource(String prefix, String uri, String schema) {
        super(prefix, uri);
        this.schema = schema;
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Schema;
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        try {
            File f = new File(this.getCacheFile());
            if (f.exists()) {
                model.read(new FileInputStream(f), null, "TURTLE");
            } else {
                model.read(schema);
                f.getParentFile().mkdirs();
                model.write(new FileOutputStream(f), "TURTLE");
            }
        } catch (Exception e) {
            log.error("Cannot load ontology from URI: " + schema);
        }
        return new QueryExecutionFactoryModel(model);
    }
}
