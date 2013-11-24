package org.aksw.databugger.ui;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.aksw.databugger.Databugger;
import org.aksw.databugger.Utils.DatabuggerUtils;
import org.aksw.databugger.sources.Source;
import org.aksw.databugger.tests.TestExecutor;
import org.aksw.databugger.tests.TestGeneratorExecutor;
import org.aksw.databugger.tests.UnitTest;
import org.aksw.databugger.tripleReaders.TripleReader;
import org.aksw.databugger.tripleReaders.TripleReaderFactory;

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

    private final static Databugger databugger = new Databugger();
    private final static String baseDir = _getBaseDir();
    private final static TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
    private final static TestExecutor testExecutor = new TestExecutor();;
    private final static List<UnitTest> tests = new ArrayList<UnitTest>();

    public DatabuggerUISession(VaadinService service) {
        super(service);
    }

    public static void init() {
        DatabuggerUtils.fillSchemaServiceFromLOV();
        DatabuggerUtils.fillSchemaServiceFromFile(DatabuggerUISession.getBaseDir() + "schemaDecl.csv");
    }

    private static String _getBaseDir() {
        File f = VaadinSession.getCurrent().getService().getBaseDirectory();
        return f.getAbsolutePath()+"/data/";
    }

    public static void initDatabugger() {
        try {
            DatabuggerUtils.fillPrefixService(getBaseDir() + "prefixes.ttl");

                TripleReader patternReader = TripleReaderFactory.createTripleFileReader(baseDir + "patterns.ttl");
                TripleReader testGeneratorReader = TripleReaderFactory.createTripleFileReader(baseDir+"testGenerators.ttl");
                databugger.initPatternsAndGenerators(patternReader, testGeneratorReader);
            } catch (Exception e) {
                //TODO
        }
    }

    public static Databugger getDatabugger(){
        return databugger;
    }

    public static List<UnitTest> generateTests(Source source){
        try {
            TestGeneratorExecutor testGeneratorExecutor = new TestGeneratorExecutor();
            return testGeneratorExecutor.generateTests(getBaseDir()+"tests/", source,databugger.getAutoGenerators());
        } catch (Exception e) {
            return new ArrayList<UnitTest>();
        }
    }

    public static String getBaseDir(){
        return baseDir;
    }

    public static TestGeneratorExecutor getTestGeneratorExecutor() {
        return testGeneratorExecutor;
    }

    public static TestExecutor getTestExecutor() {
        return testExecutor;
    }

    public static List<UnitTest> getTests() {
        return tests;
    }
}
