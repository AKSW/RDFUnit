package org.aksw.rdfunit.elements.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.List;

/**
 * Interface to a function according to SHACL
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:10 PM
 */
public interface Function extends Element {

    String getComment();
    Optional<Resource> getReturnType();
    boolean isCachable();

    List<Argument> getArguments();
    String getSparqlString();

}
