package org.aksw.databugger.sources;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.databugger.tripleReaders.TripleReader;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

import java.io.File;
import java.io.FileInputStream;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:53 PM
 */
public class EnrichedSchemaSource extends SchemaSource {

    public EnrichedSchemaSource(String prefix, String uri, TripleReader schemaReader) {
        super(prefix, uri, schemaReader);
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
