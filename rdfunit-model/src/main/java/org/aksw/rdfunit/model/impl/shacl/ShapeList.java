package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.rdf.model.Resource;

@Builder
@Value
@EqualsAndHashCode()
public class ShapeList implements Shape {

  @Getter
  @NonNull
  private final ImmutableSet<Shape> shapes;

  @Getter
  @NonNull
  private final Resource element;
  private final ShapePath shaclPath;
  @Getter
  @NonNull
  private final PropertyValuePairSet propertyValuePairSets;

  @Override
  public Optional<ShapePath> getPath() {
    return Optional.ofNullable(shaclPath);
  }
}
