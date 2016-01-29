package org.aksw.rdfunit.io.reader;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/14/15 11:49 AM
 */
public class RDFReaderFactoryTest {

    @Test
    public void testCreateFileOrDereferenceReader() throws Exception {

    }

    @Test
    public void testCreateResourceReader() throws Exception {

    }

    @Test
    public void testCreateFileOrResourceReader() throws Exception {

    }

    @Test
    public void testCreateDereferenceReader() throws Exception {

    }

    @Test
    public void testCreateReaderFromText() throws Exception {

        URL url = Resources.getResource(this.getClass(),"/org/aksw/rdfunit/io/onetriple.ttl");
        String content = Resources.toString(url, Charsets.UTF_8);

        RDFReader reader = RDFReaderFactory.createReaderFromText(content, "TTL");

        Model model = reader.read();
        assertThat(model.isIsomorphicWith(ReaderTestUtils.createOneTripleModel())).isTrue();

        }
    }