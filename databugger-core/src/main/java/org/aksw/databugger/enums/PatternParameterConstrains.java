package org.aksw.databugger.enums;

/**
 * User: Dimitris Kontokostas
 * Enumerates the different parameter constrains
 * Created: 9/25/13 10:35 AM
 */
public enum PatternParameterConstrains {

    /**
     * The parameter can a resource
     * */
    Resource,

    /**
     * The parameter is restricted to a property
     * */
    Property,

    /**
     * The parameter is restricted to a Class
     * */
    Class,

    /**
     * The parameter can be an operator
     * */
    Operator,

    /**
     * The parameter can be anything, even a free variable (?var)
     * */
    Any;

    private static String uri = "http://databugger.aksw.org/ontology/";


    public String getUri() {
        // TODO make prefix configurable
        return uri + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    public static PatternParameterConstrains resolve(String value) {

        String s = value.replace(uri, "").toString();
        if (s.equals("Resource")) {
            return Resource;
        } else if (s.equals("Property")) {
            return Property;
        } else if (s.equals("Class")) {
            return Class;
        } else if (s.equals("Operator")) {
            return Operator;
        } else if (s.equals("Any"))
            return Any;

        return null;
    }
}
