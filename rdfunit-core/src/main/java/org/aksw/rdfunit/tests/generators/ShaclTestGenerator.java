package org.aksw.rdfunit.tests.generators;

import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.shacl.ShaclModel;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.Source;

import java.util.Set;

/**
 * @author Dimitris Kontokostas
 * @since 14/2/2016 4:45 μμ
 */

public class ShaclTestGenerator implements RdfUnitTestGenerator{

    @Override
    public Set<TestCase> generate(Source source) {
        if (! (source instanceof SchemaSource)) {
            throw new IllegalArgumentException("SHACL test generator expect a schema source as input");
        }
        try {
            ShaclModel shaclModel = new ShaclModel(((SchemaSource) (source)).getModel());
            return shaclModel.generateTestCases();

        } catch (RdfReaderException e) {
            throw new IllegalArgumentException( e);
        }

    }
}
