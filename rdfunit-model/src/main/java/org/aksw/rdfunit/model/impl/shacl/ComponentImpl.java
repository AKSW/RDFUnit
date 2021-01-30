package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.apache.jena.rdf.model.Resource;

@ToString
@EqualsAndHashCode(exclude = {"element"})
@Builder
public class ComponentImpl implements Component {

  @Getter
  @NonNull
  private final Resource element;
  @Getter
  @NonNull
  @Singular
  private ImmutableList<ComponentParameter> parameters;
  @Getter
  @NonNull
  @Singular
  private ImmutableList<ComponentValidator> validators;
}
