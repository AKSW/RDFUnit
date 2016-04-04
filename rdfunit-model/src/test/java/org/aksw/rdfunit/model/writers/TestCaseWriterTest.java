package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.readers.BatchTestCaseReader;
import org.aksw.rdfunit.resources.ManualTestResources;
import org.aksw.rdfunit.utils.UriToPathUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/28/15 12:00 PM
 */
@RunWith(Parameterized.class)
public class TestCaseWriterTest {

    @Parameterized.Parameter
    public Model inputModel;
    @Parameterized.Parameter(value=1)
    public String label;


    @Parameterized.Parameters(name= "{index}: Model: {1} ")
    public static Collection<Object[]> models() {

        Collection<Object[]> models = new ArrayList<>();

        for (Map.Entry<String, String> entry : ManualTestResources.getInstance().entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            String resource = "/org/aksw/rdfunit/tests/" + "Manual/" + UriToPathUtils.getCacheFolderForURI(uri) + prefix + "." + "tests" + "." + "Manual" + ".ttl";
            try {
                models.add(new Object[] {RdfReaderFactory.createResourceReader(resource).read(), resource});
            } catch (RdfReaderException e) {
                throw new IllegalArgumentException("Cannot read resource: " + resource + " (" + prefix + " - " + uri + ")", e);
            }
        }
        return models;
    }


    @Before
    public void setUp() throws RdfReaderException {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }



    @Test
    public void testWrite() {
        Collection<TestCase> testCaseCollection = BatchTestCaseReader.create().getTestCasesFromModel(inputModel);

        Model modelWritten = ModelFactory.createDefaultModel();
        for (TestCase tc : testCaseCollection) {
            TestCaseWriter.create(tc).write(modelWritten);
        }

        // See the difference...
        //Model difference = inputModel.difference(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".in.ttl", "TTL").write(inputModel);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".out.ttl", "TTL").write(modelWritten);
        //new RDFFileWriter("tmp" + label.replace("/", "_") + ".diff.ttl", "TTL").write(difference);

        assertThat(inputModel.isIsomorphicWith(modelWritten)).isTrue();
    }
}