package org.aksw.databugger.enums;

/**
 * User: Dimitris Kontokostas
 * Describes where a test can apply to
 * Created: 9/25/13 9:06 AM
 */
public enum TestAppliesTo {
    /**
    *  Schema: When the tests applies to an ontology / vocabulary
    * */
    Schema,

    /**
     *  EnrichedSchema: When the tests applies to an ontology / vocabulary enriched with
     *  external (semi-)automatic approaches
     * */
    EnrichedSchema,

    /**
     *  Dataset: When the tests applies to a dataset only (i.e. a SPARQL Endpoint)
     * */
    Dataset,

    /**
     *  Application: When the tests are specific to an application only
     * */
    Application;

    public String getUri() {
        // TODO make prefix configurable
        return "http://databugger.aksw.org/ontology/" + name();
    }

    @Override
    public String toString() {
        return getUri();
    }
}
