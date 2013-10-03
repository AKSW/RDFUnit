package org.aksw.databugger.tests;

/**
 * User: Dimitris Kontokostas
 * Holds a parameter binding between a pattern parameter and a test instance
 * Created: 9/30/13 8:28 AM
 */
public class Binding {
    private final String parameterURI;
    private final String value;

    public Binding(String parameterURI, String value) {
        this.parameterURI = parameterURI;
        this.value = value;
    }
}
