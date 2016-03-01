package org.aksw.rdfunit.validate.ws;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.writer.RdfResultsWriterFactory;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.validate.ParameterException;
import org.apache.jena.rdf.model.ModelFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>Abstract RDFUnitWebService class.</p>
 *
 * @author Dimitris Kontokostas
 *         A super class that can serve as an easy validation service based on RDFUnit
 * @since 6/18/14 8:52 AM
 * @version $Id: $Id
 */
public abstract class AbstractRDFUnitWebService extends HttpServlet {


    /** {@inheritDoc} */
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    /** {@inheritDoc} */
    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    /**
     * Has all the workflow logic for getting the parameters, performing a validation and writing the output
     */
    private void handleRequestAndRespond(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
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
        assert configuration != null;

        final TestSource dataset = configuration.getTestSource();

        final TestSuite testSuite = getTestSuite(configuration, dataset);
        assert testSuite != null;

        TestExecution results = null;
        try {
            results = validate(configuration, dataset, testSuite);
        } catch (TestCaseExecutionException e) {
            // TODO catch error
        }
        assert results != null;

        try {
            writeResults(configuration, results, httpServletResponse);
        } catch (RdfWriterException e) {
            printMessage(httpServletResponse, e.getMessage());
        }
    }

    /**
     * Writes the output of the validation to the HttpServletResponse
     */
    private void writeResults(final RDFUnitConfiguration configuration, final TestExecution testExecution, HttpServletResponse httpServletResponse) throws RdfWriterException, IOException {
        SerializationFormat serializationFormat = configuration.geFirstOutputFormat();
        if (serializationFormat == null) {
            throw new RdfWriterException("Invalid output format");
        }

        httpServletResponse.setContentType(serializationFormat.getHeaderType());
        RdfWriter RdfWriter = RdfResultsWriterFactory.createWriterFromFormat(httpServletResponse.getOutputStream(), serializationFormat, testExecution);
        RdfWriter.write(ModelFactory.createDefaultModel());
    }

    /**
     * Creates an RDFUnitConfiguration based on the GET/POST parameters
     *
     * @param httpServletRequest the HttpServletRequest
     * @return an initialized RDFUnitConfiguration object
     * @throws org.aksw.rdfunit.validate.ParameterException if any.
     */
    protected abstract RDFUnitConfiguration getConfiguration(HttpServletRequest httpServletRequest) throws ParameterException;

    /**
     * Creates a TestSuite based on the RDFUnitConfiguration and the Source to be tested
     *
     * @param configuration an RDFUnitConfiguration object generated with getConfiguration
     * @param testSource       The dataset to be tested, this is generated automatically from the RDFUnitConfiguration object
     * @return A TestSuite (list of test cases) for running against the tested Source
     */
    protected abstract TestSuite getTestSuite(final RDFUnitConfiguration configuration, final TestSource testSource);

    /**
     * Executes the validation of a Sourse dataset against a TestSuite based on a RDFUnitConfiguration
     *
     * @param configuration an RDFUnitConfiguration object generated with getConfiguration
     * @param testSource       The dataset to be tested, this is generated automatically from the RDFUnitConfiguration object
     * @param testSuite     a TestSuite generated with getTestSuite()
     * @return a Model that contains the results of a validation
     * @throws org.aksw.rdfunit.exceptions.TestCaseExecutionException if any.
     */
    protected abstract TestExecution validate(final RDFUnitConfiguration configuration, final TestSource testSource, final TestSuite testSuite) throws TestCaseExecutionException;

    /**
     * Prints a help message with the correct arguments one can use to call the service
     *
     * @param httpServletResponse a {@link javax.servlet.http.HttpServletResponse} object.
     * @throws java.io.IOException if any.
     */
    protected abstract void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException;

    /**
     * Help function that writes a string to the output surrounded with {@code <pre> </pre>}
     *
     * @param httpServletResponse a {@link javax.servlet.http.HttpServletResponse} object.
     * @param message             the message we want to write
     * @throws java.io.IOException if any.
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
