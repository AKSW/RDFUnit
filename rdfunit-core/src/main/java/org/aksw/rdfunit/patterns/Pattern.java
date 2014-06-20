package org.aksw.rdfunit.patterns;

import org.aksw.rdfunit.tests.results.ResultAnnotation;

/**
 * @author Dimitris Kontokostas
 *         Class that holds a sparqlWherePattern definition
 * @since 9/16/13 1:14 PM
 */
public class Pattern {
    private final String id;
    private final String description;
    private final String sparqlWherePattern;
    private final String sparqlPatternPrevalence;
    private final java.util.Collection<PatternParameter> parameters;
    private final java.util.Collection<ResultAnnotation> annotations;

    public Pattern(String id, String description, String sparqlWherePattern, String sparqlPatternPrevalence, java.util.Collection<PatternParameter> parameters, java.util.Collection<ResultAnnotation> annotations) {
        this.id = id;
        this.description = description;
        this.sparqlWherePattern = sparqlWherePattern;
        this.sparqlPatternPrevalence = sparqlPatternPrevalence;
        this.parameters = parameters;
        this.annotations = annotations;
    }

    public boolean isValid() {
        if (getParameters() == null || getParameters().size() == 0) {
            return false;
        }
        //check if defined parameters exist is sparql
        for (PatternParameter p : getParameters()) {
            if (!getSparqlWherePattern().contains("%%" + p.getId() + "%%")) {
                return false;
            }
        }
        // TODO search if we need more parameters
        return true;
    }

    /*
    * Checks if all given arguments exist in the patters and the opposite
    * */
    private boolean validateArguments() {
        //TODO implement this method
        return true;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getSparqlWherePattern() {
        return sparqlWherePattern;
    }

    public String getSparqlPatternPrevalence() {
        return sparqlPatternPrevalence;
    }

    public java.util.Collection<PatternParameter> getParameters() {
        return parameters;
    }

    public PatternParameter getParameter(String parameterURI) {
        for (PatternParameter parameter : parameters) {
            if (parameter.getUri().equals(parameterURI)) {
                return parameter;
            }
        }
        return null;
    }

    public java.util.Collection<ResultAnnotation> getAnnotations() {
        return annotations;
    }
}
