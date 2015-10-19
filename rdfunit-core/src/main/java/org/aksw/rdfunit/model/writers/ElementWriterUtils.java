package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.Element;

/**
 * Element Writer utils
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 11:06 PM
 */
final class ElementWriterUtils {
    private ElementWriterUtils() {}

    /**
     * <p>copyElementResourceInModel.</p>
     *
     * @param element a {@link org.aksw.rdfunit.model.interfaces.Element} object.
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    public static Resource copyElementResourceInModel(Element element, Model model) {
        if (!element.getResource().isAnon()) {
            return model.createResource(element.getResource().getURI());
        } else {
            return model.createResource();
        }
    }
}
