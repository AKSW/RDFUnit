package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.readers.PatternBatchReader;
import org.junit.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/28/15 5:33 PM
 */
public class PatternWriterTest {

    @Test
    public void testWrite() throws Exception {

        Model inputModel = RDFReaderFactory.createResourceReader(Resources.PATTERNS).read();

        Collection<Pattern> patterns = PatternBatchReader.create().getPatternsFromModel(inputModel);

        Model outputModel = ModelFactory.createDefaultModel();
        for (Pattern pattern: patterns) {
            PatternWriter.createPatternWriter(pattern).write(outputModel);
        }

        //Model difference = inputModel.difference(outputModel);
        //new RDFFileWriter("tmp_pattern.in.ttl", "TTL").write(inputModel);
        //new RDFFileWriter("tmp_pattern.out.ttl", "TTL").write(outputModel);
        //new RDFFileWriter("tmp_pattern.diff.ttl", "TTL").write(difference);

        assertThat(inputModel.isIsomorphicWith(outputModel)).isTrue();
    }
}