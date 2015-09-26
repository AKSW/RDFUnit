package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM
 */
public class TestGeneratorBatchReader {

    private TestGeneratorBatchReader(){}

    public static TestGeneratorBatchReader create() { return new TestGeneratorBatchReader();}

    public Collection<TestGenerator> getTestGeneratorsFromModel(Model model) {
        return getTestGeneratorsFromResourceList(
                model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList()
        );
    }

    public Collection<TestGenerator> getTestGeneratorsFromResourceList(List<Resource> resources) {
        Collection<TestGenerator> testGenerators = new ArrayList<>();
        for (Resource resource: resources) {
            testGenerators.add(TestGeneratorReader.create().read(resource));
        }

        return testGenerators;
    }
}
