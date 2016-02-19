package org.aksw.rdfunit.enums;

public enum ShapeScopeType {

    ClassScope(true),
    NodeScope(true),
    PropertyScope(true),
    InversePropertyScope(true),
    AllSubjectsScope(false),
    AllObjectsScope(false),

    ValueShapeScope(true)
    ;

    private final boolean hasArgument;
    public boolean hasArgument() {return  hasArgument;}

    ShapeScopeType(boolean hasArgument) {
        this.hasArgument = hasArgument;
    }

}
