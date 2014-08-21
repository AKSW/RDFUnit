package org.aksw.rdfunit.resources;

import java.util.*;

/**
 * Holds all available test resource URIs
 * These are written manually ATM
 *
 * @author Dimitris Kontokostas
 * @since 8/21/14 12:14 PM
 */
public class Resources {
    private static class ResourcesInstance {
        private static final Collection<String> resources = create();

        private static Collection<String> create() {
            Collection<String> r = Arrays.asList(
                    "http://dbpedia.org",
                    "http://dbpedia.org/ontology/",
                    "http://lemon-model.net/lemon#",
                    "http://linkedgeodata.org/ontology/",
                    "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#",
                    "http://www.w3.org/2003/01/geo/wgs84_pos#",
                    "http://www.w3.org/2004/02/skos/core#"
            );

            return Collections.unmodifiableCollection(r);
        }
    }


    /**
     * Returns a Collection of all URIs that have tests defined.
     *
     * @return a Collection of all URIs that have tests defined.
     */
    public Collection<String> getInstance() {
        return ResourcesInstance.resources;
    }
}
