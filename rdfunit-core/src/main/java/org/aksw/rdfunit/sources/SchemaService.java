package org.aksw.rdfunit.sources;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * Holds all the schema definitions It is usually instantiated from LOV on app startup
 *
 * @author Dimitris Kontokostas
 * @since 10/2/13 12:24 PM
 */
public final class SchemaService {

  /**
   * Creates a Bi-Directional map between prefix & namespace
   */
  private static final BiMap<String, String> schemata = HashBiMap.create();

  /**
   * if namespace is different from the ontology uri, we keep it in this map
   */
  private static final Map<String, String> definedBy = new HashMap<>();

  private SchemaService() {
  }

  public static int getSize() {
    return schemata.size();
  }

  public static void addSchemaDecl(String prefix, String uri, String url) {
    schemata.forcePut(prefix, uri);
    definedBy.put(uri, url);
  }

  public static void addSchemaDecl(String prefix, String uri) {
    schemata.put(prefix, uri);
  }


  public static Optional<SchemaSource> getSourceFromUri(String uri) {
    return getSourceFromUri(null, uri);
  }


  public static Optional<SchemaSource> getSourceFromUri(String baseFolder, String uri) {
    String prefix = schemata.inverse().get(uri);
    if (prefix == null) {
      return Optional.empty();
    }

    return getSourceFromPrefix(baseFolder, prefix);
  }


  public static Optional<SchemaSource> getSourceFromPrefix(String prefix) {
    return getSourceFromPrefix(null, prefix);
  }


  public static Optional<SchemaSource> getSourceFromPrefix(String baseFolder, String prefix) {
    String namespace = schemata.get(prefix);
    if (namespace == null) {
      return getDereferenceSchemaSource(prefix);
    }

    String isDefinedBy = definedBy.get(namespace);
    if (isDefinedBy != null && !isDefinedBy.isEmpty()) {
      return getSchemaSourceWithDefinedBy(baseFolder, prefix, namespace, isDefinedBy);
    } else {
      return getSchemaSourceFromNs(baseFolder, prefix, namespace);
    }
  }

  private static Optional<SchemaSource> getSchemaSourceFromNs(String baseFolder, String prefix,
      String namespace) {
    if (baseFolder != null) {
      return Optional
          .of(SchemaSourceFactory.createSchemaSourceFromCache(baseFolder, prefix, namespace));
    } else {
      return Optional.of(SchemaSourceFactory.createSchemaSourceDereference(prefix, namespace));
    }
  }

  private static Optional<SchemaSource> getSchemaSourceWithDefinedBy(String baseFolder,
      String prefix, String namespace, String isDefinedBy) {
    if (baseFolder != null) {
      return Optional.of(SchemaSourceFactory
          .createSchemaSourceFromCache(baseFolder, prefix, namespace, isDefinedBy));
    } else {
      return Optional
          .of(SchemaSourceFactory.createSchemaSourceDereference(prefix, namespace, isDefinedBy));
    }
  }

  private static Optional<SchemaSource> getDereferenceSchemaSource(String prefix) {
    // If not a prefix try to dereference it
    if (prefix.contains("/") || prefix.contains("\\")) {
      return Optional.of(
          SchemaSourceFactory
              .createSchemaSourceDereference(UriToPathUtils.getAutoPrefixForURI(prefix), prefix));
    }
    return Optional.empty();
  }

  public static SchemaSource getSource(String baseFolder, String prefixOrUri)
      throws UndefinedSchemaException {
    Optional<SchemaSource> src = getSourceFromPrefix(baseFolder, prefixOrUri.trim());
    if (src.isPresent()) {
      return src.get();
    } else {
      src = getSourceFromUri(baseFolder, prefixOrUri.trim());
      if (src.isPresent()) {
        return src.get();
      } else {
        throw new UndefinedSchemaException(prefixOrUri);
      }
    }
  }

  public static Collection<SchemaSource> getSourceList(String baseFolder,
      Collection<String> prefixesOrUris) throws UndefinedSchemaException {
    Collection<SchemaSource> sources = new ArrayList<>();
    for (String id : prefixesOrUris) {
      sources.add(getSource(baseFolder, id));
    }
    return sources;
  }


  public static Collection<SchemaSource> getSourceListAll(boolean fileCache, String baseFolder)
      throws UndefinedSchemaException {
    Collection<String> prefixes = new ArrayList<>(schemata.keySet());

    if (fileCache) {
      return getSourceList(baseFolder, prefixes);
    } else {
      return getSourceList(null, prefixes);
    }
  }
}

