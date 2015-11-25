package org.aksw.rdfunit.services;

import org.aksw.rdfunit.model.interfaces.Pattern;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>PatternService class.</p>
 *
 * @author Dimitris Kontokostas
 *         class that contains instances of all idPatterns
 * @since 9/20/13 2:52 PM
 * @version $Id: $Id
 */
public final class PatternService {
    final private static Map<String, Pattern> idPatterns = new ConcurrentHashMap<>();
    final private static Map<String, Pattern> iriPatterns = new ConcurrentHashMap<>();



    private PatternService() {
    }

    /**
     * <p>addPattern.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @param iri a {@link java.lang.String} object.
     */
    public static void addPattern(String id, String iri, Pattern pattern) {
        idPatterns.put(id, pattern);
        iriPatterns.put(iri, pattern);
    }

    /**
     * <p>getPatternFromID.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @since 0.7.7
     */
    public static Pattern getPatternFromID(String id) {
        return idPatterns.get(id);
    }


    /**
     * <p>getPatternFromID.</p>
     *
     * @param iri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     * @since 0.7.7
     */
    public static Pattern getPatternFromIRI(String iri) {
        return iriPatterns.get(iri);
    }

    /**
     * <p>getPatternList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> getPatternList() {
        return idPatterns.keySet();
    }


}

