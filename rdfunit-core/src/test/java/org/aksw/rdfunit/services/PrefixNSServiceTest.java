package org.aksw.rdfunit.services;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.junit.Test;

import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PrefixNSServiceTest {

    /* tests if the prefix map is setup correctly */
    @Test
    public void testGetPrefix() throws Exception {
        Model prefixModel = ModelFactory.createDefaultModel();
        try (InputStream is = PrefixNSService.class.getResourceAsStream("/org/aksw/rdfunit/prefixes.ttl");) {
            prefixModel.read(is, null, "TURTLE");
        } catch (Exception e) {
            throw new RuntimeException("Cannot init prefixes");
        }
        // Update Prefix Service
        Map<String, String> prefixes = prefixModel.getNsPrefixMap();
        for (Map.Entry<String, String> entry : prefixes.entrySet()) {
            // All entries should match
            String uri = PrefixNSService.getNSFromPrefix(entry.getKey());
            assertEquals("All prefixed should be initialized", uri, entry.getValue());
        }

        // The size should be the same
        assertEquals("mismatch in size of prefixes", prefixes.size(), PrefixNSService.createPrefixNsBidiMap().size());
    }
}