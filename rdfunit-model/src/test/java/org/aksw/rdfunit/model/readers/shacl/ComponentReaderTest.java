package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ComponentReaderTest {
    @Parameterized.Parameters(name= "{index}: Validator: {0}")
    public static Collection<Object[]> resources() throws RdfReaderException {
        Model model = RdfReaderFactory.createResourceReader(Resources.SHACL_CORE_CCs).read();
        Collection<Object[]> parameters = new ArrayList<>();
        for (Resource r: model.listSubjectsWithProperty(RDF.type, SHACL.ConstraintComponent).toList()) {
            parameters.add(new Object[] {r});
        }
        return parameters;
    }

    @Parameterized.Parameter
    public Resource resource;

    @Test
    public void testRead() {
        Component c = ComponentReader.create().read(resource);
        int i = 0;
    }
}