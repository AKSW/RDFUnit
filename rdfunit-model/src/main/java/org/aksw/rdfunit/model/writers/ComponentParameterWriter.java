package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.ComponentParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

/**
 *
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class ComponentParameterWriter implements ElementWriter {

    private final ComponentParameter componentParameter;

    private ComponentParameterWriter(ComponentParameter componentParameter) {
        this.componentParameter = componentParameter;
    }

    /**
     * <p>create.</p>
     *
     * @param componentParameter a {@link ComponentParameter} object.
     * @return a {@link ComponentParameterWriter} object.
     */
    public static ComponentParameterWriter create(ComponentParameter componentParameter) {return new ComponentParameterWriter(componentParameter);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(componentParameter, model);

        // rdf:type sh:ComponentParameter
        resource.addProperty(RDF.type, SHACL.Parameter);

        // sh:path sh:argX
        resource.addProperty(SHACL.path, componentParameter.getPredicate()) ;

        //Optional
        if (componentParameter.isOptional()) {
            resource.addProperty(SHACL.optional, ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean)) ;
        }
        return resource;
    }
}
