package org.aksw.rdfunit.validate.examples;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.aksw.rdfunit.validate.ws.RDFUnitWebService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/18/14 10:13 AM
 */
public class DataIDWS extends RDFUnitWebService {

    @Override
    public void init() throws ServletException {
        RDFUnitStaticWrapper.initWrapper("http://dataid.dbpedia.org/ns/core#");
    }

    @Override
    protected RDFUnitConfiguration getConfiguration(HttpServletRequest httpServletRequest) throws ParameterException {
        throw new ParameterException("not implemented");
    }

    @Override
    protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final Source dataset) {
        return RDFUnitStaticWrapper.getTestSuite();
    }

    @Override
    protected Model validate(final RDFUnitConfiguration configuration, final Source dataset, final TestSuite testSuite) throws TestCaseExecutionException {
        return RDFUnitStaticWrapper.validate(configuration, dataset, testSuite);
    }

    @Override
    protected void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException {
        String helpMessage =
                    "\n -t\ttype: One of 'text|uri'" +
                    "\n -s\tsource: Depending on -t it can be either a uri or text" +
                    "\n -i\tInput format (in case of text type):'Turtle|N-Triples|RDF/XML|JSON-LD|RDF/JSON|TriG|NQuads'" +
                    "\n -o\tInput format (defaults to html):'html|Turtle|N-Triples|RDF/XML|JSON-LD|RDF/JSON|TriG|NQuads'"
                ;

    }
}
