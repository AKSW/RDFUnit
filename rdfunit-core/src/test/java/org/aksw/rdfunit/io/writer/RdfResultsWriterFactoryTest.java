package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
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
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RdfResultsWriterFactoryTest {

    @Parameterized.Parameters(name= "{index}: Result Type: {1}")
    public static Collection<Object[]> resources() throws RdfReaderException {
        //rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.aggregatedTestCaseResult.ttl
        Model aggregated = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.aggregatedTestCaseResult.ttl").read()).read();
        Model status = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.statusTestCaseResult.ttl").read()).read();
        Model shacl = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.shaclFullTestCaseResult.ttl").read()).read();
        Model shacllite = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.shaclSimpleTestCaseResult.ttl").read()).read();
        Model rlog = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.rlogTestCaseResult.ttl").read()).read();
        Model extended = new RdfModelReader(RdfReaderFactory.createResourceOrFileOrDereferenceReader("../rdfunit-model/src/test/resources/org/aksw/rdfunit/model/results/sample.extendedTestCaseResult.ttl").read()).read();

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
    public void testOutputWellFormed() throws RdfWriterException, IOException, SAXException {

        assertThat(inputModel.isEmpty()).isFalse();

        for (Resource testExecutionResource: inputModel.listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {
            TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

            validateHtml(getHtmlForExecution(testExecution));
            validateXml(getXmlForExecution(testExecution));
        }
    }

    private String getHtmlForExecution(TestExecution testExecution) throws RdfWriterException, UnsupportedEncodingException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        RdfResultsWriterFactory.createHtmlWriter(testExecution, os).write(ModelFactory.createDefaultModel());
        return os.toString("UTF8");
    }

    private String getXmlForExecution(TestExecution testExecution) throws RdfWriterException, UnsupportedEncodingException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        RdfResultsWriterFactory.createJunitXmlWriter(testExecution, os).write(ModelFactory.createDefaultModel());
        return os.toString("UTF8");
    }

    private void validateHtml(String html) {
        Tidy tidy = new Tidy();
        StringWriter writer = new StringWriter();
        tidy.parse(new StringReader(html), writer);
        assertThat(tidy.getParseErrors()).isZero();
        assertThat(tidy.getParseWarnings()).isZero();
    }

    private void validateXml(String xml) throws SAXException, IOException {

        String schemaFile = "/org/aksw/rdfunit/io/writer/junit-4.xsd";
        InputStream xsd = RdfResultsWriterFactoryTest.class.getResourceAsStream(schemaFile);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new StreamSource(xsd));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(new StringReader(xml)));
    }

}