package org.aksw.rdfunit.model.interfaces.shacl;

import org.aksw.rdfunit.model.interfaces.Element;

/**
 * @author Dimitris Kontokostas
 * @since 7/23/17
 */
public interface PrefixDeclaration extends Element{
    String getPrefix();
    String getNamespace();
    default String asSparqlPrefix() {
        return "PREFIX " + getPrefix() + ": <" + getNamespace() + ">";
    }
}
