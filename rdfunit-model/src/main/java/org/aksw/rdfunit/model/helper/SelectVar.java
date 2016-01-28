package org.aksw.rdfunit.model.helper;

import lombok.*;

/**
 * Holds a select variable mapping
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 4:39 PM
 * @version $Id: $Id
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class SelectVar {
    @Getter @NonNull
    private final String name;
    @Getter @NonNull
    private final String label;

    public static SelectVar create(String name) {
        return new SelectVar(name, name);
    }

    public static SelectVar create(String name, String label) {
        return new SelectVar(name, label);
    }

    public String asString() {
        if (isLabeled()) {
            return " ( ?" + name + " AS ?" +label + " ) ";
        } else {
            return   " ?" + name + " ";
        }
    }

    private boolean isLabeled() {
        return !label.equals(name);
    }


}
