package org.aksw.rdfunit.statistics;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.SourceConfig;
import org.aksw.rdfunit.utils.UriToPathUtils;

/**
 * Provides statistics on the namespaces used in a dataset
 *
 * @author Dimitris Kontokostas
 * @since 6/27/15 1:20 PM
 */
@Slf4j
public final class NamespaceStatistics {

  private final boolean skipUnknownNamespaces;
  private final Collection<SourceConfig> excludeSchemata;
  private final Collection<DatasetStatistics> datasetStatistics;

  private NamespaceStatistics(Collection<DatasetStatistics> datasetStatisticses,
      boolean skipUnknownNamespaces, RDFUnitConfiguration conf) {

    this.datasetStatistics = Collections.unmodifiableCollection(datasetStatisticses);
    this.skipUnknownNamespaces = skipUnknownNamespaces;
    if (conf != null) {
      this.excludeSchemata = conf.getExcludeSchemata().stream().map(SchemaSource::getSourceConfig)
          .collect(Collectors.toList());
    } else {
      this.excludeSchemata = Lists.newArrayList();
    }
  }

  public static NamespaceStatistics createOntologyNSStatisticsKnown(RDFUnitConfiguration conf) {
    Collection<DatasetStatistics> datasetStatistics = Arrays
        .asList(new DatasetStatisticsClasses(), new DatasetStatisticsProperties());
    return new NamespaceStatistics(datasetStatistics, true, conf);
  }

  public static NamespaceStatistics createOntologyNSStatisticsAll(RDFUnitConfiguration conf) {
    Collection<DatasetStatistics> datasetStatistics = Arrays
        .asList(new DatasetStatisticsClasses(), new DatasetStatisticsProperties());
    return new NamespaceStatistics(datasetStatistics, false, conf);
  }

  public static NamespaceStatistics createCompleteNSStatisticsKnown(RDFUnitConfiguration conf) {
    Collection<DatasetStatistics> datasetStatistics = new ArrayList<>();
    datasetStatistics.add(new DatasetStatisticsAllIris());
    return new NamespaceStatistics(datasetStatistics, true, conf);
  }

  public static NamespaceStatistics createCompleteNSStatisticsAll(RDFUnitConfiguration conf) {
    Collection<DatasetStatistics> datasetStatistics = new ArrayList<>();
    datasetStatistics.add(new DatasetStatisticsAllIris());
    return new NamespaceStatistics(datasetStatistics, false, conf);
  }


  public Collection<SchemaSource> getNamespaces(QueryExecutionFactory qef) {

    Set<String> namespaces = new HashSet<>();

    for (DatasetStatistics dt : datasetStatistics) {

      namespaces.addAll(dt.getStatisticsMap(qef).keySet().stream()
          .map(this::getNamespaceFromURI)
          .collect(Collectors.toList()));

    }

    return getIdentifiedSchemata(namespaces);
  }


  private Collection<SchemaSource> getIdentifiedSchemata(Collection<String> namespaces) {
    Collection<SchemaSource> sources = new ArrayList<>();

    for (String namespace : namespaces) {

      Optional<SchemaSource> source = SchemaService.getSourceFromUri(namespace);

      // If not null, get it from SchemaService
      if (source.isPresent()) {

        // Skip some schemas which should be excluded
        if (excludeSchemata.contains(source.get().getSourceConfig())) {
          continue;
        }
        sources.add(source.get());
      } else {
        if (skipUnknownNamespaces) {
          log.warn("Undefined namespace in LOV or schemaDecl.csv: " + namespace);
        } else {
          sources.add(SchemaSourceFactory
              .createSchemaSourceDereference(UriToPathUtils.getAutoPrefixForURI(namespace),
                  namespace));
        }
      }
    }

    return sources;
  }

  /**
   * Gets namespace from uRI.
   *
   * @param uri the uri
   * @return the namespace from uRI
   */
  public String getNamespaceFromURI(String uri) {
    String breakChar = "/";
    if (uri.contains("#")) {
      breakChar = "#";
    } else {
      if (uri.substring(6).contains(":")) {
        breakChar = ":";
      }
    }

    int pos = Math.min(uri.lastIndexOf(breakChar), uri.length());
    return uri.substring(0, pos + 1);
  }
}
