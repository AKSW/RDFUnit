package org.aksw.rdfunit.webdemo;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.exceptions.UndefinedSchemaException;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Creates RDFUnitConfigurations
 * @since 11/15/13 12:15 PM
 */
public class RDFunitConfigurationFactory {

    //new RDFUnitConfiguration("linkedchemistry.info", "http://rdf.farmbio.uu.se/chembl/sparql", Arrays.asList("http://linkedchemistry.info/chembl/"), "cheminf,cito");
    public static RDFUnitConfiguration createConfiguration(String datasetURI, String endpointURI, Collection<String> endpointGraphs, String schemaPrefixes, String testFolder) throws UndefinedSchemaException {
        return createConfiguration(datasetURI, endpointURI, endpointGraphs, Arrays.asList(schemaPrefixes.split(",")), testFolder);
    }

    public static RDFUnitConfiguration createConfiguration(String datasetURI, String endpointURI, Collection<String> endpointGraphs, Collection<String> schemaPrefixes, String testFolder) throws UndefinedSchemaException {
        RDFUnitConfiguration configuration = new RDFUnitConfiguration(datasetURI, "", testFolder);
        configuration.setEndpointConfiguration(endpointURI, endpointGraphs);
        configuration.setSchemataFromPrefixes(schemaPrefixes);

        return configuration;
    }


    public static RDFUnitConfiguration createDBpediaConfiguration(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://dbpedia.org", "", baseFolder);

        configuration.setSchemataFromPrefixes(
                Arrays.asList(/*"rdf", "rdfs",*/ "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov", "owl"));

        configuration.setEnrichedSchema("dbo");
        configuration.setEndpointConfiguration("http://dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"));

        return configuration;
    }

    public static RDFUnitConfiguration createDBpediaConfigurationSimple(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://dbpedia.org", "", baseFolder);
        configuration.setPrefix("dbpedia.org");

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        configuration.setSchemataFromPrefixes(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        configuration.setEndpointConfiguration("http://dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"));

        return configuration;
    }

    public static RDFUnitConfiguration createDBpediaLiveConfigurationSimple(String baseFolder) throws UndefinedSchemaException {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        Collection<String> prefixes = Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov");

        return createConfiguration("http://live.dbpedia.org", "http://live.dbpedia.org/sparql", Arrays.asList("http://dbpedia.org"), prefixes, baseFolder);
    }

    public static RDFUnitConfiguration createDatosBneEsDataset(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://datos.bne.es", "", baseFolder);

        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        configuration.setSchemataFromPrefixes(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "frbrer", "isbd", "dcterms", "skos"));

        configuration.setEnrichedSchema("datos");

        configuration.setEndpointConfiguration("http://localhost:8890/sparql", Arrays.asList("http://datos.bne.es"));
        return configuration;
    }

    public static RDFUnitConfiguration createLCSHDataset(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://id.loc.gov", "", baseFolder);
        // vocabularies based on http://stats.lod2.eu/rdfdocs/44
        configuration.setSchemataFromPrefixes(
                Arrays.asList(/*"rdf", "rdfs", "owl",*/ "foaf", "dcterms", "skos", "mads", "mrel", "premis"));
        configuration.setEnrichedSchema("loc");

        configuration.setEndpointConfiguration("http://localhost:8891/sparql", Arrays.asList("http://id.loc.gov"));

        return configuration;
    }


    public static RDFUnitConfiguration createDBpediaNLDataset(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://nl.dbpedia.org", "", baseFolder);

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        configuration.setSchemataFromPrefixes(
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov"));

        //Enriched Schema (cached in folder)
        configuration.setEnrichedSchema("dbo");

        configuration.setEndpointConfiguration("http://nl.dbpedia.org/sparql", Arrays.asList("http://nl.dbpedia.org"));
        return configuration;
    }

    public static RDFUnitConfiguration createDBpediaNLDatasetSimple(String baseFolder) throws UndefinedSchemaException {

        // vocabularies based on http://stats.lod2.eu/rdfdocs/1719
        Collection<String> prefixes =
                Arrays.asList(/*"rdf", "rdfs",*/ "owl", "dbo", "foaf", "dcterms", "dc", "skos", "geo", /*"georss",*/ "prov");

        return createConfiguration("http://nl.dbpedia.org", "http://nl.dbpedia.org/sparql", Arrays.asList("http://nl.dbpedia.org"), prefixes, baseFolder);
    }

    public static RDFUnitConfiguration createLGDDataset(String baseFolder) throws UndefinedSchemaException {

        RDFUnitConfiguration configuration = new RDFUnitConfiguration("http://linkedgeodata.org", "", baseFolder);

        configuration.setSchemataFromPrefixes(
                Arrays.asList("ngeo", "spatial", "lgdm", "lgdo", "dcterms", "gsp", /*"rdf","rdfs",*/ "owl", "geo", "skos", "foaf"));

        configuration.setEnrichedSchema("lgd");
        configuration.setEndpointConfiguration("http://localhost:8891/sparql", Arrays.asList("http://linkedgeodata.org"));

        return configuration;
    }
}
