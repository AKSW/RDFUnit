package org.aksw.rdfunit.services;

import org.aksw.rdfunit.patterns.Pattern;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Dimitris Kontokostas
 *         class that contains instances of all patterns
 * @since 9/20/13 2:52 PM
 */
public final class PatternService {
    final private static Map<String, Pattern> patterns = new ConcurrentHashMap<>();


    private PatternService() {
    }

    public static void addPattern(String id, Pattern pattern) {
        patterns.put(id, pattern);
    }

    public static Pattern getPattern(String id) {
        return patterns.get(id);
    }

    public static Set<String> getPatternList() {
        return patterns.keySet();
    }


}

