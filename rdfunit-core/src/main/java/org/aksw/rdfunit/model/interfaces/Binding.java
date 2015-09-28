package org.aksw.rdfunit.model.interfaces;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.PatternParameterConstraints;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <p>Binding class.</p>
 * TODO: make this an interface and move to Impl
 *
 * @author Dimitris Kontokostas
 *         Holds a parameter binding between a pattern parameter and a test instance
 * @since 9/30/13 8:28 AM
 * @version $Id: $Id
 */
public class Binding implements Element {
    private final Resource resource;
    private final PatternParameter parameter;
    private final RDFNode value;

    /**
     * <p>Constructor for Binding.</p>
     *
     * @param resource a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     * @param parameter a {@link org.aksw.rdfunit.model.interfaces.PatternParameter} object.
     * @param value a {@link com.hp.hpl.jena.rdf.model.RDFNode} object.
     * @throws org.aksw.rdfunit.exceptions.BindingException if any.
     */
    public Binding(Resource resource, PatternParameter parameter, RDFNode value) {
        this.resource = resource;
        this.parameter = checkNotNull(parameter, "parameter must not be null in Binding");
        this.value = checkNotNull(value, "value must not be null in Binding");

        //Validate bibding
        if (!validateType()) {
            //throw new BindingException("Binding is of incorrect constraint type");
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
            return value.asLiteral().getLexicalForm();
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
     * <p>Getter for the field <code>parameter</code>.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.interfaces.PatternParameter} object.
     */
    public PatternParameter getParameter() {
        return parameter;
    }

    private boolean validateType() {
        PatternParameterConstraints pc = parameter.getConstraint();
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
        if (!parameter.getConstraintPattern().isPresent()) {
            return true;
        }
        // TODO Check the pattern
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Resource> getResource() {
        return Optional.fromNullable(resource);
    }
}
