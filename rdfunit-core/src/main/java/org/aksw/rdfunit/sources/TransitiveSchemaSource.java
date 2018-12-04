package org.aksw.rdfunit.sources;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aksw.rdfunit.io.reader.RdfMultipleReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.util.*;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
public class TransitiveSchemaSource extends SchemaSource {

    private final Set<SchemaSource> predefinedImports = new HashSet<>();
    @Getter(lazy=true) private final Set<SchemaSource> imports = collectImports();

    TransitiveSchemaSource(SourceConfig sourceConfig, RdfReader schemaReader, Collection<SchemaSource> imports) {
        this(sourceConfig, sourceConfig.getUri(), schemaReader, imports);
    }

    TransitiveSchemaSource(SourceConfig sourceConfig, String schema, RdfReader schemaReader, Collection<SchemaSource> imports) {
        super(sourceConfig, schema,
            new RdfMultipleReader(Lists.asList(schemaReader,
                imports.stream().map(x -> x.schemaReader).collect(Collectors.toList()).toArray(new RdfReader[imports.size()]))));
        this.predefinedImports.addAll(imports);
    }

    public TransitiveSchemaSource(SchemaSource source, Collection<SchemaSource> imports) {
        this(source.sourceConfig, source.getSchema(), source.schemaReader, imports);
    }

    public TransitiveSchemaSource(SchemaSource source) {
        this(source, Collections.emptyList());
    }

    /**
     * Tries to collect a SchemaSource for each imported ontology.
     */
    private Set<SchemaSource> collectImports(){
        Model m = this.getModel();
        Property importsProp = m.createProperty("http://www.w3.org/2002/07/owl#imports");
        Set<String> imports = Lists.newArrayList(m.listObjectsOfProperty(importsProp)).stream().map(x -> x.asResource().getURI()).collect(Collectors.toSet());
        HashSet<SchemaSource> importSources = new HashSet<>(this.predefinedImports);
        this.predefinedImports.forEach(i -> imports.remove(i.getUri()));                //remove cyclic
        imports.remove(this.getUri());
        for(String uri : imports){
            Optional<SchemaSource> imported = SchemaService.getSourceFromUri(uri);
            if(imported.isPresent()){
                importSources.add(imported.get());
            } else{
                log.warn("Cannot find ontology: " + uri + " to import into " + TransitiveSchemaSource.this.getSchema());
            }
        }
        return importSources;
    }

    /**
     * Will return this model with all transitive imports
     */
    public Set<SchemaSource> getTransitiveImports(){
        Set<SchemaSource> allImports = this.getImports().stream()
                .flatMap(schema -> new TransitiveSchemaSource(schema).getImports().stream())
                .filter(x -> ! x.getUri().equals(this.getUri()))
                .collect(Collectors.toSet());

        allImports.addAll(this.getImports());
        return allImports;
    }

    @Override
    public Model getModel() {
        Model res = super.getModel();
        getTransitiveImports().forEach(s -> res.add(s.getModel()));
        return res;
    }
}
