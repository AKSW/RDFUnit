package org.aksw.rdfunit.tests.generators;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.shacl.ShaclModel;
import org.aksw.rdfunit.sources.SchemaSource;

import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

public class ShaclTestGenerator implements RdfUnitTestGenerator{

    @Override
    public Set<TestCase> generate(SchemaSource source) {
        try {
            ShaclModel shaclModel = new ShaclModel(source.getModel());
            return shaclModel.generateTestCases();

        } catch (RdfReaderException e) {
            throw new IllegalArgumentException( e);
        }

    }
}
