package org.aksw.rdfunit.validate;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.io.DataReader;
import org.aksw.rdfunit.io.DataWriter;
import org.aksw.rdfunit.io.HTMLResultsWriter;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * User: Dimitris Kontokostas
 * Validation as a web service
 * Created: 6/13/14 1:50 PM
 */
public class ValidateWS extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ValidateWS.class);

    private final String dataFolder = "data/";
    private final String testFolder = dataFolder + "tests/";
    private final DataReader patternReader = RDFUnitUtils.getPatternsFromResource();
    private final DataReader testGeneratorReader = RDFUnitUtils.getAutoGeneratorsFromResource();
    private final RDFUnit rdfunit = new RDFUnit();

    @Override
    public void init() throws ServletException {
        RDFUnitUtils.fillSchemaServiceFromLOV();
        RDFUnitUtils.fillSchemaServiceFromFile(ValidateWS.class.getResourceAsStream("/org/aksw/rdfunit/schemaDecl.csv"));
        try {
            rdfunit.initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (TripleReaderException e) {
            log.error("Cannot read patterns and/or pattern generators");
        }
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        handleRequestAndRespond(httpServletRequest, httpServletResponse);
    }

    private void handleRequestAndRespond(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        String[] arguments = convertArgumentsToStringArray(httpServletRequest);

        CommandLine commandLine = null;
        try {
            commandLine = ValidateUtils.parseArguments(arguments);
        } catch (ParseException e) {
            e.printStackTrace();
            printMessage(httpServletResponse, "Error");
            return;
        }

        if (commandLine.hasOption("h")) {
            printHelpMessage(httpServletResponse);
            httpServletResponse.getWriter().close();
            return;
        }

        boolean printHelpAndReturn = false;

        RDFUnitConfiguration configuration = null;
        try {
            configuration = ValidateUtils.getConfigurationFromArguments(commandLine);
        } catch (ParameterException e) {
            String message = e.getMessage();
            if (message != null) {
                printMessage(httpServletResponse, message);
            }
            printHelpAndReturn = true;
        }
        assert (configuration != null);


        if (commandLine.hasOption("h")) {
            printHelpAndReturn = true;
        }

        if (printHelpAndReturn) {
            printHelpMessage(httpServletResponse);
            httpServletResponse.getWriter().close();
            return;
        }


        final Source dataset = configuration.getTestSource();
        /* </cliStuff> */


        TestSuite testSuite = getTestSuite(configuration, dataset);


        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(configuration.getResultLevelReporting());
        if (testExecutor == null) {
            printMessage(httpServletResponse, "Cannot initialize test executor. Exiting");
            httpServletResponse.getWriter().close();
            return;
        }
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite, 0);

        httpServletResponse.setContentType("text/html");
        try {
            DataWriter html = HTMLResultsWriter.create(configuration.getResultLevelReporting(), httpServletResponse.getOutputStream());
            html.write(testExecutorMonitor.getModel());

        } catch (TripleWriterException e) {
            log.error("Cannot write tests to file: " + e.getMessage());
        }


    }

    @Override
    public void destroy() {
        // do nothing.
    }

    private synchronized TestSuite getTestSuite(RDFUnitConfiguration configuration, final Source dataset) {
        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(configuration.isTestCacheEnabled(), configuration.isManualTestsEnabled());
        return testGeneratorExecutor.generateTestSuite(configuration.getTestFolder(), dataset, rdfunit.getAutoGenerators());
    }

    private void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/html");
        PrintWriter printWriter = httpServletResponse.getWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(printWriter, 200, "/validate?", "<pre>", ValidateUtils.getCliOptions(), 0, 0, "</pre>");
    }

    private void printMessage(HttpServletResponse httpServletResponse, String message) throws IOException {
        // Set response content type
        httpServletResponse.setContentType("text/html");
        // Actual logic goes here.
        PrintWriter out = httpServletResponse.getWriter();
        out.println("<pre>" + message + "</pre>");
        //out.close();
    }

    private static String[] convertArgumentsToStringArray(HttpServletRequest httpServletRequest) {
        //twice the size to split key value
        String[] args = new String[httpServletRequest.getParameterMap().size() * 2];

        int x = 0;
        for (Object key : httpServletRequest.getParameterMap().keySet()) {
            String pname = (String) key;
            //transform key to CLI style
            pname = (pname.length() == 1) ? "-" + pname : "--" + pname;

            //collect CLI args
            args[x++] = pname;
            args[x++] = httpServletRequest.getParameter((String) key);
        }

        return args;
    }
}