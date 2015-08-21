package org.aksw.rdfunit.elements.implementations;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.enums.ValueKind;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:04 PM
 */
public final class ArgumentImpl implements Argument {

    private final Resource element;
    private final Resource predicate;
    private final String comment;
    private final Resource valueType;
    private final ValueKind valueKind;
    private final RDFNode defaultValue;
    private final boolean isOptional;

    private ArgumentImpl(Builder builder) {
        this.element = checkNotNull(builder.element);
        this.predicate = checkNotNull(builder.predicate);
        this.comment = checkNotNull(builder.comment);
        this.valueType = builder.valueType;
        this.valueKind = builder.valueKind;
        this.defaultValue = builder.defaultValue;
        this.isOptional = builder.isOptional;
    }

    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(element);
    }

    @Override
    public String getComment() {
        return comment;
    }


    @Override
    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public RDFNode getPredicate() {
        return predicate;
    }

    @Override
    public Optional<Resource> getValueType() {
        return Optional.fromNullable(valueType);
    }

    @Override
    public Optional<ValueKind> getValueKind() {
        return Optional.fromNullable(valueKind);
    }

    @Override
    public Optional<RDFNode> getDefaultValue() {
        return Optional.fromNullable(defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(element, predicate, comment, valueType, valueKind, defaultValue, isOptional);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ArgumentImpl other = (ArgumentImpl) obj;
        return Objects.equal(this.element, other.element)
                && Objects.equal(this.predicate, other.predicate)
                && Objects.equal(this.comment, other.comment)
                && Objects.equal(this.valueType, other.valueType)
                && Objects.equal(this.valueKind, other.valueKind)
                && Objects.equal(this.defaultValue, other.defaultValue)
                && Objects.equal(this.isOptional, other.isOptional);
    }

    public static class Builder {
        private Resource element;
        private Resource predicate;
        private String comment = "";
        private Resource valueType = null;
        private ValueKind valueKind = null;
        private RDFNode defaultValue = null;
        private boolean isOptional = false;


        public Builder(Resource element) {
            this.element = checkNotNull(element);
        }

        public Builder setPredicate(Resource predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder setComment(String val) {
            this.comment = val;
            return this;
        }

        public Builder setOptional(boolean val) {
            this.isOptional = val;
            return this;
        }

        public Builder setValueType(Resource val1, ValueKind val2) {
            this.valueType = val1;
            this.valueKind = val2;
            return this;
        }

        public Builder setDefaultValue(RDFNode val) {
            this.defaultValue = val;
            return this;
        }

        public ArgumentImpl build() {
            return new ArgumentImpl(this);
        }

    }

}
