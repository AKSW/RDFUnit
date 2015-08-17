package org.aksw.rdfunit.elements.implementations;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.elements.interfaces.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 3:48 PM
 */
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

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public Optional<Resource> getReturnType() {
        return Optional.fromNullable(returnType);
    }

    @Override
    public boolean isCachable() {
        return isCachable;
    }

    @Override
    public List<Argument> getArguments() {
        return arguments;
    }

    @Override
    public String getSparqlString() {
        return sparqlString;
    }

    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(element);
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

        public Builder setSupoerFunction(Function val) {
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

    @Override
    public int hashCode() {
        return Objects.hashCode(comment, isCachable, superFunction, arguments, sparqlString, returnType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final FunctionImpl other = (FunctionImpl) obj;
        return Objects.equal(this.comment, other.comment)
                && Objects.equal(this.isCachable, other.isCachable)
                && Objects.equal(this.superFunction, other.superFunction)
                && Objects.equal(this.arguments, other.arguments)
                && Objects.equal(this.sparqlString, other.sparqlString)
                && Objects.equal(this.returnType, other.returnType);
    }
}
