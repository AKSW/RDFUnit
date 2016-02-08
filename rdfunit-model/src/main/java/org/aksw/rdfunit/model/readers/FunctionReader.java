package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.FunctionImpl;
import org.aksw.rdfunit.model.interfaces.Function;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDFS;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class FunctionReader implements ElementReader<Function> {

    private FunctionReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.FunctionReader} object.
     */
    public static FunctionReader create() { return new FunctionReader();}


    /** {@inheritDoc} */
    @Override
    public Function read(Resource resource) {
        checkNotNull(resource);

        FunctionImpl.Builder functionBuilder = new FunctionImpl.Builder();

        //comment
        for (Statement smt : resource.listProperties(RDFS.comment).toList()) {
            functionBuilder.setComment(smt.getObject().asLiteral().getLexicalForm());
        }

        //cachable
        for (Statement smt : resource.listProperties(SHACL.cachable).toList()) {
            functionBuilder.setCachable(smt.getObject().asLiteral().getBoolean());
        }

        //sparql string
        for (Statement smt : resource.listProperties(SHACL.sparql).toList()) {
            functionBuilder.setSPARQLString(smt.getObject().asLiteral().getLexicalForm());
        }

        //sparql string
        for (Statement smt : resource.listProperties(SHACL.returnType).toList()) {
            functionBuilder.setSPARQLString(smt.getObject().asLiteral().getLexicalForm());
        }

        // arguments
        for (Statement smt : resource.listProperties(SHACL.argument).toList()) {
            ArgumentReader argumentReader = ArgumentReader.create();
            functionBuilder.addArguments(argumentReader.read(smt.getObject().asResource()));
        }

        return functionBuilder.build();
    }
}
