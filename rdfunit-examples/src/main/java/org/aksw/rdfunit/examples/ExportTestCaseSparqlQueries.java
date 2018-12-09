package org.aksw.rdfunit.examples;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.tests.generators.RdfUnitTestGenerator;
import org.aksw.rdfunit.tests.generators.TestGeneratorFactory;
import org.aksw.rdfunit.tests.query_generation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class ExportTestCaseSparqlQueries {


    public static void main(String[] args) {

        checkArgument(args.length == 1, "Needs a file or a URL as input");


        SchemaSource schema = SchemaSourceFactory.createSchemaSourceSimple(args[0]);

        RDFUnit rdfUnit = RDFUnit.createWithOwlAndShacl();
        rdfUnit.init();

        RdfUnitTestGenerator testGenerator = TestGeneratorFactory.createAllNoCache(rdfUnit.getAutoGenerators(), "./");

        Collection<TestCase> tests = testGenerator.generate(schema).stream()
                .filter(tc -> TestCase.class.isAssignableFrom(tc.getClass()))
                .map(tc -> ((TestCase) tc))
                .collect(Collectors.toList());

        // You may choose only one of these depending on what type of queries you want to generate
        List<QueryGenerationFactory> SparqlGeneratorTypes = Arrays.asList(
                new QueryGenerationAskFactory(),
                new QueryGenerationCountFactory(),
                new QueryGenerationSelectFactory(),
                new QueryGenerationExtendedSelectFactory()
        );

        for (QueryGenerationFactory sparqlGenerator: SparqlGeneratorTypes)
            for (TestCase test: tests) {
                System.out.println(sparqlGenerator.getSparqlQueryAsString(test));
            }

    }
}
