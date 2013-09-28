package org.aksw.databugger.patterns;

import java.util.HashMap;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * class that contains instances of all patterns
 * Created: 9/20/13 2:52 PM
 */
public class PatternService {
    final private static HashMap<String, Pattern> patterns = new HashMap<String, Pattern>();


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

