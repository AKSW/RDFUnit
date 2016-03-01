package org.aksw.rdfunit.services;

import org.aksw.rdfunit.Resources;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class PrefixNSServiceTest {

    /* tests if the prefix map is setup correctly */
    @Test
    public void testGetPrefix() throws IOException {
        Model prefixModel = ModelFactory.createDefaultModel();
        try (InputStream is = org.aksw.rdfunit.services.PrefixNSService.class.getResourceAsStream(Resources.PREFIXES)) {
            prefixModel.read(is, null, "TURTLE");
        }
        // Update Prefix Service
        Map<String, String> prefixes = prefixModel.getNsPrefixMap();
        for (Map.Entry<String, String> entry : prefixes.entrySet()) {
            // All entries should match
            String uri = org.aksw.rdfunit.services.PrefixNSService.getNSFromPrefix(entry.getKey());
            Assert.assertEquals("All prefixed should be initialized", uri, entry.getValue());
        }
    }
}