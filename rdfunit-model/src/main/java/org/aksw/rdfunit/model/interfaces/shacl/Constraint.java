package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Map;

public interface Constraint {
    Shape getShape();
    Resource getSeverity();

    /**
     * default message for constraint, if there are multiple picks one, if there are none, provides one
     */
    String getMessage();

    /**
     * message for a specific language, if there are multiple picks one
     */
    // Optional<String> getMessageForLang(String langTag);

    /**
     * return the constraint component associated with this constraint
     */
    Component getComponent();

    ComponentValidator getValidator();

    Map<ComponentParameter, RDFNode> getBindings();

    TestCase getTestCase();

    /**
     * return parent constraint, if nested
     */
    // Optional<Constraint> getParentConstraint();
}
