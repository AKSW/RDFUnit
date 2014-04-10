package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixService;

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
        return PrefixService.getPrefix("rut") + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    public static PatternParameterConstraints resolve(String value) {

        String s = value.replace(PrefixService.getPrefix("rut"), "");
        if (s.equals("Resource")) {
            return Resource;
        } else if (s.equals("Property")) {
            return Property;
        } else if (s.equals("Class")) {
            return Class;
        } else if (s.equals("Operator")) {
            return Operator;
        }

        return None;
    }
}
