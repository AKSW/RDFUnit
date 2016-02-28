package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM
 * @version $Id: $Id
 */
public final class BatchTestGeneratorReader {

    private BatchTestGeneratorReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link BatchTestGeneratorReader} object.
     */
    public static BatchTestGeneratorReader create() { return new BatchTestGeneratorReader();}

    /**
     * <p>getTestGeneratorsFromModel.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
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
        return resources.stream()
                .map(resource -> TestGeneratorReader.create().read(resource))
                .collect(Collectors.toList());

    }
}
