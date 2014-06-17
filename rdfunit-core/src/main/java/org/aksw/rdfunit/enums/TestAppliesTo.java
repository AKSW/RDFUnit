package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * User: Dimitris Kontokostas
 * Describes where a test can apply to
 * Created: 9/25/13 9:06 AM
 */
public enum TestAppliesTo {
    /**
     * Schema: When the tests applies to an ontology / vocabulary
     */
    Schema,

    /**
     * EnrichedSchema: When the tests applies to an ontology / vocabulary enriched with
     * external (semi-)automatic approaches
     */
    EnrichedSchema,

    /**
     * Dataset: When the tests applies to a dataset only (i.e. a SPARQL Endpoint)
     */
    Dataset,

    /**
     * Application: When the tests are specific to an application only
     */
    Application;

    /**
     * Holder the prefix to resolve this enum
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
    public static TestAppliesTo resolve(String value) {

        String s = value.replace(PrefixNSService.getNSFromPrefix(schemaPrefix), "");
        if (s.equals("Schema")) {
            return Schema;
        } else if (s.equals("EnrichedSchema")) {
            return EnrichedSchema;
        } else if (s.equals("Dataset")) {
            return Dataset;
        } else if (s.equals("Application")) {
            return Application;
        }

        return null;
    }
}
