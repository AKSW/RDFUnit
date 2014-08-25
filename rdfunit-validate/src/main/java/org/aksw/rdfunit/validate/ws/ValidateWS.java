package org.aksw.rdfunit.validate.ws;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.exceptions.TripleReaderException;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.sources.Source;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.validate.ParameterException;
import org.aksw.rdfunit.validate.utils.ValidateUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dimitris Kontokostas
 *         Validation as a web service
 * @since 6/13/14 1:50 PM
 */
public class ValidateWS extends RDFUnitWebService {
    private static final Logger log = LoggerFactory.getLogger(ValidateWS.class);

    private final String dataFolder = "data/";
    private final String testFolder = dataFolder + "tests/";
    private final RDFReader patternReader = RDFUnitUtils.getPatternsFromResource();
    private final RDFReader testGeneratorReader = RDFUnitUtils.getAutoGeneratorsOWLFromResource();
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
    protected synchronized TestSuite getTestSuite(final RDFUnitConfiguration configuration, final Source dataset) {
        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                configuration.isAutoTestsEnabled(),
                configuration.isTestCacheEnabled(),
                configuration.isManualTestsEnabled());
        return testGeneratorExecutor.generateTestSuite(configuration.getTestFolder(), dataset, rdfunit.getAutoGenerators());
    }

    @Override
    protected Model validate(final RDFUnitConfiguration configuration, final Source dataset, final TestSuite testSuite) throws TestCaseExecutionException {
        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(configuration.getTestCaseExecutionType());
        if (testExecutor == null) {
            throw new TestCaseExecutionException(null, "Cannot initialize test executor. Exiting");
        }
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite, 0);

        return testExecutorMonitor.getModel();
    }

    @Override
    protected RDFUnitConfiguration getConfiguration(HttpServletRequest httpServletRequest) throws ParameterException {
        String[] arguments = convertArgumentsToStringArray(httpServletRequest);

        CommandLine commandLine = null;
        try {
            commandLine = ValidateUtils.parseArguments(arguments);
        } catch (ParseException e) {
            String errorMEssage = "Error! Not valid parameter input";
            throw new ParameterException(errorMEssage, e);
        }

        // TODO: hack to print help message
        if (commandLine.hasOption("h")) {
            throw new ParameterException("");
        }

        RDFUnitConfiguration configuration = null;
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        if (configuration.getOutputFormats().size() != 1) {
            throw new ParameterException("Error! Multiple formats defined");
        }

        return configuration;
    }

    protected void printHelpMessage(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setContentType("text/html");
        PrintWriter printWriter = httpServletResponse.getWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(printWriter, 200, "/validate?", "<pre>", ValidateUtils.getCliOptions(), 0, 0, "</pre>");
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


    @Override
    public void destroy() {
        // do nothing.
    }
}