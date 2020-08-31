package org.aksw.rdfunit.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.commons.RdfUnitModelFactory;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfStreamReader;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;

@Slf4j
public final class RDFUnitUtils {

  private static Model defModel = RdfUnitModelFactory.createDefaultModel();
  private static Property vannPrefix = defModel
      .getProperty("http://purl.org/vocab/vann/preferredNamespacePrefix");
  private static Property vannNamespace = defModel
      .getProperty("http://purl.org/vocab/vann/preferredNamespaceUri");

  private RDFUnitUtils() {
  }

  public static void fillSchemaServiceFromFile(String additionalCSV) throws IOException {

    try (InputStream inputStream = new FileInputStream(additionalCSV)) {
      fillSchemaServiceFromFile(inputStream);
    } catch (IOException e) {
      log.error("Error loading schemas from " + additionalCSV + "!", e);
      throw e;
    }
  }

  private static void fillSchemaServiceFromResource(String additionalCSVAsResource)
      throws IOException {

    URL resourceUrl = Resources.getResource(additionalCSVAsResource);
    try (
        InputStream inputStream = resourceUrl.openStream()) {
      fillSchemaServiceFromFile(inputStream);
    } catch (IOException e) {
      log.error("Error loading schemas from " + additionalCSVAsResource + "!", e);
      throw e;
    }
  }

  private static void fillSchemaServiceFromFile(InputStream additionalCSV) throws IOException {

    int count = 0;

    if (additionalCSV != null) {

      try (BufferedReader in = new BufferedReader(
          new InputStreamReader(additionalCSV, StandardCharsets.UTF_8))) {

        String line;

        while ((line = in.readLine()) != null) {
          // skip comments & empty lines
          String trimmed = line.trim();
          if (trimmed.startsWith("#") || trimmed.isEmpty()) {
            continue;
          }

          count++;

          String[] parts = trimmed.split(",");
          switch (parts.length) {
            case 2:
              SchemaService.addSchemaDecl(parts[0], parts[1]);
              break;
            case 3:
              SchemaService.addSchemaDecl(parts[0], parts[1], parts[2]);
              break;
            default:
              log.error("Invalid schema declaration in " + additionalCSV + ". Line: " + line);
              count--;
              break;
          }
        }

      }

      log.info("Loaded " + count + " schema declarations from: " + additionalCSV);
    }

    if (additionalCSV != null) {
      try {
        additionalCSV.close();
      } catch (IOException e) {
        log.debug("IOException: ", e);
      }
    }
  }

  public static void fillSchemaServiceWithStandardVocabularies() throws IOException {
    log.info("Adding standard vocabularies.");
    List<String> fileNames = IOUtils.readLines(
        RDFUnitUtils.class.getClassLoader().getResourceAsStream("org/aksw/rdfunit/vocabularies/"),
        Charsets.UTF_8);
    for (String f : fileNames) {
      if (!f.trim().endsWith(".md") && !f.trim().endsWith(".txt")) {
        try {
          URL resource = RDFUnitUtils.class.getClassLoader()
              .getResource("org/aksw/rdfunit/vocabularies/" + f);
          if (resource != null) {
            RdfReader reader = new RdfStreamReader(resource.openStream(),
                FormatService.getFormatFromExtension(f));
            Model model = reader.read();
            StmtIterator prefixIter = model.listStatements(null, vannPrefix, (String) null);
            StmtIterator namespaceIter = model.listStatements(null, vannNamespace, (String) null);
            if (prefixIter.hasNext() && namespaceIter.hasNext()) {
              SchemaService.addSchemaDecl(prefixIter.next().getObject().toString(),
                  namespaceIter.next().getObject().toString(), resource.toString());
            }
          }
        } catch (RdfReaderException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static void fillSchemaServiceFromSchemaDecl() throws IOException {
    log.info("Adding manual schema entries (or overriding LOV)!");
    RDFUnitUtils.fillSchemaServiceFromResource("org/aksw/rdfunit/configuration/schemaDecl.csv");
  }

  public static void fillSchemaServiceFromLOV() throws IOException {

    log.info("Loading cached schema entries from LOV!");
    RDFUnitUtils.fillSchemaServiceFromResource("org/aksw/rdfunit/configuration/schemaLOV.csv");
  }

  public static <T> Optional<T> getFirstItemInCollection(Collection<T> collection) {
    return collection.stream().findFirst();
  }

  public static List<SchemaSource> augmentWithOwlImports(Set<SchemaSource> originalSources) {

    ImmutableList.Builder<SchemaSource> augmentedSources = ImmutableList.builder();
    augmentedSources.addAll(originalSources);

    Set<String> schemaIris = originalSources.stream().map(SchemaSource::getSchema)
        .collect(Collectors.toSet());
    Set<SchemaSource> currentSources = new HashSet<>(originalSources);

    while (!currentSources.isEmpty()) {

      Set<SchemaSource> computedSources = currentSources.stream()
          .map(SchemaSource::getModel)
          .flatMap(m -> m.listObjectsOfProperty(OWL.imports).toList().stream())
          .filter(RDFNode::isResource)
          .map(RDFNode::asResource)
          .map(Resource::getURI)
          .filter(uri -> !schemaIris.contains(uri))
          .map(SchemaService::getSourceFromUri)
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());

      Set<String> newIris = computedSources.stream().map(SchemaSource::getSchema)
          .collect(Collectors.toSet());
      schemaIris.addAll(newIris);

      augmentedSources.addAll(computedSources);
      currentSources = computedSources;

    }
    return augmentedSources.build();
  }
}
