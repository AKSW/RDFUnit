package org.aksw.rdfunit.patterns;

import org.aksw.rdfunit.enums.PatternParameterConstraints;

/**
 * Defines a Pattern Parameter.
 * contains all necessary fields for storing the parameter data.
 *
 * @author Dimitris Kontokostas
 * @since 9 /20/13 2:47 PM
 */
public final class PatternParameter {
    private final String uri;
    private final String id;
    private final PatternParameterConstraints constrain;
    private final String constraintPattern;

    /**
     * Instantiates a new Pattern parameter.
     *
     * @param uri               the uri of the parameter
     * @param id                the parameter id
     * @param constrain         the constrain
     * @param constraintPattern the constraint pattern (if exists)
     */
    public PatternParameter(String uri, String id, PatternParameterConstraints constrain, String constraintPattern) {

        assert uri != null;
        this.uri = uri;

        assert id != null;
        this.id = id;

        assert constrain != null;
        this.constrain = constrain;

        assert constraintPattern != null;
        this.constraintPattern = constraintPattern;
    }

    /**
     * Gets uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets constrain.
     *
     * @return the constrain
     */
    public PatternParameterConstraints getConstrain() {
        return constrain;
    }

    /**
     * Gets constraint pattern.
     *
     * @return the constraint pattern
     */
    public String getConstraintPattern() {
        return constraintPattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatternParameter)) return false;

        PatternParameter that = (PatternParameter) o;

        if (constrain != that.constrain) return false;
        if (!constraintPattern.equals(that.constraintPattern)) return false;
        if (!id.equals(that.id)) return false;
        if (!uri.equals(that.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + constrain.hashCode();
        result = 31 * result + constraintPattern.hashCode();
        return result;
    }
}
