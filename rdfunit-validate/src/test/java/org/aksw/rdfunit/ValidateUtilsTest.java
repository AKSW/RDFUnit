package org.aksw.rdfunit;

import org.aksw.rdfunit.services.SchemaService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ValidateUtilsTest {

    @org.junit.Test
    public void testGetConfigurationFromArguments() throws Exception {
        Options cliOptions = ValidateUtils.getCliOptions();

        String args ="";
        RDFUnitConfiguration configuration = null;
        CommandLine commandLine = null;
        CommandLineParser cliParser = new GnuParser();

        // Set two dummy schemas for testing
        SchemaService.addSchemaDecl("rdfs","http://www.w3.org/2000/01/rdf-schema#");
        SchemaService.addSchemaDecl("owl","http://www.w3.org/2002/07/owl#");

        args = " -d http://dbpedia.org -e http://dbpedia.org/sparql -g http://dbpedia.org -s rdfs,owl -p dbo";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertEquals(configuration.getDatasetURI(), "http://dbpedia.org");
        assertEquals(configuration.getPrefix(), "dbpedia.org");
        assertEquals(configuration.getEndpointURI(), "http://dbpedia.org/sparql");
        assertNull(configuration.getCustomDereferenceURI());
        assertEquals(configuration.getEndpointGraphs(), Arrays.asList("http://dbpedia.org"));
        assertEquals(configuration.getAllSchemata().size(), 3); //2 schema + 1 enriched
        assertNotNull(configuration.getEnrichedSchema());
        assertEquals(configuration.getDataFolder(), "../data/");
        assertEquals(configuration.getTestFolder(), "../data/tests/");


        args = " -d http://dbpedia.org -U http://custom.dbpedia.org -s rdfs -f /home/rdfunit/";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertEquals(configuration.getDatasetURI(), "http://dbpedia.org");
        assertEquals(configuration.getCustomDereferenceURI(), "http://custom.dbpedia.org");
        assertEquals(configuration.getAllSchemata().size(), 1);
        assertNull(configuration.getEnrichedSchema());
        assertEquals(configuration.getDataFolder(), "/home/rdfunit/");


        // foaf does not exists in service
        args = " -d http://dbpedia.org -s foaf -f /home/rdfunit/";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertNotNull(configuration.getAllSchemata()); // although empty it shouldn't be null
        assertEquals(configuration.getAllSchemata().size(), 0);
        assertEquals(configuration.getDataFolder(), "/home/rdfunit/");
        assertEquals(configuration.getTestFolder(), "/home/rdfunit/tests/");

    }
}