package org.aksw.rdfunit.sources;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>SchemaSource class.</p>
 *
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:51 PM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode(exclude={"model", "schemaReader"})
public class SchemaSource implements Source {
    /** Constant <code>log</code> */
    protected static final Logger LOGGER = LoggerFactory.getLogger(SchemaSource.class);


    protected final SourceConfig sourceConfig;
    @Getter private final String schema;

    protected final RdfReader schemaReader;
    @Getter(lazy=true) private final Model model = initModel() ;

    SchemaSource(SourceConfig sourceConfig, RdfReader schemaReader) {
        this(sourceConfig, sourceConfig.getUri(), schemaReader);
    }


    SchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader) {
        this.sourceConfig = sourceConfig;
        this.schema = schema;
        this.schemaReader = schemaReader;
    }

    SchemaSource(SchemaSource source) {
        this.sourceConfig = source.sourceConfig;
        this.schema = source.getSchema();
        this.schemaReader = source.schemaReader;
    }

    @Override
    public String getPrefix() {
        return sourceConfig.getPrefix();
    }

    @Override
    public String getUri() {
        return sourceConfig.getUri();
    }

    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Schema;
    }


    /**
     * lazy loaded via lombok
     */
    private Model initModel() {
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        try {
            schemaReader.read(m);
        } catch (RdfReaderException e) {
            LOGGER.error("Cannot load ontology: {} ", getSchema(), e);
        }
        return m;
    }



}
