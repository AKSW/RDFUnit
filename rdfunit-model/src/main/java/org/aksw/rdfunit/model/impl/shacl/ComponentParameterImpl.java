package org.aksw.rdfunit.model.impl.shacl;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:04 PM
 */
@ToString
@EqualsAndHashCode(exclude = {"element"})
@Builder
public final class ComponentParameterImpl implements ComponentParameter {

  @Getter
  @NonNull
  private final Resource element;
  @Getter
  @NonNull
  private final Property predicate;
  @Getter
  private final boolean isOptional;
  private final RDFNode defaultValue;


  @Override
  public Optional<RDFNode> getDefaultValue() {
    return Optional.ofNullable(defaultValue);
  }


}
