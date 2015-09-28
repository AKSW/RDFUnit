package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM
 */
public class TestCaseBatchReader {

    private TestCaseBatchReader(){}

    public static TestCaseBatchReader create() { return new TestCaseBatchReader();}

    public Collection<TestCase> getTestCasesFromModel(Model model) {
        Collection<TestCase> testCases = new ArrayList<>();

        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.ManualTestCase).toList()){
            testCases.add(ManualTestCaseReader.create().read(resource));
        }

        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.PatternBasedTestCase).toList()){
            testCases.add(PatternBasedTestCaseReader.create().read(resource));
        }

        return testCases;

    }

}
