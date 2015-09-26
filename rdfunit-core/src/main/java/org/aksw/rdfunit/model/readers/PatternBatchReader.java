package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.Pattern;
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
public class PatternBatchReader {

    private PatternBatchReader(){}

    public static PatternBatchReader create() { return new PatternBatchReader();}

    public Collection<Pattern> getPatternsFromModel(Model model) {
        return getPatternsFromResourceList(
                model.listResourcesWithProperty(RDF.type, RDFUNITv.Pattern).toList()
        );
    }

    public Collection<Pattern> getPatternsFromResourceList(List<Resource> resources) {
        Collection<Pattern> patterns = new ArrayList<>();
        for (Resource resource: resources) {
            patterns.add(PatternReader.create().read(resource));
        }

        return patterns;
    }
}
