package org.aksw.rdfunit.model.readers.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.io.reader.RDFModelReader;
import org.aksw.rdfunit.io.reader.RDFReaderFactory;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TestExecutionReaderTest {


    @Parameterized.Parameters(name= "{index}: Result Type: {1}")
    public static Collection<Object[]> resources() throws Exception {

        Model aggregated = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.aggregatedTestCaseResult.ttl").read()).read();
        Model status = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.statusTestCaseResult.ttl").read()).read();
        Model shacl = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.shaclFullTestCaseResult.ttl").read()).read();
        Model shacllite = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.shaclSimpleTestCaseResult.ttl").read()).read();
        Model rlog = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.rlogTestCaseResult.ttl").read()).read();
        Model extended = new RDFModelReader(RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/model/results/sample.extendedTestCaseResult.ttl").read()).read();
        Model all = new RDFModelReader(aggregated.union(status).union(shacl).union(shacllite).union(rlog).union(extended)).read();

        return Arrays.asList(new Object[][] {
                { aggregated, "aggregated" },
                { status, "status" },
                { shacl, "shacl" },
                { shacllite, "shacl-lite" },
                { rlog, "rlog" },
                { extended, "extended" },
                { all, "all-together" },
        });
    }

    @Parameterized.Parameter
    public Model inputModel;

    @Parameterized.Parameter(value = 1)
    public String resultTypeName;


    @Test
    public void test() throws Exception {

        assertThat(inputModel.isEmpty()).isFalse();
        Model outputModel = ModelFactory.createDefaultModel();
        outputModel.setNsPrefixes(inputModel.getNsPrefixMap());

        for (Resource testExecutionResource: inputModel.listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {
            TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

            TestExecutionWriter writer = TestExecutionWriter.create(testExecution) ;
            writer.write(outputModel);
        }

        //new RDFFileWriter(resultTypeName).write(outputModel);

        Model difference = inputModel.difference(outputModel);
        //new RDFFileWriter("diff-" +resultTypeName).write(difference);

        //assertThat(inputModel.isIsomorphicWith(outputModel)).isTrue();

        assertThat(difference.isEmpty()).isTrue();






    }
}