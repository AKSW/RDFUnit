package org.aksw.rdfunit.elements.implementations;

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
    private final String comment;
    private final boolean isCachable;
    private final Function superFunction;
    private final List<Argument> arguments;
    private final String sparqlString;
    private final Resource returnType;

    private FunctionImpl(FunctionImpl.Builder builder) {
        comment = builder.comment;
        isCachable = builder.isCachable;
        superFunction = builder.superFunction;
        arguments = builder.arguments;
        sparqlString = builder.sparqlString;
        returnType = builder.returnType;
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
        return null;
    }

    public static class Builder {
        private String comment = "";
        private boolean isCachable = false;
        private Function superFunction = null;
        private List<Argument> arguments = new ArrayList<>();
        private String sparqlString = "";
        private Resource returnType = null;

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
}
