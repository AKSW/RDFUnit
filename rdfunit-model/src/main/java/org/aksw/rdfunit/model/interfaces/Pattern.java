package org.aksw.rdfunit.model.interfaces;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Defines an RDFUnitL Pattern
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:14 PM

 */
public interface Pattern extends Element{

    /*
    * Checks if all given arguments exist in the patters and the opposite
    *
    private boolean validateArguments() {
        //TODO implement this method
        return true;
    } */


    /**
     * Goes through all external annotations and if it finds a literal value with %%XX%%
     * it replaces it with the binding value
     *
     * @param bindings a {@link java.util.Collection} object.
     * @return a {@link java.util.Collection} object.
     */
    Set<ResultAnnotation> getBindedAnnotations(Collection<Binding> bindings);

    String getIRI();

    String getId();

    String getDescription();

    String getSparqlWherePattern();

    Optional<String> getSparqlPatternPrevalence();

    Collection<PatternParameter> getParameters();

    Optional<PatternParameter> getParameter(String parameterURI);

    Collection<ResultAnnotation> getResultAnnotations();


}
