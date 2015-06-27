package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.elements.writers.ArgumentWriter;
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

        testIndividual("http://example.com/arguments/1");
        testIndividual("http://example.com/arguments/2");
        testIndividual("http://example.com/arguments/3");

    }

    /**
     * reads a resource IRI from a model, then writes it back to another model, re-read  and check equals with the first
     */
    private void testIndividual(String resourceUri) {

        // read IRI from model
        Resource resource = model.getResource(resourceUri);
        ArgumentReader argumentReader = ArgumentReader.createArgumentReader();
        Argument arg1 = argumentReader.read(resource);

        //now write it back to another model
        ArgumentWriter argumentWriter = ArgumentWriter.createArgumentWriter(arg1);
        Resource resourceWritten = argumentWriter.write();

        // read again
        Argument arg2 = argumentReader.read(resourceWritten);

        // check if first read equals the second
        assert (arg1.equals(arg2) );
    }
}