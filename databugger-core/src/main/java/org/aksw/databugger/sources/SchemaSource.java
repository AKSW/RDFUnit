package org.aksw.databugger.sources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.enums.TestAppliesTo;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:51 PM
 */
public class SchemaSource extends Source {

    public final String schema;

    public SchemaSource(String uri) {
        this(uri,uri);

    }

    public SchemaSource(String uri, String schema) {
        super(uri);
        this.schema = schema;
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Schema;
    }

    @Override
    protected QueryExecutionFactory initQueryFactory() {
        Model model = ModelFactory.createDefaultModel();
        model.read(schema);
        return new QueryExecutionFactoryModel(model);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
