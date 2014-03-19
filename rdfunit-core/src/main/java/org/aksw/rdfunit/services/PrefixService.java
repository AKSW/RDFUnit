package org.aksw.rdfunit.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * Keeps a list of all prefixes used in the project
 * Created: 10/1/13 7:06 PM
 */
public class PrefixService {
    final private static HashMap<String, String> prefixes = new HashMap<String, String>();


    private PrefixService() {
    }

    public static void addPrefix(String prefix, String uri) {
        prefixes.put(prefix, uri);
    }

    public static String getPrefix(String id) {
        return prefixes.get(id);
    }

    public static Set<String> getPrefixList() {
        return prefixes.keySet();
    }

    public static Map<String, String> getPrefixMap() {
        return prefixes;
    }
}
