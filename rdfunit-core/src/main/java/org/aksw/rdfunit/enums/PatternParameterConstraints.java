package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * User: Dimitris Kontokostas
 * Enumerates the different parameter constrains
 * Created: 9/25/13 10:35 AM
 */
public enum PatternParameterConstraints {

    /**
     * The parameter can a resource
     */
    Resource,

    /**
     * The parameter is restricted to a property
     */
    Property,

    /**
     * The parameter is restricted to a Class
     */
    Class,

    /**
     * The parameter can be an operator
     */
    Operator,

    /**
     * The parameter can be anything, even a free variable (?var)
     */
    None;

    /**
     * Holds the prefix to resolve this enum
     */
    private static final String schemaPrefix = "rut";

    /**
     * @return a full URI/IRI as a String
     */
    public String getUri() {
        return PrefixNSService.getNSFromPrefix(schemaPrefix) + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    /**
     * Resolves a full URI/IRI to an enum
     * @param value the URI/IRI we want to resolve
     * @return the equivalent enum type or null if it cannot resolve
     */
    public static PatternParameterConstraints resolve(String value) {

        String s = value.replace(PrefixNSService.getNSFromPrefix(schemaPrefix), "");
        switch (s) {
            case "Resource":
                return Resource;
            case "Property":
                return Property;
            case "Class":
                return Class;
            case "Operator":
                return Operator;
            default:
                return None;
        }
    }
}
