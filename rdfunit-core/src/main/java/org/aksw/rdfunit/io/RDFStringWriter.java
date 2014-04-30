package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.services.PrefixService;

import java.io.StringWriter;

/**
 * User: Dimitris Kontokostas
 * Writes a model to a string (for display in screen / web page)
 * Created: 11/14/13 5:12 PM
 */
public class RDFStringWriter extends DataWriter {

    private final StringBuilder str;
    private final String format;

    public RDFStringWriter() {
        this(new StringBuilder(), "TURTLE");
    }

    public RDFStringWriter(StringBuilder str) {
        this(str, "TURTLE");
    }

    public RDFStringWriter(StringBuilder str, String format) {
        this.str = str;
        this.format = format;
    }

    public String getString() {
        return str.toString();
    }

    @Override
    public void write(QueryExecutionFactory qef) throws TripleWriterException {
        try {

            StringWriter out = new StringWriter();
            Model model = SparqlUtils.getModelFromQueryFactory(qef);
            model.setNsPrefixes(PrefixService.getPrefixMap());
            model.write(out, format);
            str.append(out.toString());
        } catch (Exception e) {
            throw new TripleWriterException(e.getMessage());
        }

    }
}
