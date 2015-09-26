package org.aksw.rdfunit.model.helper;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds a select variable mapping
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 4:39 PM
 */
public final class SelectVar {
    private final String name;
    private final String label;

    private SelectVar(String name, String label) {
        this.name = checkNotNull(name);
        this.label = checkNotNull(label);
    }

    public static SelectVar create(String name) {
        return new SelectVar(name, name);
    }

    public static SelectVar create(String name, String label) {
        return new SelectVar(name, label);
    }

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

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}
