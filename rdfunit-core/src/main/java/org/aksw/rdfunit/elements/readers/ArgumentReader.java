package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.elements.implementations.ArgumentImpl;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.vocabulary.SHACL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class ArgumentReader implements ElementReader<Argument> {

    private ArgumentReader(){}

    public static ArgumentReader create() { return new ArgumentReader();}

    @Override
    public Argument read(Resource resource) {
        checkNotNull(resource);

        ArgumentImpl.Builder argumentBuilder = new ArgumentImpl.Builder(resource);

        // get predicate
        for (Statement smt : resource.listProperties(SHACL.predicate).toList()) {
            argumentBuilder = argumentBuilder.setPredicate(smt.getObject().asResource());
        }

        checkNotNull(argumentBuilder);

        //comment
        for (Statement smt : resource.listProperties(RDFS.comment).toList()) {
            argumentBuilder.setComment(smt.getObject().toString());
        }

        //default value
        for (Statement smt : resource.listProperties(SHACL.defaultValue).toList()) {
            argumentBuilder.setDefaultValue(smt.getObject());
        }

        //get datatype / valueType...
        for (Statement smt : resource.listProperties(SHACL.datatype).toList()) {
            argumentBuilder.setValueType(smt.getObject().asResource(), ValueKind.DATATYPE);
        }
        for (Statement smt : resource.listProperties(SHACL.valueType).toList()) {
            argumentBuilder.setValueType(smt.getObject().asResource(), ValueKind.IRI);
        }

        // get optional
        for (Statement smt : resource.listProperties(SHACL.optional).toList()) {
            argumentBuilder.setOptional(smt.getObject().asLiteral().getBoolean());
        }

        return argumentBuilder.build();
    }
}
