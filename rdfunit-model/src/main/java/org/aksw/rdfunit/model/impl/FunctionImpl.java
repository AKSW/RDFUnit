package org.aksw.rdfunit.model.impl;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.model.interfaces.Function;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:48 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode
public final class FunctionImpl implements Function {
    private final Resource element;
    private final String comment;
    private final boolean isCachable;
    private final Function superFunction;
    private final List<Argument> arguments;
    private final String sparqlString;
    private final Resource returnType;

    private FunctionImpl(FunctionImpl.Builder builder) {
        this.element = builder.element;
        this.comment = builder.comment;
        this.isCachable = builder.isCachable;
        this.superFunction = builder.superFunction;
        this.arguments = builder.arguments;
        this.sparqlString = builder.sparqlString;
        this.returnType = builder.returnType;
    }

    /** {@inheritDoc} */
    @Override
    public String getComment() {
        return comment;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Resource> getReturnType() {
        return Optional.ofNullable(returnType);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCachable() {
        return isCachable;
    }

    /** {@inheritDoc} */
    @Override
    public List<Argument> getArguments() {
        return arguments;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparqlString() {
        return sparqlString;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }

    public static class Builder {
        private final Resource element;
        private String comment = "";
        private boolean isCachable = false;
        private Function superFunction = null;
        private List<Argument> arguments = new ArrayList<>();
        private String sparqlString = "";
        private Resource returnType = null;

        public Builder(Resource element) {
            this.element = element;
        }

        public Builder() {
            this(null);
        }

        public Builder setComment(String val) {
            comment = val;
            return this;
        }

        public Builder setCachable(boolean val) {
            isCachable = val;
            return this;
        }

        public Builder setSuperFunction(Function val) {
            superFunction = val;
            return this;
        }

        public Builder setArguments(List<Argument> val) {
            arguments = val;
            return this;
        }

        public Builder addArguments(Argument val) {
            arguments.add(val);
            return this;
        }

        public Builder setSPARQLString(String val) {
            sparqlString = val;
            return this;
        }

        public Builder setReturnTyp(Resource val) {
            returnType = val;
            return this;
        }

        public  FunctionImpl build() {
            return new FunctionImpl(this);
        }
    }

}
