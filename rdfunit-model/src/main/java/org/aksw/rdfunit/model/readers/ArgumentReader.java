package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.impl.ArgumentImpl;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
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
public final class ArgumentReader implements ElementReader<Argument> {

    private ArgumentReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.ArgumentReader} object.
     */
    public static ArgumentReader create() { return new ArgumentReader();}

    /** {@inheritDoc} */
    @Override
    public Argument read(Resource resource) {
        checkNotNull(resource);

        ArgumentImpl.ArgumentImplBuilder argumentBuilder = ArgumentImpl.builder();

        argumentBuilder.element(resource);

        // get predicate
        for (Statement smt : resource.listProperties(SHACL.predicate).toList()) {
            argumentBuilder = argumentBuilder.predicate(ResourceFactory.createProperty(smt.getObject().asResource().getURI()));
        }

        checkNotNull(argumentBuilder);

        //comment
        for (Statement smt : resource.listProperties(RDFS.comment).toList()) {
            argumentBuilder.comment(smt.getObject().asLiteral().getLexicalForm());
        }

        //default value
        for (Statement smt : resource.listProperties(SHACL.defaultValue).toList()) {
            argumentBuilder.defaultValue(smt.getObject());
        }

        //get datatype / valueType...
        for (Statement smt : resource.listProperties(SHACL.datatype).toList()) {
            argumentBuilder.valueType(smt.getObject().asResource());
            argumentBuilder.valueKind(ValueKind.DATATYPE);
        }
        for (Statement smt : resource.listProperties(SHACL.valueType).toList()) {
            argumentBuilder.valueType(smt.getObject().asResource());
            argumentBuilder.valueKind(ValueKind.IRI);
        }

        // get optional
        for (Statement smt : resource.listProperties(SHACL.optional).toList()) {
            argumentBuilder.isOptional(smt.getObject().asLiteral().getBoolean());
        }

        return argumentBuilder.build();
    }
}
