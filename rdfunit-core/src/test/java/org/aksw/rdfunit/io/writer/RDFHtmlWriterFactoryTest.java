package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.readers.results.TestExecutionReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RDFHtmlWriterFactoryTest {

    @Parameterized.Parameters(name= "{index}: Result Type: {1}")
    public static Collection<Object[]> resources() throws Exception {
        //rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.aggregatedTestCaseResult.ttl
        Model aggregated = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.aggregatedTestCaseResult.ttl").read()).read();
        Model status = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.statusTestCaseResult.ttl").read()).read();
        Model shacl = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.shaclFullTestCaseResult.ttl").read()).read();
        Model shacllite = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.shaclSimpleTestCaseResult.ttl").read()).read();
        Model rlog = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.rlogTestCaseResult.ttl").read()).read();
        Model extended = new RDFModelReader(RDFReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.extendedTestCaseResult.ttl").read()).read();

        return Arrays.asList(new Object[][] {
                { aggregated, "aggregated" },
                { status, "status" },
                { shacl, "shacl" },
                { shacllite, "shacl-lite" },
                { rlog, "rlog" },
                { extended, "extended" },
        });
    }

    @Parameterized.Parameter
    public Model inputModel;

    @Parameterized.Parameter(value = 1)
    public String inputName;

    @Test
    public void testOutputWellFormed() throws Exception {

        assertThat(inputModel.isEmpty()).isFalse();

        for (Resource testExecutionResource: inputModel.listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {
            TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

            validateHtml(getHtmlForExecution(testExecution));


        }
    }

    private String getHtmlForExecution(TestExecution testExecution) throws Exception {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        RDFHtmlWriterFactory.createHTMLWriter(testExecution, os).write(ModelFactory.createDefaultModel());
        return os.toString("UTF8");
    }

    private void validateHtml(String html) {
        Tidy tidy = new Tidy();
        StringWriter writer = new StringWriter();
        tidy.parse(new StringReader(html), writer);
        assertThat(tidy.getParseErrors()).isZero();
        assertThat(tidy.getParseWarnings()).isZero();
    }
}