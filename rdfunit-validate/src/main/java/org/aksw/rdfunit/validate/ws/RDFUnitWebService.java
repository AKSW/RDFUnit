package org.aksw.rdfunit.validate.ws;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.io.RDFWriter;
import org.aksw.rdfunit.io.RDFWriterFactory;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.validate.ParameterException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dimitris Kontokostas
 *         A super class that can serve as an easy validation service based on RDFUnit
 * @since 6/18/14 8:52 AM
 */
public abstract class RDFUnitWebService extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        // This function is executed on first run and can be used for initialization
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    /**
     * Has all the workflow logic for getting the parameters, performing a validation and writing the output
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    private final void handleRequestAndRespond(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        RDFUnitConfiguration configuration = null;
        try {
            configuration = getConfiguration(httpServletRequest);
        } catch (ParameterException e) {
            String message = e.getMessage();
            if (message != null) {
                printMessage(httpServletResponse, message);
            }
            printHelpMessage(httpServletResponse);
            httpServletResponse.getWriter().close();
            return;
        }
        assert (configuration != null);

        final Source dataset = configuration.getTestSource();

        final TestSuite testSuite = getTestSuite(configuration, dataset);
        assert (testSuite != null);

        Model results = null;
        try {
            results = validate(configuration, dataset, testSuite);
        } catch (TestCaseExecutionException e) {

        }
        assert (results != null);

        try {
            writeResults(configuration, results, httpServletResponse);
        } catch (TripleWriterException e) {
            printMessage(httpServletResponse, e.getMessage());
        }
    }

    /**
     * Writes the output of the validation to the HttpServletResponse
     *
     * @param configuration       an RDFUnitConfiguration object generated with getConfiguration
     * @param model               a Model generated with validate()
     * @param httpServletResponse the HttpServletResponse where we write our output
     * @throws TripleWriterException
     * @throws IOException
     */
    private final void writeResults(final RDFUnitConfiguration configuration, final Model model, HttpServletResponse httpServletResponse) throws TripleWriterException, IOException {
        SerializationFormat serializationFormat = configuration.geFirstOutputFormat();

        httpServletResponse.setContentType(serializationFormat.getHeaderType());
        RDFWriter RDFWriter = RDFWriterFactory.createWriterFromFormat(httpServletResponse.getOutputStream(), serializationFormat, configuration.getTestCaseExecutionType());
        RDFWriter.write(model);
    }

    /**
     * Creates an RDFUnitConfiguration based on the GET/POST parameters
     *
     * @param httpServletRequest the HttpServletRequest
     * @return an initialized RDFUnitConfiguration object
     * @throws ParameterException
     */
    abstract protected RDFUnitConfiguration getConfiguration(HttpServletRequest httpServletRequest) throws ParameterException;

    /**
     * Creates a TestSuite based on the RDFUnitConfiguration and the Source to be tested
     *
     * @param configuration an RDFUnitConfiguration object generated with getConfiguration
     * @param dataset       The dataset to be tested, this is generated automatically from the RDFUnitConfiguration object
     * @return A TestSuite (list of test cases) for running against the tested Source
     */
    abstract protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final Source dataset);

    /**
     * Executes the validation of a Sourse dataset against a TestSuite based on a RDFUnitConfiguration
     *
     * @param configuration an RDFUnitConfiguration object generated with getConfiguration
     * @param dataset       The dataset to be tested, this is generated automatically from the RDFUnitConfiguration object
     * @param testSuite     a TestSuite generated with getTestSuite()
     * @return a Model that contains the results of a validation
     * @throws TestCaseExecutionException
     */
    abstract protected Model validate(final RDFUnitConfiguration configuration, final Source dataset, final TestSuite testSuite) throws TestCaseExecutionException;

    /**
     * Prints a help message with the correct arguments one can use to call the service
     *
     * @param httpServletResponse
     * @throws IOException
     */
    abstract protected void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException;

    /**
     * Help function that writes a string to the output surrounded with <pre> </pre>
     *
     * @param httpServletResponse
     * @param message             the message we want to write
     * @throws IOException
     */
    protected void printMessage(HttpServletResponse httpServletResponse, String message) throws IOException {
        // Set response content type
        httpServletResponse.setContentType("text/html");
        // Actual logic goes here.
        PrintWriter out = httpServletResponse.getWriter();
        out.println("<pre>" + message + "</pre>");
        //out.close();
    }


}
