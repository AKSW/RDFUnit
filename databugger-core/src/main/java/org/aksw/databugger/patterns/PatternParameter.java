package org.aksw.databugger.patterns;

import org.aksw.databugger.enums.PatternParameterConstrains;

/**
 * User: Dimitris Kontokostas
 * Defines a patter parameter
 * Created: 9/20/13 2:47 PM
 */
public class PatternParameter {
    private final String URI;
    private final String id;
    private final PatternParameterConstrains constrain;
    private final String pattern;

    public PatternParameter(String uri, String id, PatternParameterConstrains constrain, String pattern) {
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

    public PatternParameterConstrains getConstrain() {
        return constrain;
    }

    public String getPattern() {
        return pattern;
    }
}
