package org.aksw.rdfunit.elements.implementations;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.aksw.rdfunit.elements.interfaces.ResultAnnotation;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/15/15 4:37 PM
 */

public final class ResultAnnotationImpl implements ResultAnnotation {

    private final Resource element;
    private final Property property;
    private final RDFNode value;
    private final String variableName;

    private ResultAnnotationImpl(Builder builder) {
        this.element = builder.element;
        this.property = builder.property;
        this.value = builder.value;
        this.variableName = builder.variableName;
    }

    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(element);
    }

    @Override
    public Property getAnnotationProperty() {
        return property;
    }

    @Override
    public Optional<RDFNode> getAnnotationValue() {
        return Optional.fromNullable(value);
    }

    @Override
    public Optional<String> getAnnotationVarName() {
        return Optional.fromNullable(variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(property, value, variableName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ResultAnnotationImpl other = (ResultAnnotationImpl) obj;
        return Objects.equal(this.property, other.property)
                && Objects.equal(this.value, other.value)
                && Objects.equal(this.variableName, other.variableName);
    }

    public static class Builder {
        private final Resource element;
        private final Property property;
        private RDFNode value = null;
        private String variableName = null;

        public Builder(Resource element, Property property) {

            assert property != null;

            this.element = element;
            this.property = property;
        }

        public Builder(Property property) {
            this(null, property);
        }

        public Builder(String propertyIri) {
            this(null, ResourceFactory.createProperty(propertyIri));
        }

        public Builder setValue(RDFNode value) {
            this.value = value;
            return this;
        }

        public Builder setVariableName(String variableName) {
            this.variableName = variableName;
            return this;
        }

        public Builder setValueRDFUnit(RDFNode value) {
            if (value.toString().startsWith("?")) {
                this.variableName = value.toString().substring(1);
            }
            else {
                this.value = value;
            }
            return this;
        }

        public ResultAnnotationImpl build() {
            return new ResultAnnotationImpl(this);
        }
    }
}
