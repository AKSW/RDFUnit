package org.aksw.rdfunit.utils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.readers.PatternReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Util functions that instantiate patterns from a QEF
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/23/13 11:09 AM
 * @version $Id: $Id
 */
public final class PatternUtils {

    private PatternUtils() {
    }


    /**
     * Takes a QEF and tries to instantiate all defined patterns in that QEF
     *
     * @param model the Model
     * @return the collection
     */
    public static Collection<Pattern> instantiatePatternsFromModel(Model model) {

        Collection<Pattern> patterns = new ArrayList<>();
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.Pattern).toList()) {
            patterns.add(PatternReader.create().read(resource));
        }

        return patterns;
    }

}
