package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.ShaclCCParameterImpl;
import org.aksw.rdfunit.model.interfaces.ShaclCCParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class ShaclCCParameterReader implements ElementReader<ShaclCCParameter> {

    private ShaclCCParameterReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link ShaclCCParameterReader} object.
     */
    public static ShaclCCParameterReader create() { return new ShaclCCParameterReader();}

    /** {@inheritDoc} */
    @Override
    public ShaclCCParameter read(Resource resource) {
        checkNotNull(resource);

        ShaclCCParameterImpl.ShaclCCParameterImplBuilder argumentBuilder = ShaclCCParameterImpl.builder();

        argumentBuilder.element(resource);

        // get predicate
        for (Statement smt : resource.listProperties(SHACL.predicate).toList()) {
            argumentBuilder = argumentBuilder.predicate(ResourceFactory.createProperty(smt.getObject().asResource().getURI()));
        }

        checkNotNull(argumentBuilder);

        //default value
        for (Statement smt : resource.listProperties(SHACL.defaultValue).toList()) {
            argumentBuilder.defaultValue(smt.getObject());
        }

        // get optional
        for (Statement smt : resource.listProperties(SHACL.optional).toList()) {
            argumentBuilder.isOptional(smt.getObject().asLiteral().getBoolean());
        }

        return argumentBuilder.build();
    }
}
