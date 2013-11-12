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
    private final Pattern pattern;

    public PatternParameter(String uri, String id, PatternParameterConstraints constrain, Pattern pattern) {
        URI = uri;
        this.id = id;
        this.constrain = constrain;
        this.pattern = pattern;
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

    public Pattern getPattern() {
        return pattern;
    }
}
