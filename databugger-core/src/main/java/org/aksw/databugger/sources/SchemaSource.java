package org.aksw.databugger.sources;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 9/16/13 1:51 PM
 */
public class SchemaSource extends Source {

    public final String schema;

    private final Model model = ModelFactory.createDefaultModel();

    public SchemaSource(String uri) {
        super(uri);
        this.schema = uri;
    }

    public SchemaSource(String uri, String schema) {
        super(uri);
        this.schema = schema;
    }
}
