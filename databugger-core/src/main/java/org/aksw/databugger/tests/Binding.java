package org.aksw.databugger.tests;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.databugger.enums.PatternParameterConstraints;
import org.aksw.databugger.exceptions.BindingException;
import org.aksw.databugger.patterns.PatternParameter;
import org.aksw.databugger.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Holds a parameter binding between a pattern parameter and a test instance
 * Created: 9/30/13 8:28 AM
 */
public class Binding {
    private final PatternParameter parameter;
    private final RDFNode value;

    public Binding(PatternParameter parameter, RDFNode value) throws BindingException {
        this.parameter = parameter;
        this.value = value;

        //Validate bibding
        if (!validateType())
            throw new BindingException("Binding is of incorrect constraint type");
    }

    public String getValue() {
        if (value.isResource()) {
            // some vocabularies use spaces in uris
            return "<" + value.toString().trim().replace(" ", "") + ">";

        } else
            return value.toString();
    }

    public String getParameterId() {
        return parameter.getId();
    }

    public Resource writeToModel(Model model) {
        return model.createResource()
                .addProperty(RDF.type, model.createResource(PrefixService.getPrefix("tddo") + "Binding"))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "parameter"), model.createResource(parameter.getURI()))
                .addProperty(ResourceFactory.createProperty(PrefixService.getPrefix("tddo"), "bindingValue"), value);

    }

    private boolean validateType() {
        PatternParameterConstraints pc = parameter.getConstrain();
        if (pc.equals(PatternParameterConstraints.None))
            return true;
        if (value.isResource()) {
            if (pc.equals(PatternParameterConstraints.Resource) ||
                    pc.equals(PatternParameterConstraints.Property) ||
                    pc.equals(PatternParameterConstraints.Class))
                return true;
        }
        if (value.isLiteral()) {
            if (pc.equals(PatternParameterConstraints.Operator)) {
                return true;
            }
        }

        // TODO check for more
        return false;
    }

    private boolean validatePattern() {
        if (parameter.getConstraintPattern().equals(""))
            return true;
        // TODO Check the pattern
        return true;
    }
}
