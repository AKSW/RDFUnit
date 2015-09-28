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
 * @version $Id: $Id
 */
public class TestGeneratorBatchReader {

    private TestGeneratorBatchReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.TestGeneratorBatchReader} object.
     */
    public static TestGeneratorBatchReader create() { return new TestGeneratorBatchReader();}

    /**
     * <p>getTestGeneratorsFromModel.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<TestGenerator> getTestGeneratorsFromModel(Model model) {
        return getTestGeneratorsFromResourceList(
                model.listResourcesWithProperty(RDF.type, RDFUNITv.TestGenerator).toList()
        );
    }

    /**
     * <p>getTestGeneratorsFromResourceList.</p>
     *
     * @param resources a {@link java.util.List} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<TestGenerator> getTestGeneratorsFromResourceList(List<Resource> resources) {
        Collection<TestGenerator> testGenerators = new ArrayList<>();
        for (Resource resource: resources) {
            testGenerators.add(TestGeneratorReader.create().read(resource));
        }

        return testGenerators;
    }
}
