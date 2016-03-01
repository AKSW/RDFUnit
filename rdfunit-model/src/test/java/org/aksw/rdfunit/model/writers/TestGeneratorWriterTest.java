package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.model.readers.BatchTestGeneratorReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/29/15 8:41 AM
 */
@RunWith(Parameterized.class)
public class TestGeneratorWriterTest {

    @Before
    public void setUp() throws RdfReaderException {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }


    @Parameterized.Parameters(name= "{index}: Model: {1} ")
    public static Collection<Object[]> models() throws RdfReaderException {
        return Arrays.asList( new Object[][]{
                        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_OWL).read(), "OWLGen"},
                        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_RS).read(), "RSGen"},
                        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_DSP).read(), "DSPGen"}
                });
    }

    @Parameterized.Parameter
    public Model inputModel;
    @Parameterized.Parameter(value=1)
    public String label;

    @Test
    public void testWrite() {
        Collection<TestGenerator> testCaseCollection = BatchTestGeneratorReader.create().getTestGeneratorsFromModel(inputModel);

        Model modelWritten = ModelFactory.createDefaultModel();
        for (TestGenerator tg : testCaseCollection) {
            TestGeneratorWriter.create(tg).write(modelWritten);
        }

        // See the difference...
        //Model difference = inputModel.difference(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".in.ttl", "TTL").write(inputModel);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".out.ttl", "TTL").write(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".diff.ttl", "TTL").write(difference);

        assertThat(inputModel.isIsomorphicWith(modelWritten)).isTrue();
    }
}