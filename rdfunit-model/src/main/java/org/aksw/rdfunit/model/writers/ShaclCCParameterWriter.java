package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.ShaclCCParameter;
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
public final class ShaclCCParameterWriter implements ElementWriter {

    private final ShaclCCParameter shaclCCParameter;

    private ShaclCCParameterWriter(ShaclCCParameter shaclCCParameter) {
        this.shaclCCParameter = shaclCCParameter;
    }

    /**
     * <p>create.</p>
     *
     * @param shaclCCParameter a {@link ShaclCCParameter} object.
     * @return a {@link ShaclCCParameterWriter} object.
     */
    public static ShaclCCParameterWriter create(ShaclCCParameter shaclCCParameter) {return new ShaclCCParameterWriter(shaclCCParameter);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(shaclCCParameter, model);

        // rdf:type sh:ShaclCCParameter
        resource.addProperty(RDF.type, SHACL.Argument);

        // sh:predicate sh:argX
        resource.addProperty(SHACL.predicate, shaclCCParameter.getPredicate()) ;

        //Optional
        if (shaclCCParameter.isOptional()) {
            resource.addProperty(SHACL.optional, ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean)) ;
        }
        return resource;
    }
}
