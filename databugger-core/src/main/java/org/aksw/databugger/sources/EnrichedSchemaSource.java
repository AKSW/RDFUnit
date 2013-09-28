package org.aksw.databugger.sources;

import org.aksw.databugger.enums.TestAppliesTo;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:53 PM
 */
public class EnrichedSchemaSource extends SchemaSource {

    private final DatasetSource dataset;

    public EnrichedSchemaSource(String uri, DatasetSource dataset) {
        super(uri);
        this.dataset =  dataset;
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.EnrichedSchema;
    }

    @Override
    protected String getCacheFolder(){
        return super.getCacheFolder() + dataset.getCacheFolder();
    }

    //TODO enrich
    // call default DLLearner's output
    public void enrichDataset(){

    }

}
