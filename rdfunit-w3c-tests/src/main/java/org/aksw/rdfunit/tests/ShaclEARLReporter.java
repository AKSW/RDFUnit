package org.aksw.rdfunit.tests;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.commons.RdfUnitModelFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.vocabulary.DOAP;
import org.apache.jena.sparql.vocabulary.EARL;
import org.apache.jena.vocabulary.RDF;

import java.nio.file.Paths;

/**
 * Created by Markus Ackermann.
 * No rights reserved.
 */

@Builder
@Slf4j
public class ShaclEARLReporter {

    @Getter @NonNull private final String authorUri;

    @Getter @NonNull private final String subjectUri;

    @Getter @NonNull private final String projectName;

    @Getter @NonNull private final W3CShaclTestSuite testSuite;

    @Getter(lazy = true) @NonNull private final Resource author = ResourceFactory.createResource(authorUri);

    @Getter(lazy = true) @NonNull private final Resource testSubject = ResourceFactory.createResource(subjectUri);

    @Getter(lazy = true) @NonNull private final Model reportModel = generateReport();



    private Model generateReport() {

        val report =  RdfUnitModelFactory.createDefaultModel();
        report.setNsPrefix("earl", "http://www.w3.org/ns/earl#");

        report.add(getTestSubject(), RDF.type, DOAP.Project);
        report.add(getTestSubject(), RDF.type, EARL.Software);
        report.add(getTestSubject(), RDF.type, EARL.TestSubject);
        report.add(getTestSubject(), DOAP.developer, getAuthor());
        report.add(getTestSubject(), DOAP.name, projectName);

         getTestSuite().getTestCases().stream().forEach(test -> {

            val assertion = report.createResource();
            assertion.addProperty(RDF.type, EARL.Assertion);
            assertion.addProperty(EARL.assertedBy, getAuthorUri());
            assertion.addProperty(EARL.subject, getSubjectUri());

            val testUri = report.createResource("urn:x-shacl-test:" + test.getId());
            assertion.addProperty(EARL.test, testUri);

            val result = report.createResource();
            result.addProperty(RDF.type, EARL.TestResult);
            result.addProperty(EARL.mode, EARL.automatic);
            result.addProperty(EARL.outcome, test.getEarlOutcome());
            assertion.addProperty(EARL.result, result);

         });

        return report;
    }


    public static void main(String[] args) {

        val rootManifestPath = Paths.get("/home/mack/projects/ALIGNED/RDFUnit/data-shapes-repo/" +
                "data-shapes-test-suite/tests/manifest.ttl");

        val suite = W3CShaclTestSuite.load(rootManifestPath, false);


        val reporter = ShaclEARLReporter.builder()
                .authorUri("http://aksw.org/DimitrisKontokostas")
                .subjectUri("http://aksw.org/projects/RDFUnit")
                .projectName("RDFUnit")
                .testSuite(suite).build();

        val reportModel = reporter.generateReport();

        reportModel.write(System.out);
    }
}
