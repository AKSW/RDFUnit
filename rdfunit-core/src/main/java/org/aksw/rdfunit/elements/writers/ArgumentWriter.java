package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.vocabulary.SHACL;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class ArgumentWriter implements ElementWriter {

    private final Argument Argument;

    private ArgumentWriter(Argument argument) {
        this.Argument = argument;
    }

    /**
     * <p>createArgumentWriter.</p>
     *
     * @param argument a {@link org.aksw.rdfunit.elements.interfaces.Argument} object.
     * @return a {@link org.aksw.rdfunit.elements.writers.ArgumentWriter} object.
     */
    public static ArgumentWriter createArgumentWriter(Argument argument) {return new ArgumentWriter(argument);}

    /** {@inheritDoc} */
    @Override
    public Resource write() {
        Resource resource;

        // keep the original resource if exists
        resource = Argument.getResource().isPresent() ? Argument.getResource().get() : ResourceFactory.createResource();

        // rdf:type sh:Argument
        resource.addProperty(RDF.type, SHACL.Argument);

        // sh:predicate sh:argX
        resource.addProperty(SHACL.predicate, Argument.getPredicate()) ;

        // rdfs:comment
        if (!Argument.getComment().isEmpty())
            resource.addProperty(RDFS.comment, Argument.getComment());

        // default value
        if (Argument.getDefaultValue().isPresent()) {
            resource.addProperty(SHACL.defaultValue, Argument.getDefaultValue().get()) ;
        }

        // get  valueType / Datatype
        if (Argument.getValueType().isPresent() && Argument.getValueKind().isPresent()) {
            Property property = (Argument.getValueKind().get().equals(ValueKind.DATATYPE)) ? SHACL.datatype : SHACL.valueType ;
            resource.addProperty(property, Argument.getValueType().get()) ;
        }

        //Optional
        if (Argument.isOptional()) {
            resource.addProperty(SHACL.optional, ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean)) ;
        }
        return resource;
    }
}
