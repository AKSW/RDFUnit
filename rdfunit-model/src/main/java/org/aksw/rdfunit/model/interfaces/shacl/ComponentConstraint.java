package org.aksw.rdfunit.model.interfaces.shacl;


import org.aksw.rdfunit.model.interfaces.TestCase;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Map;

public interface ComponentConstraint {
    Shape getShape();

    /**
     * default message for constraint, if there are multiple picks one, if there are none, provides one
     */
    Literal getMessage();

    // Optional<String> getMessageForLang(String langTag);

    /**
     * return the constraint component associated with this constraint
     */
    Component getComponent();

    ComponentValidator getValidator();

    Map<ComponentParameter, RDFNode> getBindings();

    TestCase getTestCase();

    // Optional<ComponentConstraint> getParentConstraint();
}
