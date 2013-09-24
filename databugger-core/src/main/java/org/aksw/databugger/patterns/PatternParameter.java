package org.aksw.databugger.patterns;

/**
 * User: Dimitris Kontokostas
 * Defines a patter parameter
 * Created: 9/20/13 2:47 PM
 */
public class PatternParameter {
    public final String id;
/*
    public final boolean onlyClass;
    public final boolean onlyProperty;
    public final boolean onlyOperator;
    // resource? / freeVariable?
    public final String allowedValues; // i.e. < > + = ...
  */
    public PatternParameter(String id /*, boolean onlyClass, boolean onlyProperty, boolean onlyOperator, String allowedValues*/) {
        this.id = id;
        /*
        this.onlyClass = onlyClass;
        this.onlyProperty = onlyProperty;
        this.onlyOperator = onlyOperator;
        this.allowedValues = allowedValues;
        */
    }
}
