package org.aksw.rdfunit.elements.interfaces;

import java.util.List;

/**
 * Interface to a function according to SHACL
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:10 PM
 */
public interface Function {

    String getComment();
    String getXSDDataType();
    boolean isCachable();

    List<Argument> getArguments();
    String getSparqlString();

}
