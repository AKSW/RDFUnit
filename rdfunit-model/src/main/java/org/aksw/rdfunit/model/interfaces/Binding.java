package org.aksw.rdfunit.model.interfaces;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.PatternParameterConstraints;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

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
@ToString
@EqualsAndHashCode(exclude = "element")
public class Binding implements Element {
    private final Resource element;
    private final PatternParameter parameter;
    private final RDFNode value;


    /**
     * <p>Constructor for Binding.</p>
     *
     * @param parameter a {@link org.aksw.rdfunit.model.interfaces.PatternParameter} object.
     * @param value a {@link org.apache.jena.rdf.model.RDFNode} object.
     */
    public Binding(PatternParameter parameter, RDFNode value) {
        this(ResourceFactory.createResource(), parameter, value);
    }
    /**
     * <p>Constructor for Binding.</p>
     *
     * @param element a {@link org.apache.jena.rdf.model.Resource} object.
     * @param parameter a {@link org.aksw.rdfunit.model.interfaces.PatternParameter} object.
     * @param value a {@link org.apache.jena.rdf.model.RDFNode} object.
     */
    public Binding(Resource element, PatternParameter parameter, RDFNode value) {
        this.element = checkNotNull(element, "Element must not be null");
        this.parameter =checkNotNull(parameter, "parameter must not be null in Binding");
        this.value = checkNotNull(value, "value must not be null in Binding");

        //Validate biding
        if (!validateType()) {
            //throw new BindingException("Binding is of incorrect constraint type");
        }
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
     * @return a {@link org.apache.jena.rdf.model.RDFNode} object.
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
        if (value.isResource() &&
                (pc.equals(PatternParameterConstraints.Resource) ||
                pc.equals(PatternParameterConstraints.Property) ||
                pc.equals(PatternParameterConstraints.Class))) {
            return true;
        }
        return value.isLiteral() && pc.equals(PatternParameterConstraints.Operator);
    }

    /** {@inheritDoc} */
    @Override
    public Resource getElement() {
        return element;
    }
}
