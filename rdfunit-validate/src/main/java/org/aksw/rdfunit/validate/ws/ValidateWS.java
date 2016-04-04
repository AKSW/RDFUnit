package org.aksw.rdfunit.validate.ws;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.executors.monitors.SimpleTestExecutorMonitor;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;
import org.aksw.rdfunit.utils.RDFUnitUtils;
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
import java.util.Collection;

/**
 * <p>ValidateWS class.</p>
 *
 * @author Dimitris Kontokostas
 *         Validation as a web service
 * @since 6/13/14 1:50 PM
 * @version $Id: $Id
 */
public class ValidateWS extends AbstractRDFUnitWebService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidateWS.class);

    // TODO: pass dataFolder in configuration initialization
    private Collection<TestGenerator> autogenerators;

    /** {@inheritDoc} */
    @Override
    public void init() throws ServletException {
        RDFUnitUtils.fillSchemaServiceFromLOV();
        RDFUnitUtils.fillSchemaServiceFromFile(ValidateWS.class.getResourceAsStream("/org/aksw/rdfunit/configuration/schemaDecl.csv"));
        try {
            RDFUnit rdfunit = new RDFUnit();
            rdfunit.init();
            autogenerators = rdfunit.getAutoGenerators();
        } catch (RdfReaderException e) {
            LOGGER.error("Cannot read patterns and/or pattern generators", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected TestSuite getTestSuite(final RDFUnitConfiguration configuration, final TestSource dataset) {
        synchronized(this) {
            TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor(
                    configuration.isAutoTestsEnabled(),
                    configuration.isTestCacheEnabled(),
                    configuration.isManualTestsEnabled());
            return testGeneratorExecutor.generateTestSuite(configuration.getTestFolder(), dataset, autogenerators);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected TestExecution validate(final RDFUnitConfiguration configuration, final TestSource dataset, final TestSuite testSuite) throws TestCaseExecutionException {
        final TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(configuration.getTestCaseExecutionType());
        if (testExecutor == null) {
            throw new TestCaseExecutionException(null, "Cannot initialize test executor. Exiting");
        }
        final SimpleTestExecutorMonitor testExecutorMonitor = new SimpleTestExecutorMonitor();
        testExecutor.addTestExecutorMonitor(testExecutorMonitor);

        // warning, caches intermediate results
        testExecutor.execute(dataset, testSuite);

        return testExecutorMonitor.getTestExecution();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
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


    /** {@inheritDoc} */
    @Override
    public void destroy() {
        // do nothing.
    }
}
