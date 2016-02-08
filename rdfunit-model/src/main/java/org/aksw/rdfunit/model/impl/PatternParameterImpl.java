package org.aksw.rdfunit.model.impl;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.PatternParameterConstraints;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines a Pattern Parameter.
 * contains all necessary fields for storing the parameter data.
 *
 * @author Dimitris Kontokostas
 * @since 9 /20/13 2:47 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode
public final class PatternParameterImpl implements PatternParameter {

    private final Resource element;
    private final String id;
    private final PatternParameterConstraints constraint;
    private final String constraintPattern;


    private PatternParameterImpl(Builder builder) {

        this.element = checkNotNull(builder.element, "Parameter element is required and not null");
        String name = this.element.getURI();
        this.id = checkNotNull(builder.id, "ID is required for PatternParameter %s", name);
        this.constraint = checkNotNull(builder.constraint,"Constraint type is required for PatternParameter %s", name);
        this.constraintPattern = builder.constraintPattern;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }

    /** {@inheritDoc} */
    @Override
    public String getUri() {
        return element.getURI();
    }

    /** {@inheritDoc} */
    @Override
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public PatternParameterConstraints getConstraint() {
        return constraint;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getConstraintPattern() {
        return Optional.ofNullable(constraintPattern);
    }

    public static class Builder {
        private Resource element = null;
        private String id = null;
        private PatternParameterConstraints constraint = PatternParameterConstraints.None; // default value
        private String constraintPattern = null;

        public Builder setElement(Resource element) {
            this.element = element;
            return this;
        }

        public Builder setID(String id) {
            this.id = id;
            return this;
        }

        public Builder setPatternParameterConstraints(PatternParameterConstraints constraint) {
            this.constraint = constraint;
            return this;
        }

        public Builder setPatternParameterConstraints(String constraintStr) {
            this.constraint = PatternParameterConstraints.resolve(constraintStr);
            return this;
        }

        public Builder setConstraintPattern(String constraintPattern) {
            this.constraintPattern = constraintPattern;
            return this;
        }

        public PatternParameter build() {
            return new PatternParameterImpl(this);
        }
    }
}
