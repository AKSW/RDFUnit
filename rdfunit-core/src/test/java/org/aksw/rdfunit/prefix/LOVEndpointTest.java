package org.aksw.rdfunit.prefix;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class LOVEndpointTest {

  @Ignore
  @Test
  // please note, you could enable this test, but it takes some seconds and might fail due to reachability of the resources
  // since this was created for testing the LOVEndpoint behaviour, use it when making changes to it (also its fine if it finds a different serialization)
  public void testExtractResourceLocation() {
    LOVEndpoint endpoint = new LOVEndpoint();

    SchemaEntry entry5 = new SchemaEntry("agr", "https://promsns.org/def/agr",
        "http://promsns.org/def/agr#");
    SchemaEntry result5 = endpoint.extractResourceLocation(entry5);
    Assert.assertEquals("https://promsns.org/def/agr/agr.ttl", result5.getVocabularyDefinedBy());

    SchemaEntry entry1 = new SchemaEntry("SAN",
        "http://www.irit.fr/recherches/MELODI/ontologies/SAN",
        "http://www.irit.fr/recherches/MELODI/ontologies/SAN#",
        "http://www.irit.fr/recherches/MELODI/ontologies/SAN#");
    SchemaEntry result1 = endpoint.extractResourceLocation(entry1);
    Assert.assertEquals(entry1.getVocabularyURI().replace("http://", "https://") + ".owl",
        result1.getVocabularyDefinedBy());

    SchemaEntry entry9 = new SchemaEntry("medred", "http://w3id.org/medred/medred",
        "http://w3id.org/medred/medred#");
    SchemaEntry result9 = endpoint.extractResourceLocation(entry9);
    Assert.assertEquals("https://jpcik.github.io/medred/onto/medred/ontology.n3",
        result9.getVocabularyDefinedBy());

    SchemaEntry entry8 = new SchemaEntry("comm", "http://vocab.resc.info/communication",
        "http://vocab.resc.info/communication#");
    SchemaEntry result8 = endpoint.extractResourceLocation(entry8);
    Assert
        .assertEquals("http://vocab.resc.info/communication.rdf", result8.getVocabularyDefinedBy());

    SchemaEntry entry3 = new SchemaEntry("af", "http://purl.org/ontology/af/",
        "http://purl.org/ontology/af/", "http://purl.org/ontology/af/");
    SchemaEntry result3 = endpoint.extractResourceLocation(entry3);
    Assert.assertEquals("http://motools.sourceforge.net/doc/audio_features.rdf",
        result3.getVocabularyDefinedBy());

    SchemaEntry entry7 = new SchemaEntry("ssn", "http://www.w3.org/2005/Incubator/ssn/ssnx/ssn",
        "http://purl.oclc.org/NET/ssnx/ssn#");
    SchemaEntry result7 = endpoint.extractResourceLocation(entry7);
    Assert.assertEquals("https://www.w3.org/2005/Incubator/ssn/ssnx/ssn.owl",
        result7.getVocabularyDefinedBy());

    SchemaEntry entry6 = new SchemaEntry("agrelon", "http://d-nb.info/standards/elementset/agrelon",
        "http://d-nb.info/standards/elementset/agrelon#");
    SchemaEntry result6 = endpoint.extractResourceLocation(entry6);
    Assert.assertEquals("https://d-nb.info/standards/elementset/agrelon.rdf",
        result6.getVocabularyDefinedBy());

    SchemaEntry entry4 = new SchemaEntry("brk", "http://brk.basisregistraties.overheid.nl/def/brk",
        "http://brk.basisregistraties.overheid.nl/def/brk#");
    SchemaEntry result4 = endpoint.extractResourceLocation(entry4);
    Assert.assertEquals("https://brk.basisregistraties.overheid.nl/def/brk.owl",
        result4.getVocabularyDefinedBy());

    SchemaEntry entry2 = new SchemaEntry("acco", "http://purl.org/acco/ns",
        "http://purl.org/acco/ns#", "http://purl.org/acco/ns#");
    SchemaEntry result2 = endpoint.extractResourceLocation(entry2);
    Assert.assertEquals("http://ontologies.sti-innsbruck.at/acco/ns.owl",
        result2.getVocabularyDefinedBy());

  }
}