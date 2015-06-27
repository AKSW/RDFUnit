package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.elements.ValueKind;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 */
public final class ArgumentWriter implements ElementWriter {

    private final Argument argument;

    private ArgumentWriter(Argument argument) {
        this.argument = argument;
    }

    public static ArgumentWriter createArgumentWriter(Argument argument) {return new ArgumentWriter(argument);}

    @Override
    public Resource write() {
        Resource resource;

        // keep the original resource if exists
        resource = argument.getResource().isPresent() ? argument.getResource().get() : ResourceFactory.createResource();

        // rdf:type sh:Argument
        resource.addProperty(RDF.type, SHACL.Argument);

        // sh:predicate sh:argX
        resource.addProperty(SHACL.predicate, argument.getPredicate()) ;

        // rdfs:comment
        if (!argument.getComment().isEmpty())
            resource.addProperty(RDFS.comment, argument.getComment());

        // default value
        if (argument.getDefaultValue().isPresent()) {
            resource.addProperty(SHACL.defaultValue, argument.getDefaultValue().get()) ;
        }

        // get  valueType / Datatype
        if (argument.getValueType().isPresent() && argument.getValueKind().isPresent()) {
            Property property = (argument.getValueKind().get().equals(ValueKind.DATATYPE)) ? SHACL.datatype : SHACL.valueType ;
            resource.addProperty(property, argument.getValueType().get()) ;
        }

        //Optional
        if (argument.isOptional()) {
            resource.addProperty(SHACL.optional, ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean)) ;
        }
        return resource;
    }
}
