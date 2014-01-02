package org.aksw.databugger.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.aksw.databugger.Databugger;
import org.aksw.databugger.DatabuggerConfiguration;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.io.TripleFileReader;
import org.aksw.databugger.tests.TestCase;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.tests.TestGeneratorExecutor;
import org.aksw.databugger.io.TripleReader;
import org.aksw.databugger.io.TripleReaderFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

        TestExecutor testExecutor = new TestExecutor();
        VaadinSession.getCurrent().setAttribute(TestExecutor.class, testExecutor);

        UnitTestList testList = new UnitTestList();
        testList.tests = new ArrayList<TestCase>();
        VaadinSession.getCurrent().setAttribute(UnitTestList.class, testList);

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

    public static List<TestCase> getTests() {
        return VaadinSession.getCurrent().getAttribute(UnitTestList.class).tests;
    }

    public static void setDatabuggerConfiguration(DatabuggerConfiguration configuration) {
        VaadinSession.getCurrent().setAttribute(DatabuggerConfiguration.class, configuration);
    }

    public static DatabuggerConfiguration getDatabuggerConfiguration() {
        return VaadinSession.getCurrent().getAttribute(DatabuggerConfiguration.class);
    }

    static class UnitTestList {
        List<TestCase> tests;

        public UnitTestList() {
        }
    }
}
