package org.aksw.rdfunit.model.interfaces;

import static lombok.Lombok.checkNotNull;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.enums.PatternParameterConstraints;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Holds a parameter binding between a pattern parameter and a test instance TODO: make this an
 * interface and move to Impl
 *
 * @author Dimitris Kontokostas
 * @since 9/30/13 8:28 AM
 */
@ToString
@EqualsAndHashCode(exclude = "element")
public class Binding implements Element {

  private final Resource element;
  private final PatternParameter parameter;
  private final RDFNode value;


  public Binding(PatternParameter parameter, RDFNode value) {
    this(ResourceFactory.createResource(), parameter, value);
  }

  public Binding(Resource element, PatternParameter parameter, RDFNode value) {
    this.element = checkNotNull(element, "Element must not be null");
    this.parameter = checkNotNull(parameter, "parameter must not be null in Binding");
    this.value = checkNotNull(value, "value must not be null in Binding");

    //Validate biding
    if (!validateType()) {
      //throw new BindingException("Binding is of incorrect constraint type");
    }
  }

  public String getValueAsString() {
    if (value.isResource()) {
      // some vocabularies use spaces in uris
      return "<" + value.toString().trim().replace(" ", "") + ">";

    } else {
      return value.asLiteral().getLexicalForm();
    }
  }

  public RDFNode getValue() {
    return value;
  }

  public String getParameterId() {
    return parameter.getId();
  }

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

  @Override
  public Resource getElement() {
    return element;
  }
}
