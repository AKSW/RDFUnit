package org.aksw.databugger.patterns;

import org.aksw.databugger.enums.PatternParameterConstraints;

/**
 * User: Dimitris Kontokostas
 * Defines a patter parameter
 * Created: 9/20/13 2:47 PM
 */
public class PatternParameter {
    private final String URI;
    private final String id;
    private final PatternParameterConstraints constrain;
    private final String constraintPattern;

    public PatternParameter(String uri, String id, PatternParameterConstraints constrain, String constraintPattern) {
        URI = uri;
        this.id = id;
        this.constrain = constrain;
        this.constraintPattern = constraintPattern;
    }

    public String getURI() {
        return URI;
    }

    public String getId() {
        return id;
    }

    public PatternParameterConstraints getConstrain() {
        return constrain;
    }

    public String getConstraintPattern() {
        return constraintPattern;
    }
}
