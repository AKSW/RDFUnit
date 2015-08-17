package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.elements.interfaces.PatternParameter;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/18/15 12:33 AM
 */
public class PatternParameterReaderTest {

    @Test
    public void testRead() throws Exception {

        RDFReader reader = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/patterns.ttl");
        Model model = reader.read();

        // read all parameter definitions in pattern.ttl
        for (Resource resource: model.listResourcesWithProperty(RDF.type, RDFUNITv.Parameter).toList()) {
            PatternParameter patternParameter = PatternParameterReader.create().read(resource);
        }

    }
}