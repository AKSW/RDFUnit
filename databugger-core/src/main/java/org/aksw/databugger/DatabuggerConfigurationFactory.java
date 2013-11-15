package org.aksw.databugger;

import org.aksw.databugger.services.SchemaService;
import org.aksw.databugger.sources.SchemaSource;
import org.aksw.databugger.sources.SourceFactory;

import java.util.Arrays;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Creates DatabuggerConfigurations
 * Created: 11/15/13 12:15 PM
 */
public class DatabuggerConfigurationFactory {
    public static DatabuggerConfiguration createDBpediaConfiguration(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        List<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "dbo", "http://dbpedia.org"));

        return new DatabuggerConfiguration("dbpedia.org", "http://dbpedia.org", "http://dbpedia.org/sparql", "http://dbpedia.org", sources);
    }

    public static DatabuggerConfiguration createDatosBneEsDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        List<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "frbrer", "isbd", "dcterms", "skos"));

        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "datos", "http://datos.bne.es"));

        return new DatabuggerConfiguration("datos.bne.es", "http://datos.bne.es", "http://localhost:8890/sparql", "http://datos.bne.es", sources);
    }

    public static DatabuggerConfiguration createLCSHDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        List<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs", "owl",*/ "foaf", "dcterms", "skos", "mads", "mrel", "premis"));
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "loc", "http://id.loc.gov"));


        return new DatabuggerConfiguration("id.loc.gov", "http://id.loc.gov", "http://localhost:8891/sparql", "http://id.loc.gov", sources);
    }


    public static DatabuggerConfiguration createDBpediaNLDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        List<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "dbo", "http://nl.dbpedia.org"));

        return new DatabuggerConfiguration("nl.dbpedia.org", "http://nl.dbpedia.org", "http://nl.dbpedia.org/sparql", "http://nl.dbpedia.org", sources);
    }

    public static DatabuggerConfiguration createLGDDataset(String baseFolder) {

        List<SchemaSource> sources = SchemaService.getSourceList(baseFolder, Arrays.asList(
                "ngeo", "spatial", "lgdm", "lgdo", "dcterms", "gsp", /*"rdf",
                "rdfs",*/ "owl", "geo", "skos", "foaf"));

        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "lgd", "http://linkedgeodata.org"));
        return new DatabuggerConfiguration("linkedgeodata.org", "http://linkedgeodata.org", "http://localhost:8891/sparql", "http://linkedgeodata.org", sources);
    }
}
