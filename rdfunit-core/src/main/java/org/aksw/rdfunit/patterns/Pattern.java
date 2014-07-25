package org.aksw.rdfunit.patterns;

import org.aksw.rdfunit.tests.Binding;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;
import java.util.Collection;

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
    private final Collection<PatternParameter> parameters;
    private final Collection<ResultAnnotation> annotations;

    public Pattern(String id, String description, String sparqlWherePattern, String sparqlPatternPrevalence, Collection<PatternParameter> parameters, Collection<ResultAnnotation> annotations) {
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


    /**
     * Goes through all external annotations and if it finds a literal value with %%XX%%
     * it replaces it with the binding value
     */
    public Collection<ResultAnnotation> getBindedAnnotations(Collection<Binding> bindings) {
        Collection<ResultAnnotation> finalAnnotations = new ArrayList<>();

        for (ResultAnnotation externalAnnotation: annotations) {
            ResultAnnotation sanitizedAnnotation = externalAnnotation;
            if (externalAnnotation.getAnnotationValue().isLiteral()) {
                String value = externalAnnotation.getAnnotationValue().toString();
                for (Binding binding: bindings) {
                    if (value.equals("%%" + binding.getParameterId() + "%%")) {
                        sanitizedAnnotation = new ResultAnnotation(externalAnnotation.getAnnotationProperty(), binding.getValue());
                    }
                }
            }
            finalAnnotations.add(sanitizedAnnotation);
        }
        return finalAnnotations;
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

    public Collection<PatternParameter> getParameters() {
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

}
