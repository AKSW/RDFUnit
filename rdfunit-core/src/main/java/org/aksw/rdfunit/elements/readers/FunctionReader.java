package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.elements.implementations.FunctionImpl;
import org.aksw.rdfunit.elements.interfaces.Function;
import org.aksw.rdfunit.vocabulary.SHACL;

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
     * @return a {@link org.aksw.rdfunit.elements.readers.FunctionReader} object.
     */
    public static FunctionReader create() { return new FunctionReader();}


    /** {@inheritDoc} */
    @Override
    public Function read(Resource resource) {
        checkNotNull(resource);

        FunctionImpl.Builder functonBuilder = new FunctionImpl.Builder();

        //comment
        for (Statement smt : resource.listProperties(RDFS.comment).toList()) {
            functonBuilder.setComment(smt.getObject().toString());
        }

        //cachable
        for (Statement smt : resource.listProperties(SHACL.cachable).toList()) {
            functonBuilder.setCachable(smt.getObject().asLiteral().getBoolean());
        }

        //sparql string
        for (Statement smt : resource.listProperties(SHACL.sparql).toList()) {
            functonBuilder.setSPARQLString(smt.getObject().asLiteral().toString());
        }

        //sparql string
        for (Statement smt : resource.listProperties(SHACL.returnType).toList()) {
            functonBuilder.setSPARQLString(smt.getObject().asLiteral().toString());
        }

        // arguments
        for (Statement smt : resource.listProperties(SHACL.argument).toList()) {
            ArgumentReader argumentReader = ArgumentReader.create();
            functonBuilder.addArguments(argumentReader.read(smt.getObject().asResource()));
        }

        return functonBuilder.build();
    }
}
