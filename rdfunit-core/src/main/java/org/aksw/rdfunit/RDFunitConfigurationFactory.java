package org.aksw.rdfunit;

import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SourceFactory;

import java.util.Arrays;

/**
 * User: Dimitris Kontokostas
 * Creates RDFUnitConfigurations
 * Created: 11/15/13 12:15 PM
 */
public class RDFunitConfigurationFactory {
    public static RDFUnitConfiguration createDBpediaConfiguration(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov", "owl"));

        //Enriched Schema (cached in folder)
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "dbo", "http://dbpedia.org"));

        return new RDFUnitConfiguration("dbpedia.org", "http://dbpedia.org", "http://dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"), sources);
    }

    public static RDFUnitConfiguration createDBpediaConfigurationSimple(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        return new RDFUnitConfiguration("dbpedia.org", "http://dbpedia.org", "http://dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"), sources);
    }

    public static RDFUnitConfiguration createDBpediaLiveConfigurationSimple(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        return new RDFUnitConfiguration("live.dbpedia.org", "http://live.dbpedia.org", "http://live.dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"), sources);
    }

    public static RDFUnitConfiguration createDatosBneEsDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "frbrer", "isbd", "dcterms", "skos"));

        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "datos", "http://datos.bne.es"));

        return new RDFUnitConfiguration("datos.bne.es", "http://datos.bne.es", "http://localhost:8890/sparql", Arrays.asList("http://datos.bne.es"), sources);
    }

    public static RDFUnitConfiguration createLCSHDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs", "owl",*/ "foaf", "dcterms", "skos", "mads", "mrel", "premis"));
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "loc", "http://id.loc.gov"));


        return new RDFUnitConfiguration("id.loc.gov", "http://id.loc.gov", "http://localhost:8891/sparql", Arrays.asList("http://id.loc.gov"), sources);
    }


    public static RDFUnitConfiguration createDBpediaNLDataset(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "dbo", "http://nl.dbpedia.org"));

        return new RDFUnitConfiguration("nl.dbpedia.org", "http://nl.dbpedia.org", "http://nl.dbpedia.org/sparql", Arrays.asList("http://nl.dbpedia.org"), sources);
    }

    public static RDFUnitConfiguration createDBpediaNLDatasetSimple(String baseFolder) {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder,
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        return new RDFUnitConfiguration("nl.dbpedia.org", "http://nl.dbpedia.org", "http://nl.dbpedia.org/sparql", Arrays.asList("http://nl.dbpedia.org"), sources);
    }

    public static RDFUnitConfiguration createLGDDataset(String baseFolder) {

        java.util.Collection<SchemaSource> sources = SchemaService.getSourceList(baseFolder, Arrays.asList(
                "ngeo", "spatial", "lgdm", "lgdo", "dcterms", "gsp", /*"rdf",
                "rdfs",*/ "owl", "geo", "skos", "foaf"));

        sources.add(SourceFactory.createEnrichedSchemaSourceFromCache(baseFolder, "lgd", "http://linkedgeodata.org"));
        return new RDFUnitConfiguration("linkedgeodata.org", "http://linkedgeodata.org", "http://localhost:8891/sparql", Arrays.asList("http://linkedgeodata.org"), sources);
    }
}
