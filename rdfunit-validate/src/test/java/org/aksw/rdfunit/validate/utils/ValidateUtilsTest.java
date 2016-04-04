package org.aksw.rdfunit.validate.utils;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.sources.DumpTestSource;
import org.aksw.rdfunit.sources.EndpointTestSource;
import org.aksw.rdfunit.sources.SchemaService;
import org.aksw.rdfunit.validate.ParameterException;
import org.apache.commons.cli.*;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ValidateUtilsTest {

    @Test
    public void testGetConfigurationFromArguments() throws ParseException, ParameterException {
        Options cliOptions = ValidateUtils.getCliOptions();

        String args = "";
        RDFUnitConfiguration configuration = null;
        CommandLine commandLine = null;
        CommandLineParser cliParser = new DefaultParser();

        // Set two dummy schemas for testing
        SchemaService.addSchemaDecl("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        SchemaService.addSchemaDecl("owl", "http://www.w3.org/2002/07/owl#");

        /**
         * Sample endpoint configuration
         */
        args = " -d http://dbpedia.org -e http://dbpedia.org/sparql -g http://dbpedia.org -s rdfs,owl -p dbo -A -T 10 -P 10 -D 10 -L 10";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertEquals(configuration.getDatasetURI(), "http://dbpedia.org");
        assertEquals(configuration.getPrefix(), "dbpedia.org");

        // Get endpoint details
        assertEquals(configuration.getEndpointURI(), "http://dbpedia.org/sparql");
        assertNull(configuration.getCustomDereferenceURI());
        assertTrue(configuration.getTestSource() instanceof EndpointTestSource);
        assertEquals(configuration.getEndpointGraphs(), Collections.singletonList("http://dbpedia.org"));

        // get schemas
        assertEquals(configuration.getAllSchemata().size(), 3); //2 schema + 1 enriched
        assertNotNull(configuration.getEnrichedSchema());

        // data folder
        assertEquals(configuration.getDataFolder(), "data/");
        assertEquals(configuration.getTestFolder(), "data/tests/");

        // output formats
        assertEquals(configuration.getOutputFormats().size(), 1); // html by default

        // Manual / Auto test cases
        assertTrue(configuration.isManualTestsEnabled());
        assertFalse(configuration.isAutoTestsEnabled());
        assertFalse(configuration.isTestCacheEnabled());

        // Limit / pagination / TTL / Delay
        EndpointTestSource endpointTestSource = (EndpointTestSource) configuration.getTestSource();
        assertEquals(configuration.getEndpointQueryCacheTTL(), 10L * 60L * 1000L);
        assertEquals(configuration.getEndpointQueryCacheTTL(), endpointTestSource.getCacheTTL());
        assertEquals(configuration.getEndpointQueryDelayMS(), 10L);
        assertEquals(configuration.getEndpointQueryDelayMS(), endpointTestSource.getQueryDelay());
        assertEquals(configuration.getEndpointQueryPagination(), 10L);
        assertEquals(configuration.getEndpointQueryPagination(), endpointTestSource.getPagination());
        assertEquals(configuration.getEndpointQueryLimit(), 10L);
        assertEquals(configuration.getEndpointQueryLimit(), endpointTestSource.getQueryLimit());


        /**
         * Sample dereference configuration
         */

        args = " -d http://dbpedia.org -u http://custom.dbpedia.org -s rdfs -f /home/rdfunit/ -M -o html,turtle";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertEquals(configuration.getDatasetURI(), "http://dbpedia.org");
        assertEquals(configuration.getCustomDereferenceURI(), "http://custom.dbpedia.org");

        // Schemas
        assertEquals(configuration.getAllSchemata().size(), 1);
        assertNull(configuration.getEnrichedSchema());

        // output formats
        assertEquals(configuration.getOutputFormats().size(), 2); // html,turtle
        assertEquals(configuration.getDataFolder(), "/home/rdfunit/");
        assertEquals(configuration.getTestFolder(), "/home/rdfunit/tests/");

        // Manual / Auto test cases
        assertFalse(configuration.isManualTestsEnabled());
        assertTrue(configuration.isAutoTestsEnabled());
        assertTrue(configuration.isTestCacheEnabled());

        assertFalse(configuration.isCalculateCoverageEnabled());
        assertTrue(configuration.getTestSource() instanceof DumpTestSource);


        args = " -d http://dbpedia.org -s rdfs -f /home/rdfunit/ -C -c";
        commandLine = cliParser.parse(cliOptions, args.split(" "));
        configuration = ValidateUtils.getConfigurationFromArguments(commandLine);

        assertTrue(configuration.isManualTestsEnabled());
        assertFalse(configuration.isTestCacheEnabled());
        assertTrue(configuration.isCalculateCoverageEnabled());

        // Expect exception for wrong inputs
        HashMap<String, String> exceptionsExpected = new HashMap<>();
        exceptionsExpected.put(
                " -s rdf ",
                "Expected exception for missing -d");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -u http://custom.dbpedia.org ",
                "Expected exception for defining both -e & -u");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -l log",
                "Expected exception for asking unsupported -l");
        exceptionsExpected.put(
                " -d http://dbpedia.org -s foaf ",
                "Expected exception for asking for undefined 'foaf' schema ");
        exceptionsExpected.put(
                " -d http://dbpedia.org -s rdf -o htmln",
                "Expected exception for asking for undefined serialization 'htmln'");
        exceptionsExpected.put(
                " -d http://dbpedia.org -s rdf -o html,turtle123",
                "Expected exception for asking for undefined serialization 'turtle123'");

        // Manual / Auto test cases
        exceptionsExpected.put(
                " -d http://dbpedia.org -s rdf -M -A",
                "Expected exception for asking for excluding both manual & auto test cases");
        exceptionsExpected.put(
                " -d http://dbpedia.org -s rdf -A -C",
                "Expected exception for asking for excluding auto tests & specifying -C");

        // Limit / pagination / TTL / Delay
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -P 100",
                "Expected exception for asking for setting -P without an Endpoint");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -T 100",
                "Expected exception for asking for setting -T without an Endpoint");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -D 100",
                "Expected exception for asking for setting -D without an Endpoint");

        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -P asdf",
                "Expected exception for asking for setting non-numeric in -P");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -T asdf",
                "Expected exception for asking for setting non-numeric in -T");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -D asdf",
                "Expected exception for asking for setting non-numeric in -D");
        exceptionsExpected.put(
                " -d http://dbpedia.org -e http://dbpedia.org/sparql -s rdf -L asdf",
                "Expected exception for asking for setting non-numeric in -L");


        for (Map.Entry<String, String> entry : exceptionsExpected.entrySet()) {

            try {
                commandLine = cliParser.parse(cliOptions, entry.getKey().split(" "));
                configuration = ValidateUtils.getConfigurationFromArguments(commandLine);
                fail(entry.getValue());
            } catch (ParameterException e) {
                // Expected exception
                // Do nothing here
            }
        }


    }
}