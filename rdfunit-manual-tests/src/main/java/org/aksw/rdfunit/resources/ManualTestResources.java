package org.aksw.rdfunit.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all available test resource URIs
 * These are written manually ATM
 *
 * @author Dimitris Kontokostas
 * @since 8/21/14 12:14 PM
 * @version $Id: $Id
 */
public final class ManualTestResources {

    private ManualTestResources() {}

    private static class ResourcesInstance {

        private static final Map<String, String> resources = create();

        private ResourcesInstance() {}

        private static Map<String, String> create() {
            Map<String, String> r = new HashMap<>();
            r.put("dbpedia.org", "http://dbpedia.org");
            r.put("dbo", "http://dbpedia.org/ontology/");
            r.put("lemon", "http://lemon-model.net/lemon#");
            r.put("nif", "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#");
            r.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
            r.put("skos", "http://www.w3.org/2004/02/skos/core#");
            r.put("rr", "http://www.w3.org/ns/r2rml#");
            r.put("dataid", "http://dataid.dbpedia.org/ns/core#");


            return Collections.unmodifiableMap(r);
        }
    }


    /**
     * Returns a Collection of all URIs that have tests defined.
     *
     * @return a Collection of all URIs that have tests defined.
     */
    public static Map<String, String> getInstance() {
        return ResourcesInstance.resources;
    }
}
