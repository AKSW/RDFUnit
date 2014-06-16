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

    public String getUri() {
        // TODO make prefix configurable
        return PrefixNSService.getNSFromPrefix("rut") + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    public static PatternParameterConstraints resolve(String value) {

        String s = value.replace(PrefixNSService.getNSFromPrefix("rut"), "");
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
