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

    private static Databugger databugger = null;
    private static String baseDir = null;
    private static TestGeneratorExecutor testGeneratorExecutor = null;
    private static TestExecutor testExecutor = null;


    public DatabuggerUISession(VaadinService service) {
        super(service);
    }

    public static String getBaseDir() {
        if (baseDir == null) {
            File f = VaadinSession.getCurrent().getService().getBaseDirectory();
            baseDir = f.getAbsolutePath()+"/data/";
        }
        return baseDir;
    }

    public static Databugger getDatabugger(){

        if (databugger == null) {

            try {


                DatabuggerUtils.fillPrefixService(getBaseDir() + "prefixes.ttl");

                TripleReader patternReader = TripleReaderFactory.createTripleFileReader(baseDir + "patterns.ttl");
                TripleReader testGeneratorReader = TripleReaderFactory.createTripleFileReader(baseDir+"testGenerators.ttl");
                databugger = new Databugger(patternReader, testGeneratorReader);
            } catch (Exception e) {
                //TODO
                return null;
            }
        }
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

    public static TestGeneratorExecutor getTestGeneratorExecutor() {
        if (testGeneratorExecutor == null)
            testGeneratorExecutor = new TestGeneratorExecutor();
        return testGeneratorExecutor;
    }

    public static TestExecutor getTestExecutor() {
        if (testExecutor == null)
            testExecutor = new TestExecutor();
        return testExecutor;
    }
}
