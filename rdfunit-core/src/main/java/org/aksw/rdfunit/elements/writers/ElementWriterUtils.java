package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.elements.interfaces.Element;

/**
 * Element Writer utils
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 11:06 PM
 */
final class ElementWriterUtils {
    private ElementWriterUtils() {}

    public static Resource copyElementResourceInModel(Element element, Model model) {
        if (element.getResource().isPresent() && !element.getResource().get().isAnon()) {
            return model.createResource(element.getResource().get().getURI());
        } else {
            return model.createResource();
        }
    }
}
