package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixService;

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

    public String getUri() {
        // TODO make prefix configurable
        return PrefixService.getPrefix("tddo") + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    public static TestAppliesTo resolve(String value) {

        String s = value.replace(PrefixService.getPrefix("tddo"), "");
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
