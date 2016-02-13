package org.aksw.rdfunit.model.impl;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines an RDFUnitL Pattern
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:14 PM
 * @version $Id: $Id
 */
@ToString (exclude = {"sparqlWherePattern", "sparqlPatternPrevalence"})
@EqualsAndHashCode
public final class PatternImpl implements Pattern {
    private final Resource element;
    private final String id;
    private final String description;
    private final String sparqlWherePattern;
    private final String sparqlPatternPrevalence;
    private final ImmutableList<PatternParameter> parameters;
    private final ImmutableList<ResultAnnotation> annotations;

    private PatternImpl(Builder builder) {

        this.element = checkNotNull(builder.element);
        String name = element.getURI();
        this.id = checkNotNull(builder.id, "ID is required in Pattern %s", name);
        this.description = checkNotNull(builder.description, "Description is required in Pattern %s", name);
        this.sparqlWherePattern = checkNotNull(builder.sparqlWherePattern, "SPARQL Where pattern is required in Pattern %s", name);
        this.sparqlPatternPrevalence = builder.sparqlPatternPrevalence;
        this.parameters = ImmutableList.copyOf(checkNotNull(builder.parameters));
        checkArgument(!parameters.isEmpty(), "Pattern %s must have at least one parameter", name);
        this.annotations = ImmutableList.copyOf(checkNotNull(builder.annotations));

        //check if defined parameters exist is sparql
        for (PatternParameter p : getParameters()) {
            boolean exists = sparqlWherePattern.contains("%%" + p.getId() + "%%");

            checkArgument(exists, "In Pattern %s parameter %s does not exist in query", name, p.getId());
        }
    }


    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ResultAnnotation> getBindedAnnotations(Collection<Binding> bindings) {
        Collection<ResultAnnotation> finalAnnotations = new ArrayList<>();

        for (ResultAnnotation externalAnnotation : annotations) {
            ResultAnnotation sanitizedAnnotation = externalAnnotation;
            if (externalAnnotation.getAnnotationValue().isPresent() && externalAnnotation.getAnnotationValue().get().isLiteral()) {
                String value = externalAnnotation.getAnnotationValue().get().toString();
                for (Binding binding : bindings) {
                    if (value.equals("%%" + binding.getParameterId() + "%%")) {
                        sanitizedAnnotation = new ResultAnnotationImpl.Builder(externalAnnotation.getElement(), externalAnnotation.getAnnotationProperty()).setValueRDFUnit(binding.getValue()).build();
                    }
                }
            }
            finalAnnotations.add(sanitizedAnnotation);
        }
        return finalAnnotations;
    }

    /** {@inheritDoc} */
    @Override
    public String getIRI() {
        return element.getURI();
    }

    /** {@inheritDoc} */
    @Override
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlWherePattern() {
        return sparqlWherePattern;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getSparqlPatternPrevalence() {
        return Optional.ofNullable(sparqlPatternPrevalence);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<PatternParameter> getParameters() {
        return Collections.unmodifiableCollection(parameters);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<PatternParameter> getParameter(String parameterURI) {
        for (PatternParameter parameter : parameters) {
            if (parameter.getUri().equals(parameterURI)) {
                return Optional.of(parameter);
            }
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ResultAnnotation> getResultAnnotations() {
        return annotations;
    }

    public static class Builder{
        private Resource element;
        private String id;
        private String description;
        private String sparqlWherePattern;
        private String sparqlPatternPrevalence;
        private Collection<PatternParameter> parameters;
        private Collection<ResultAnnotation> annotations;

        public Builder setElement(Resource element) {
            this.element = element;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setSparqlWherePattern(String sparqlWherePattern) {
            this.sparqlWherePattern = sparqlWherePattern;
            return this;
        }

        public Builder setSparqlPatternPrevalence(String sparqlPatternPrevalence) {
            if (sparqlPatternPrevalence != null && !sparqlPatternPrevalence.trim().isEmpty()) {
                this.sparqlPatternPrevalence = sparqlPatternPrevalence;
            }
            return this;
        }

        public Builder setParameters(Collection<PatternParameter> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder setAnnotations(Collection<ResultAnnotation> annotations) {
            this.annotations = annotations;
            return this;
        }

        public Pattern build() {return new PatternImpl(this);}
    }

}
