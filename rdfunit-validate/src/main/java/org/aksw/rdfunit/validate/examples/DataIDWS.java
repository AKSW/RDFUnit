package org.aksw.rdfunit.validate.examples;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.UndefinedSerializationException;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticWrapper;
import org.aksw.rdfunit.validate.ws.RDFUnitWebService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * a DataID Validator
 * Created: 6/18/14 10:13 AM
 */
public class DataIDWS extends RDFUnitWebService {

    @Override
    public void init() throws ServletException {
        RDFUnitStaticWrapper.initWrapper("https://raw.githubusercontent.com/dbpedia/dataId/master/ontology/dataid.ttl");
    }

    @Override
    protected RDFUnitConfiguration getConfiguration(HttpServletRequest httpServletRequest) throws ParameterException {

        String type = httpServletRequest.getParameter("t");
        if (type == null || !(type.equals("text") || type.equals("uri"))) {
            throw new ParameterException("'t' must be one of text or uri");
        }

        String source = httpServletRequest.getParameter("s");
        if (source == null || source.isEmpty()){
            throw new ParameterException("'s' must be defined and not empty");
        }

        boolean isText = type.equals("text");

        String datasetName = source;
        if (isText) {
            datasetName = "custom-text";
        }

        String inputFormat = "";
        if (isText) {
            inputFormat = httpServletRequest.getParameter("i");
            if (inputFormat == null || inputFormat.isEmpty()) {
                throw new ParameterException("'i' must be defined when -t = 'text'");
            }
        }

        String outputFormat = httpServletRequest.getParameter("o");
        if (outputFormat == null || outputFormat.isEmpty()){
            outputFormat = "html";
        }

        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetName, "../data/");
        configuration.setTestCaseExecutionType(TestCaseExecutionType.rlogTestCaseResult);

        if (isText) {
            try {
                configuration.setCustomTextSource(source, inputFormat);
            } catch (UndefinedSerializationException e) {
                throw new ParameterException(inputFormat, e);
            }

        }

        try {
            configuration.setOutputFormatTypes(Arrays.asList(outputFormat));
        } catch (UndefinedSerializationException e) {
            throw new ParameterException(e.getMessage(), e);
        }

        return configuration;
    }

    @Override
    protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final Source dataset) {
        return RDFUnitStaticWrapper.getTestSuite();
    }

    @Override
    protected Model validate(final RDFUnitConfiguration configuration, final Source dataset, final TestSuite testSuite) throws TestCaseExecutionException {
        return RDFUnitStaticWrapper.validate(configuration.getTestCaseExecutionType(), dataset, testSuite);
    }

    @Override
    protected void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException {
        String helpMessage =
                "\n -t\ttype: One of 'text|uri'" +
                        "\n -s\tsource: Depending on -t it can be either a uri or text" +
                        "\n -i\tInput format (in case of text type):'turtle|ntriples|rdfxml" + //|JSON-LD|RDF/JSON|TriG|NQuads'" +
                        "\n -o\tOutput format:'html(default)|turtle|jsonld|rdfjson|ntriples|rdfxml|rdfxml-abbrev" + //JSON-LD|RDF/JSON|TriG|NQuads'"
                        "";

    }
}
