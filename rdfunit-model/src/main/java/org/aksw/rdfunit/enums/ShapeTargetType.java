package org.aksw.rdfunit.enums;

public enum ShapeTargetType {

  ClassTarget(true),
  NodeTarget(true),
  SubjectsOfTarget(true),
  ObjectsOfTarget(true),

  ValueShapeTarget(true);

  private final boolean hasArgument;

  ShapeTargetType(boolean hasArgument) {
    this.hasArgument = hasArgument;
  }

  public boolean hasArgument() {
    return hasArgument;
  }

}
