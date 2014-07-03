package org.aksw.rdfunit.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.RDFReader;
import org.aksw.rdfunit.tests.TestCase;
import org.aksw.rdfunit.tests.TestSuite;
import org.aksw.rdfunit.tests.executors.TestExecutor;
import org.aksw.rdfunit.tests.executors.TestExecutorFactory;
import org.aksw.rdfunit.tests.generators.TestGeneratorExecutor;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Dimitris Kontokostas
 *         Keeps user session variables
 *         TODO refactor
 * @since 11/15/13 9:52 AM
 */
public class RDFUnitUISession extends VaadinSession {


    public RDFUnitUISession(VaadinService service) {
        super(service);
    }

    public static void init() {

        RDFUnit rdfunit = new RDFUnit();
        VaadinSession.getCurrent().setAttribute(RDFUnit.class, rdfunit);

        String baseDir = _getBaseDir();
        VaadinSession.getCurrent().setAttribute(String.class, baseDir);

        TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
        VaadinSession.getCurrent().setAttribute(TestGeneratorExecutor.class, testGeneratorExecutor);

        TestExecutor testExecutor = TestExecutorFactory.createTestExecutor(TestCaseExecutionType.aggregatedTestCaseResult);
        VaadinSession.getCurrent().setAttribute(TestExecutor.class, testExecutor);

        TestSuite testSuite = new TestSuite(new ArrayList<TestCase>());
        VaadinSession.getCurrent().setAttribute(TestSuite.class, testSuite);

        //Fill the service schema
        RDFUnitUtils.fillSchemaServiceFromLOV();
        RDFUnitUtils.fillSchemaServiceFromFile(getBaseDir() + "schemaDecl.csv");
    }

    private static String _getBaseDir() {
        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
        return f.getAbsolutePath() + "/data/";
    }

    public static void initRDFUnit() {
        try {
            RDFReader patternReader = RDFUnitUtils.getPatternsFromResource();
            RDFReader testGeneratorReader = RDFUnitUtils.getAutoGeneratorsFromResource();
            getRDFUnit().initPatternsAndGenerators(patternReader, testGeneratorReader);
        } catch (Exception e) {
            //TODO
        }
    }

    public static RDFUnit getRDFUnit() {
        return VaadinSession.getCurrent().getAttribute(RDFUnit.class);
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

    public static void setRDFUnitConfiguration(RDFUnitConfiguration configuration) {
        VaadinSession.getCurrent().setAttribute(RDFUnitConfiguration.class, configuration);
    }

    public static RDFUnitConfiguration getRDFUnitConfiguration() {
        return VaadinSession.getCurrent().getAttribute(RDFUnitConfiguration.class);
    }
}
