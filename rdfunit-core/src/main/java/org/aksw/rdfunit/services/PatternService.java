package org.aksw.rdfunit.services;

import org.aksw.rdfunit.patterns.Pattern;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>PatternService class.</p>
 *
 * @author Dimitris Kontokostas
 *         class that contains instances of all patterns
 * @since 9/20/13 2:52 PM
 * @version $Id: $Id
 */
public final class PatternService {
    final private static Map<String, Pattern> patterns = new ConcurrentHashMap<>();


    private PatternService() {
    }

    /**
     * <p>addPattern.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param pattern a {@link org.aksw.rdfunit.patterns.Pattern} object.
     */
    public static void addPattern(String id, Pattern pattern) {
        patterns.put(id, pattern);
    }

    /**
     * <p>getPattern.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.patterns.Pattern} object.
     */
    public static Pattern getPattern(String id) {
        return patterns.get(id);
    }

    /**
     * <p>getPatternList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> getPatternList() {
        return patterns.keySet();
    }


}

