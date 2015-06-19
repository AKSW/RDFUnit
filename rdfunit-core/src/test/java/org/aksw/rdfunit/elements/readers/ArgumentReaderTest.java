package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.reader.RDFReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:11 PM
 */
public class ArgumentReaderTest {

    private Model model;

    @Before
    public void setUp() throws Exception {
        RDFReader reader = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/data/readers/FunctionArgument/functionArgument.example1.ttl");
        model = reader.read();
    }

    @Test
    public void testRead() throws Exception {
        throw new Exception("not implemented");
    }
}