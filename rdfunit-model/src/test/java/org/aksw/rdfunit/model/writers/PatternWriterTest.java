package org.aksw.rdfunit.model.writers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.readers.BatchPatternReader;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Test;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/28/15 5:33 PM
 */
public class PatternWriterTest {

  @Test
  public void testWrite() throws RdfReaderException {

    Model inputModel = RdfReaderFactory.createResourceReader(Resources.PATTERNS).read();

    Collection<Pattern> patterns = BatchPatternReader.create().getPatternsFromModel(inputModel);

    Model outputModel = ModelFactory.createDefaultModel();
    for (Pattern pattern : patterns) {
      PatternWriter.create(pattern).write(outputModel);
    }

    //Model difference = inputModel.difference(outputModel);
    //new RdfFileWriter("tmp_pattern.in.ttl", "TTL").write(inputModel);
    //new RdfFileWriter("tmp_pattern.out.ttl", "TTL").write(outputModel);
    //new RdfFileWriter("tmp_pattern.diff.ttl", "TTL").write(difference);

    assertThat(inputModel.isIsomorphicWith(outputModel)).isTrue();
  }
}