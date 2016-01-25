package org.aksw.rdfunit.dqv;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.io.reader.RDFStreamReader;
import org.aksw.rdfunit.io.writer.RDFFileWriter;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.readers.results.TestExecutionReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 21/1/2016 9:28 μμ
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Model testExecutionReader = new RDFStreamReader("/home/jim/work/code/java/Databugger/data/results/dbpedia.org_resource_Thessaloniki.shaclFullTestCaseResult.ttl").read();

        for (Resource testExecutionResource: testExecutionReader.listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {
            TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

            Collection<QualityMeasure> report = new DqvReport(testExecution).getQualityMeasures();

            Model model = ModelFactory.createDefaultModel();
            DqvReportWriter.create(report).write(model);

            new RDFFileWriter(testExecution.getTestExecutionUri()).write(model);

        }
    }
}
