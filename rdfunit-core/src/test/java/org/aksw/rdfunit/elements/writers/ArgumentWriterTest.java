package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.elements.readers.ArgumentReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 12:42 AM
 */
@RunWith(Parameterized.class)
public class ArgumentWriterTest {

    @Parameterized.Parameters(name= "{index}: Pattern: {0}")
    public static Collection<Object[]> resources() throws Exception {
        Model model = RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/shacl/shacl.shacl.ttl").read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (RDFNode node: model.listObjectsOfProperty(SHACL.argument).toList()) {
            parameters.add(new Object[] {node});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;

    private Model model;

    @Test
    public void testRead() throws Exception {
        Argument argument = ArgumentReader.create().read(resource);
        Resource rs = ArgumentWriter.createArgumentWriter(argument).write();

        assertTrue(resource.equals(rs));
    }
}