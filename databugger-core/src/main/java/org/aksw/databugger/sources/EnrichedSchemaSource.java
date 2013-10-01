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
 * Created: 9/16/13 1:53 PM
 */
public class EnrichedSchemaSource extends SchemaSource {

    public EnrichedSchemaSource(String prefix, String uri) {
        super(prefix, uri);
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        try {
            File f = new File(this.getCacheFile());
            if (f.exists()) {
                model.read(new FileInputStream(f), null, "TURTLE");
            }
            else {
                log.error("Cannot read ontology from : " + f.getAbsolutePath() + "\nPlease enrich first");
                System.exit(-1);
            }
        } catch (Exception e) {
            log.error("Cannot load ontology from URI: "+schema);
        }
        return new QueryExecutionFactoryModel(model);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.EnrichedSchema;
    }

    @Override
    public  String getPrefix() {
        return  "enriched-" + super.getPrefix() ;
    }

    public void enrichDataset(){
        enrichDataset(0.9);
    }

    //TODO enrich
    // call default DLLearner's output
    public void enrichDataset(double confidence){

    }

}
