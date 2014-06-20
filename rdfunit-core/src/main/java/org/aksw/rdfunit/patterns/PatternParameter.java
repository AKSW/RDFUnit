package org.aksw.rdfunit.patterns;

import org.aksw.rdfunit.enums.PatternParameterConstraints;

/**
 * @author Dimitris Kontokostas
 *         Defines a patter parameter
 * @since 9/20/13 2:47 PM
 */
public class PatternParameter {
    private final String uri;
    private final String id;
    private final PatternParameterConstraints constrain;
    private final String constraintPattern;

    public PatternParameter(String uri, String id, PatternParameterConstraints constrain, String constraintPattern) {
        this.uri = uri;
        this.id = id;
        this.constrain = constrain;
        this.constraintPattern = constraintPattern;
    }

    public String getUri() {
        return uri;
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
