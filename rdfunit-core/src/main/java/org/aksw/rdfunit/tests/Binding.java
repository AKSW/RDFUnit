package org.aksw.rdfunit.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.PatternParameterConstraints;
import org.aksw.rdfunit.exceptions.BindingException;
import org.aksw.rdfunit.patterns.PatternParameter;
import org.aksw.rdfunit.utils.PrefixNSService;

/**
 * <p>Binding class.</p>
 *
 * @author Dimitris Kontokostas
 *         Holds a parameter binding between a pattern parameter and a test instance
 * @since 9/30/13 8:28 AM
 * @version $Id: $Id
 */
public class Binding {
    private final PatternParameter parameter;
    private final RDFNode value;

    /**
     * <p>Constructor for Binding.</p>
     *
     * @param parameter a {@link org.aksw.rdfunit.patterns.PatternParameter} object.
     * @param value a {@link com.hp.hpl.jena.rdf.model.RDFNode} object.
     * @throws org.aksw.rdfunit.exceptions.BindingException if any.
     */
    public Binding(PatternParameter parameter, RDFNode value) throws BindingException {
        this.parameter = parameter;
        this.value = value;

        //Validate bibding
        if (!validateType()) {
            throw new BindingException("Binding is of incorrect constraint type");
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Binding [" + parameter.getId() + " => " + value + ']';
    }

    /**
     * <p>getValueAsString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValueAsString() {
        if (value.isResource()) {
            // some vocabularies use spaces in uris
            return "<" + value.toString().trim().replace(" ", "") + ">";

        } else {
            return value.toString();
        }
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link com.hp.hpl.jena.rdf.model.RDFNode} object.
     */
    public RDFNode getValue() {
        return value;
    }

    /**
     * <p>getParameterId.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getParameterId() {
        return parameter.getId();
    }

    /**
     * <p>writeToModel.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    public Resource writeToModel(Model model) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:Binding")))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:parameter")), model.createResource(parameter.getUri()))
                .addProperty(ResourceFactory.createProperty(PrefixNSService.getURIFromAbbrev("rut:bindingValue")), value);

    }

    private boolean validateType() {
        PatternParameterConstraints pc = parameter.getConstrain();
        if (pc.equals(PatternParameterConstraints.None)) {
            return true;
        }
        if (value.isResource() && pc.equals(PatternParameterConstraints.Resource) ||
                pc.equals(PatternParameterConstraints.Property) ||
                pc.equals(PatternParameterConstraints.Class)) {
            return true;
        }
        if (value.isLiteral() && pc.equals(PatternParameterConstraints.Operator)) {
            return true;
        }

        // TODO check for more
        return false;
    }

    private boolean validatePattern() {
        if (parameter.getConstraintPattern().trim().isEmpty()) {
            return true;
        }
        // TODO Check the pattern
        return true;
    }
}
