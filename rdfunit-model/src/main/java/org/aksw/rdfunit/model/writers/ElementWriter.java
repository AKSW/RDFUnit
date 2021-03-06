package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.Element;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

/**
 * Interface for writing elements back to RDF
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:55 PM
 */
public interface ElementWriter {

  static Resource copyElementResourceInModel(Element element, Model model) {
    if (!element.getElement().isAnon()) {
      return model.createResource(element.getElement().getURI());
    } else {
      return model.createResource();
    }
  }

  Resource write(Model model);
}
