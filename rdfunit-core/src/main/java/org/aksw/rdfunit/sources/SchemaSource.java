package org.aksw.rdfunit.sources;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.io.reader.RdfMultipleReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.jena.assembler.JA.getSchema;

/**
 * @author Dimitris Kontokostas
 * @since 9/16/13 1:51 PM
 */
@ToString
@EqualsAndHashCode(exclude={"model", "schemaReader", "imports"})
public class SchemaSource implements Source {
    /** Constant <code>log</code> */
    protected static final Logger log = LoggerFactory.getLogger(SchemaSource.class);

    protected final SourceConfig sourceConfig;
    @Getter private final String schema;

    protected final RdfReader schemaReader;
    @Getter(lazy=true) private final Model model = initModel() ;

    private final Set<SchemaSource> predefinedImports = new HashSet<>();
    @Getter(lazy=true) private final Set<SchemaSource> imports = collectImports();

    SchemaSource(SourceConfig sourceConfig, RdfReader schemaReader, Collection<SchemaSource> imports) {
        this(sourceConfig, sourceConfig.getUri(), schemaReader, imports);
    }

    SchemaSource(SourceConfig sourceConfig, RdfReader schemaReader) {
        this(sourceConfig, sourceConfig.getUri(), schemaReader);
    }

    SchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader, Collection<SchemaSource> imports) {
        this.sourceConfig = sourceConfig;
        this.schema = schema;
        this.schemaReader = new RdfMultipleReader(Lists.asList(schemaReader,
                imports.stream().map(x -> x.schemaReader).collect(Collectors.toList()).toArray(new RdfReader[imports.size()])));
        this.predefinedImports.addAll(imports);
    }

    SchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader) {
        this.sourceConfig = sourceConfig;
        this.schema = schema;
        this.schemaReader = schemaReader;
    }

    public SchemaSource(SchemaSource source) {this(source.sourceConfig, source.schema, source.schemaReader);}

    public SchemaSource(SchemaSource source, Collection<SchemaSource> imports) {this(source.sourceConfig, source.getSchema(), source.schemaReader, imports);}

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

    public SourceConfig getSourceConfig() {
        return sourceConfig;
    }

    private Set<SchemaSource> collectImports(){
        Model m = this.getModel();
        Property importsProp = m.createProperty("http://www.w3.org/2002/07/owl#imports");
        Set<String> imports = Lists.newArrayList(m.listObjectsOfProperty(importsProp)).stream().map(x -> x.asResource().getURI()).collect(Collectors.toSet());
        HashSet<SchemaSource> importSources = new HashSet<>(this.predefinedImports);
        imports.remove(this.getUri());                          //remove cyclic
        for(String uri : imports){
            Optional<SchemaSource> imported = SchemaService.getSourceFromUri(uri);
            if(imported.isPresent()){
                importSources.add(imported.get());
            } else{
                log.warn("Cannot find ontology: " + uri + " to import into " + SchemaSource.this.getSchema());
            }
        }
        return importSources;
    }

    /**
     * lazy loaded via lombok
     */
    private Model initModel() {
        OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, ModelFactory.createDefaultModel());
        try {
            schemaReader.read(m);
        } catch (RdfReaderException e) {
            log.error("Cannot load ontology: {} ", getSchema(), e);
        }
        return m;
    }

    /**
     * Will return this model with all transitive imports
     */
    public Model getTransitiveModel(){
        Model res = this.getModel();
        Set<SchemaSource> allImports = this.getImports().stream().flatMap(x -> x.getImports().stream()).filter(x -> ! x.getUri().equals(this.getUri())).collect(Collectors.toSet());
        allImports.addAll(this.getImports());
        allImports.forEach(x -> res.add(x.getTransitiveModel()));
        return res;
    }
}
