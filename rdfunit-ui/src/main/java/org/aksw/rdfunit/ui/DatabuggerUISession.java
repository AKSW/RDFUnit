package org.aksw.rdfunit.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.aksw.rdfunit.Databugger;
import org.aksw.rdfunit.DatabuggerConfiguration;
import org.aksw.rdfunit.Utils.DatabuggerUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.TripleFileReader;
import org.aksw.rdfunit.io.TripleReader;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestGeneratorExecutor;

import java.io.File;
import java.util.ArrayList;

/**
 * User: Dimitris Kontokostas
 * Keeps user session variables
 * TODO refactor
 * Created: 11/15/13 9:52 AM
 */
public class DatabuggerUISession extends VaadinSession {


    public DatabuggerUISession(VaadinService service) {
        super(service);
    }

    public static void init() {

        Databugger databugger = new Databugger();
        VaadinSession.getCurrent().setAttribute(Databugger.class, databugger);

        String baseDir = _getBaseDir();
        VaadinSession.getCurrent().setAttribute(String.class, baseDir);

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
        VaadinSession.getCurrent().setAttribute(TestGeneratorExecutor.class, testGeneratorExecutor);

        TestExecutor testExecutor = TestExecutor.initExecutorFactory(TestCaseExecutionType.aggregatedTestCaseResult);
        VaadinSession.getCurrent().setAttribute(TestExecutor.class, testExecutor);

        TestSuite testSuite = new TestSuite(new ArrayList<TestCase>());
        VaadinSession.getCurrent().setAttribute(TestSuite.class, testSuite);

        //Fill the service schema
        DatabuggerUtils.fillSchemaServiceFromLOV();
        DatabuggerUtils.fillSchemaServiceFromFile(getBaseDir() + "schemaDecl.csv");
    }

    private static String _getBaseDir() {
        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
        return f.getAbsolutePath() + "/data/";
    }

    public static void initDatabugger() {
        try {
            DatabuggerUtils.fillPrefixService(getBaseDir() + "prefixes.ttl");

            TripleReader patternReader = new TripleFileReader(getBaseDir() + "patterns.ttl");
            TripleReader testGeneratorReader = new TripleFileReader(getBaseDir() + "testGenerators.ttl");
            getDatabugger().initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (Exception e) {
            //TODO
        }
    }

    public static Databugger getDatabugger() {
        return VaadinSession.getCurrent().getAttribute(Databugger.class);
    }

    public static String getBaseDir() {
        return VaadinSession.getCurrent().getAttribute(String.class);
    }

    public static TestGeneratorExecutor getTestGeneratorExecutor() {
        return VaadinSession.getCurrent().getAttribute(TestGeneratorExecutor.class);
    }

    public static TestExecutor getTestExecutor() {
        return VaadinSession.getCurrent().getAttribute(TestExecutor.class);
    }

    public static TestSuite getTestSuite() {
        return VaadinSession.getCurrent().getAttribute(TestSuite.class);
    }

    public static void setTestSuite(TestSuite testSuite) {
        VaadinSession.getCurrent().setAttribute(TestSuite.class, testSuite);
    }

    public static void setDatabuggerConfiguration(DatabuggerConfiguration configuration) {
        VaadinSession.getCurrent().setAttribute(DatabuggerConfiguration.class, configuration);
    }

    public static DatabuggerConfiguration getDatabuggerConfiguration() {
        return VaadinSession.getCurrent().getAttribute(DatabuggerConfiguration.class);
    }
}
