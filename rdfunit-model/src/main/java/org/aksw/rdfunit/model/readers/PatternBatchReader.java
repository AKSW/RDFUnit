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
 * @version $Id: $Id
 */
public final class PatternBatchReader {

    private PatternBatchReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternBatchReader} object.
     */
    public static PatternBatchReader create() { return new PatternBatchReader();}

    /**
     * <p>getPatternsFromModel.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Pattern> getPatternsFromModel(Model model) {
        return getPatternsFromResourceList(
                model.listResourcesWithProperty(RDF.type, RDFUNITv.Pattern).toList()
        );
    }

    /**
     * <p>getPatternsFromResourceList.</p>
     *
     * @param resources a {@link java.util.List} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Pattern> getPatternsFromResourceList(List<Resource> resources) {
        Collection<Pattern> patterns = new ArrayList<>();
        for (Resource resource: resources) {
            patterns.add(PatternReader.create().read(resource));
        }

        return patterns;
    }
}
