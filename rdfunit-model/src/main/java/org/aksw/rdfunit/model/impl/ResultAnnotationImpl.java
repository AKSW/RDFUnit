package org.aksw.rdfunit.model.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/15/15 4:37 PM
 */
@ToString(exclude = "element")
@EqualsAndHashCode(exclude = "element")
public final class ResultAnnotationImpl implements ResultAnnotation {

  private final Resource element;
  private final Property property;
  private final RDFNode value;
  private final String variableName;

  private ResultAnnotationImpl(Builder builder) {
    this.element = builder.element;
    this.property = builder.property;
    this.value = builder.value;
    this.variableName = builder.variableName;
  }


  @Override
  public Resource getElement() {
    return element;
  }


  @Override
  public Property getAnnotationProperty() {
    return property;
  }


  @Override
  public Optional<RDFNode> getAnnotationValue() {
    return Optional.ofNullable(value);
  }


  @Override
  public Optional<String> getAnnotationVarName() {
    return Optional.ofNullable(variableName);
  }

  public static class Builder {

    private final Resource element;
    private final Property property;
    private RDFNode value = null;
    private String variableName = null;

    public Builder(Resource element, Property property) {

      this.element = checkNotNull(element);
      this.property = checkNotNull(property);
    }

    public Builder(String elementUri, String propertyIri) {
      this(ResourceFactory.createResource(elementUri), ResourceFactory.createProperty(propertyIri));
    }

    public Builder setValue(RDFNode value) {
      this.value = value;
      return this;
    }

    public Builder setVariableName(String variableName) {
      this.variableName = variableName;
      return this;
    }

    public Builder setValueRDFUnit(RDFNode value) {
      if (value.toString().startsWith("?")) {
        this.variableName = value.toString().substring(1);
      } else {
        this.value = value;
      }
      return this;
    }

    public ResultAnnotationImpl build() {
      return new ResultAnnotationImpl(this);
    }
  }
}
