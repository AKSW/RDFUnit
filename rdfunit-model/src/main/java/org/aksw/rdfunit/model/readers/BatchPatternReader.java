package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.interfaces.Pattern;
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

 */
public final class BatchPatternReader {

    private BatchPatternReader(){}

    public static BatchPatternReader create() { return new BatchPatternReader();}

    public Collection<Pattern> getPatternsFromModel(Model model) {
        return getPatternsFromResourceList(
                model.listResourcesWithProperty(RDF.type, RDFUNITv.Pattern).toList()
        );
    }

    public Collection<Pattern> getPatternsFromResourceList(List<Resource> resources) {
        return resources.stream()
                .map(resource -> PatternReader.create().read(resource))
                .collect(Collectors.toList());

    }
}
