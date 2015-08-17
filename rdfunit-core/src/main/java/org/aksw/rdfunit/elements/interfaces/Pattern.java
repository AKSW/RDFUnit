package org.aksw.rdfunit.elements.interfaces;

import org.aksw.rdfunit.elements.implementations.ResultAnnotationImpl;
import org.aksw.rdfunit.tests.Binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Defines an RDFUnitL Pattern
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:14 PM
 * @version $Id: $Id
 */
public final class Pattern {
    private final String iri;
    private final String id;
    private final String description;
    private final String sparqlWherePattern;
    private final String sparqlPatternPrevalence;
    private final Collection<PatternParameter> parameters;
    private final Collection<ResultAnnotation> annotations;

    /**
     * <p>Constructor for Pattern.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     * @param sparqlWherePattern a {@link java.lang.String} object.
     * @param sparqlPatternPrevalence a {@link java.lang.String} object.
     * @param parameters a {@link java.util.Collection} object.
     * @param annotations a {@link java.util.Collection} object.
     */
    public Pattern(String iri, String id, String description, String sparqlWherePattern, String sparqlPatternPrevalence, Collection<PatternParameter> parameters, Collection<ResultAnnotation> annotations) {
        assert iri != null;
        this.iri = iri;

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

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        if (getParameters() == null || getParameters().isEmpty()) {
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
     *
     * @param bindings a {@link java.util.Collection} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<ResultAnnotation> getBindedAnnotations(Collection<Binding> bindings) {
        Collection<ResultAnnotation> finalAnnotations = new ArrayList<>();

        for (ResultAnnotation externalAnnotation : annotations) {
            ResultAnnotation sanitizedAnnotation = externalAnnotation;
            if (externalAnnotation.getAnnotationValue().isPresent() && externalAnnotation.getAnnotationValue().get().isLiteral()) {
                String value = externalAnnotation.getAnnotationValue().get().toString();
                for (Binding binding : bindings) {
                    if (value.equals("%%" + binding.getParameterId() + "%%")) {
                        sanitizedAnnotation = new ResultAnnotationImpl.Builder(externalAnnotation.getAnnotationProperty()).setValueRDFUnit(binding.getValue()).build();
                    }
                }
            }
            finalAnnotations.add(sanitizedAnnotation);
        }
        return finalAnnotations;
    }

    /**
     * <p>Getter for the field <code>iri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getIRI() {
        return iri;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Getter for the field <code>sparqlWherePattern</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSparqlWherePattern() {
        return sparqlWherePattern;
    }

    /**
     * <p>Getter for the field <code>sparqlPatternPrevalence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
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

    /** {@inheritDoc} */
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
        return sparqlWherePattern.equals(pattern.sparqlWherePattern);

    }

    /** {@inheritDoc} */
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
