package org.aksw.rdfunit.patterns;

import org.aksw.rdfunit.tests.Binding;
import org.aksw.rdfunit.tests.results.ResultAnnotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Defines an RDFUnitL Pattern
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:14 PM
 */
public final class Pattern {
    private final String id;
    private final String description;
    private final String sparqlWherePattern;
    private final String sparqlPatternPrevalence;
    private final Collection<PatternParameter> parameters;
    private final Collection<ResultAnnotation> annotations;

    public Pattern(String id, String description, String sparqlWherePattern, String sparqlPatternPrevalence, Collection<PatternParameter> parameters, Collection<ResultAnnotation> annotations) {

        assert id != null;
        this.id = id;

        assert description != null;
        this.description = description;

        assert sparqlWherePattern != null;
        this.sparqlWherePattern = sparqlWherePattern;

        assert sparqlPatternPrevalence != null;
        this.sparqlPatternPrevalence = sparqlPatternPrevalence;

        assert parameters != null;
        this.parameters = parameters;

        assert annotations != null;
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
    *
    private boolean validateArguments() {
        //TODO implement this method
        return true;
    } */


    /**
     * Goes through all external annotations and if it finds a literal value with %%XX%%
     * it replaces it with the binding value
     */
    public Collection<ResultAnnotation> getBindedAnnotations(Collection<Binding> bindings) {
        Collection<ResultAnnotation> finalAnnotations = new ArrayList<>();

        for (ResultAnnotation externalAnnotation : annotations) {
            ResultAnnotation sanitizedAnnotation = externalAnnotation;
            if (externalAnnotation.getAnnotationValue().isLiteral()) {
                String value = externalAnnotation.getAnnotationValue().toString();
                for (Binding binding : bindings) {
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

    /**
     * Returns the Pattern Parameters as an immutable Collection
     *
     * @return the pattern parameters as an Collections.unmodifiableCollection()
     */
    public Collection<PatternParameter> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    /**
     * Returns a parameter object from a parameter URI
     *
     * @param parameterURI the parameter uRI
     * @return the parameter object or null if it does not exists
     */
    public PatternParameter getParameter(String parameterURI) {
        for (PatternParameter parameter : parameters) {
            if (parameter.getUri().equals(parameterURI)) {
                return parameter;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pattern)) return false;

        Pattern pattern = (Pattern) o;

        if (!annotations.equals(pattern.annotations)) return false;
        if (!description.equals(pattern.description)) return false;
        if (!id.equals(pattern.id)) return false;
        if (!parameters.equals(pattern.parameters)) return false;
        if (!sparqlPatternPrevalence.equals(pattern.sparqlPatternPrevalence)) return false;
        if (!sparqlWherePattern.equals(pattern.sparqlWherePattern)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + sparqlWherePattern.hashCode();
        result = 31 * result + sparqlPatternPrevalence.hashCode();
        result = 31 * result + parameters.hashCode();
        result = 31 * result + annotations.hashCode();
        return result;
    }
}
