package org.aksw.rdfunit.sources;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:51 PM
 */
@ToString
@EqualsAndHashCode(exclude = {"model", "schemaReader"})
public class SchemaSource implements Source {

  /**
   * Constant <code>log</code>
   */
  protected static final Logger log = LoggerFactory.getLogger(SchemaSource.class);

  protected final SourceConfig sourceConfig;
  protected final RdfReader schemaReader;
  @Getter
  private final String schema;
  @Getter(lazy = true)
  private final Model model = initModel();

  SchemaSource(SourceConfig sourceConfig, RdfReader schemaReader) {
    this(sourceConfig, sourceConfig.getUri(), schemaReader);
  }

  SchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader) {
    this.sourceConfig = sourceConfig;
    this.schema = schema;
    this.schemaReader = schemaReader;
  }

  public SchemaSource(SchemaSource source) {
    this(source.sourceConfig, source.schema, source.schemaReader);
  }

  @Override
  public String getPrefix() {
    if (sourceConfig.getPrefix() != "") {
      return sourceConfig.getPrefix();
    } else {
      Model m = this.getModel();

      Selector selector = new SimpleSelector(m.getResource(schema),
          m.getProperty("http://purl.org/vocab/vann/preferredNamespacePrefix"), (RDFNode) null);
      List<Statement> prefixStmts = m.listStatements(selector).toList();

      if (prefixStmts.size() == 1 && prefixStmts.get(0).getObject().isLiteral()) {
        return prefixStmts.get(0).getObject().asLiteral().getString();
      }

      log.warn("Prefix not given (-p), neither defined in schema (vann:preferredNamespacePrefix).");
      return "";
    }
  }

  @Override
  public String getUri() {
    return sourceConfig.getUri();
  }

  @Override
  public TestAppliesTo getSourceType() {
    return TestAppliesTo.Schema;
  }

  public SourceConfig getSourceConfig() {
    return sourceConfig;
  }

  /**
   * lazy loaded via lombok
   */
  private Model initModel() {
    OntModel m = ModelFactory
        .createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
    try {
      schemaReader.read(m);
    } catch (RdfReaderException e) {
      log.error("Cannot load ontology: {} ", getSchema(), e);
    }
    return m;
  }
}
