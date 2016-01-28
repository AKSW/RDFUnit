package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RDFReaderException;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.readers.BatchTestCaseReader;
import org.aksw.rdfunit.resources.ManualTestResources;
import org.aksw.rdfunit.utils.UriToPathUtils;
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

    @Before
    public void setUp() throws Exception {
        // Needed to resolve the patterns
        RDFUnit rdfUnit = new RDFUnit();
        rdfUnit.init();
    }


    @Parameterized.Parameters(name= "{index}: Model: {1} ")
    public static Collection<Object[]> models() throws Exception {

        Collection<Object[]> models = new ArrayList<>();

        for (Map.Entry<String, String> entry : ManualTestResources.getInstance().entrySet()) {
            String prefix = entry.getKey();
            String uri = entry.getValue();
            String resource = "/org/aksw/rdfunit/tests/" + "Manual/" + UriToPathUtils.getCacheFolderForURI(uri) + prefix + "." + "tests" + "." + "Manual" + ".ttl";
            try {
                models.add(new Object[] {RDFReaderFactory.createResourceReader(resource).read(), resource});
            } catch (RDFReaderException e) {
                throw new RuntimeException("Cannot read resource: " + resource + " (" + prefix + " - " + uri + ")");
            }
        }
        return models;
    }

    @Parameterized.Parameter
    public Model inputModel;
    @Parameterized.Parameter(value=1)
    public String label;

    @Test
    public void testWrite() throws Exception {
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