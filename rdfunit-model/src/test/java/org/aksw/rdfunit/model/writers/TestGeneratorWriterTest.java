package org.aksw.rdfunit.model.writers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collection;
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

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/29/15 8:41 AM
 */
@RunWith(Parameterized.class)
public class TestGeneratorWriterTest {

  @Parameterized.Parameter
  public Model inputModel;
  @Parameterized.Parameter(value = 1)
  public String label;

  @Parameterized.Parameters(name = "{index}: Model: {1} ")
  public static Collection<Object[]> models() throws RdfReaderException {
    return Arrays.asList(new Object[][]{
        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_OWL).read(), "OWLGen"},
        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_RS).read(), "RSGen"},
        {RdfReaderFactory.createResourceReader(Resources.AUTO_GENERATORS_DSP).read(), "DSPGen"}
    });
  }

  @Before
  public void setUp() {
    // Needed to resolve the patterns
    RDFUnit
        .createWithAllGenerators()
        .init();
  }

  @Test
  public void testWrite() {
    Collection<TestGenerator> testCaseCollection = BatchTestGeneratorReader.create()
        .getTestGeneratorsFromModel(inputModel);

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