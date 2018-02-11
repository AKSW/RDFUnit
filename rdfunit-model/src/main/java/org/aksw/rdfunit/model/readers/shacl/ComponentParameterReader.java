package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.model.impl.shacl.ComponentParameterImpl;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.readers.ElementReader;
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

 */
public final class ComponentParameterReader implements ElementReader<ComponentParameter> {

    private ComponentParameterReader(){}

    public static ComponentParameterReader create() { return new ComponentParameterReader();}

   @Override
    public ComponentParameter read(Resource resource) {
        checkNotNull(resource);

        ComponentParameterImpl.ComponentParameterImplBuilder argumentBuilder = ComponentParameterImpl.builder();

        argumentBuilder.element(resource);

        // get path
        for (Statement smt : resource.listProperties(SHACL.path).toList()) {
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
