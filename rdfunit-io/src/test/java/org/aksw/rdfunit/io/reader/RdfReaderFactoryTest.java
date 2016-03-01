package org.aksw.rdfunit.io.reader;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/14/15 11:49 AM
 */
public class RdfReaderFactoryTest {



    @Test
    public void testCreateReaderFromText() throws IOException, RdfReaderException {

        URL url = Resources.getResource(this.getClass(),"/org/aksw/rdfunit/io/onetriple.ttl");
        String content = Resources.toString(url, Charsets.UTF_8);

        RdfReader reader = RdfReaderFactory.createReaderFromText(content, "TTL");

        Model model = reader.read();
        assertThat(model.isIsomorphicWith(ReaderTestUtils.createOneTripleModel())).isTrue();

        }
    }