package org.aksw.rdfunit.services;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.aksw.rdfunit.model.interfaces.Pattern;

/**
 * class that contains instances of all idPatterns
 *
 * @author Dimitris Kontokostas
 * @since 9/20/13 2:52 PM
 */
public final class PatternService {

  final private static Map<String, Pattern> idPatterns = new ConcurrentHashMap<>();
  final private static Map<String, Pattern> iriPatterns = new ConcurrentHashMap<>();


  private PatternService() {
  }

  public static void addPattern(String id, String iri, Pattern pattern) {
    idPatterns.put(id, pattern);
    iriPatterns.put(iri, pattern);
  }

  public static Pattern getPatternFromID(String id) {
    return idPatterns.get(id);
  }


  public static Pattern getPatternFromIRI(String iri) {
    return iriPatterns.get(iri);
  }

  public static Set<String> getPatternList() {
    return idPatterns.keySet();
  }


}

