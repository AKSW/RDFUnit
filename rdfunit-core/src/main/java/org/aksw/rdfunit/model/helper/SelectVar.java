package org.aksw.rdfunit.model.helper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds a select variable mapping
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 4:39 PM
 * @version $Id: $Id
 */
public final class SelectVar {
    private final String name;
    private final String label;

    private SelectVar(String name, String label) {
        this.name = checkNotNull(name);
        this.label = checkNotNull(label);
    }

    /**
     * <p>create.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.model.helper.SelectVar} object.
     */
    public static SelectVar create(String name) {
        return new SelectVar(name, name);
    }

    /**
     * <p>create.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.model.helper.SelectVar} object.
     */
    public static SelectVar create(String name, String label) {
        return new SelectVar(name, label);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (isLabeled()) {
            return " ( ?" + name + " AS ?" +label + " ) ";
        } else {
            return   " ?" + name + " ";
        }
    }

    private boolean isLabeled() {
        return !label.equals(name);
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Getter for the field <code>label</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return label;
    }
}
