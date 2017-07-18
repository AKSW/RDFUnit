package org.aksw.rdfunit.tests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

import static com.google.common.collect.Streams.mapWithIndex;
import static com.google.common.collect.Streams.stream;
import static java.util.Comparator.comparing;
import static org.aksw.rdfunit.enums.TestCaseExecutionType.shaclFullTestCaseResult;

import io.vavr.control.Try;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.aksw.rdfunit.commons.RdfUnitModelFactory;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.aksw.rdfunit.vocabulary.SHACL_TEST;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.aksw.rdfunit.vocabulary.DATA_ACCESS_TESTS;
import org.topbraid.shacl.arq.SHACLPaths;
import org.topbraid.shacl.testcases.GraphValidationTestCaseType;
import org.topbraid.shacl.vocabulary.DASH;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.spin.util.JenaUtil;


import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 *
 * @author Markus Ackermann (including parts taken from Holger Knublauch)
 * @since 14/7/2017
 */


@RequiredArgsConstructor
@Slf4j
public class W3CShaclTestSuite {

    @RequiredArgsConstructor
    public static class TestCase {

        @Getter @NonNull public final Resource validateNode;

        @Getter @NonNull public final Manifest manifest;

        @Getter(lazy = true) @NonNull private final Resource action = findAction();

        @Getter(lazy = true) @NonNull private final String id = extractId();

        @Getter(lazy = true) @NonNull private final Try<TestExecution> execution = runShapes();

        @Getter(lazy = true) @NonNull private final Resource expectedValidationReport =
                extractExpectedValidationReport();

        @Getter(value = AccessLevel.PRIVATE, lazy = true) @NonNull private final Model adjustedExpectedReport =
                computeAdjustedExpectedReport();

        public Model getShapesGraph() {

           return modelForResourceValue(SHACL_TEST.shapesGraph);
        }

        public String getShapesGraphUri() {

            return getAction().getPropertyResourceValue(SHACL_TEST.shapesGraph).getURI();
        }

        public Model getDataGraph() {

            return modelForResourceValue(SHACL_TEST.dataGraph);
        }

        public String getDataGraphUri() {

            return getAction().getPropertyResourceValue(SHACL_TEST.dataGraph).getURI();
        }


        private Try<TestExecution> runShapes() {

            return Try.of(() -> {

                val shapesSource = SchemaSourceFactory.createSchemaSourceSimple(
                        getId(), getShapesGraphUri(),
                        new RdfModelReader(getDataGraph())
                );


                val testSuite = new TestSuite(new ShaclTestGenerator().generate(shapesSource));

                val testSource = new TestSourceBuilder()
                        .setImMemSingle()
                        .setInMemReader(new RdfModelReader(getDataGraph()))
                        .setPrefixUri(getId(), getDataGraphUri())
                        .setReferenceSchemata(SchemaSourceFactory.createSchemaSourceSimple(getShapesGraphUri()))
                        .build();

                return RDFUnitStaticValidator.validate(shaclFullTestCaseResult, getDataGraph(), testSuite);
            });
        }

        /**
         * This has been adopted with small adjustments from
         * {@code https://github.com/TopQuadrant/shacl/blob/master/src/main/java/org/topbraid/shacl/testcases/W3CTestRunner.java}
         */
        private Model adjustActualReport(Model actualReport) {

            val adjustedReport = RdfUnitModelFactory.createDefaultModel().add(actualReport);

            adjustedReport.removeAll(null, SHACL.message, (RDFNode)null);
            for(Statement s : adjustedReport.listStatements(null, SHACL.resultMessage, (RDFNode) null).toList()) {
                if(!getAdjustedExpectedReport().contains(null, SHACL.resultMessage, s.getObject())) {
                    adjustedReport.remove(s);
                }
            }

            return adjustedReport;
        }

        /**
         * This has been adopted with small adjustments from
         * {@code https://github.com/TopQuadrant/shacl/blob/master/src/main/java/org/topbraid/shacl/testcases/W3CTestRunner.java}
         */
        private Model computeAdjustedExpectedReport() {

            Model expectedModel = JenaUtil.createDefaultModel();

            for(Statement s : getExpectedValidationReport().listProperties().toList()) {
                expectedModel.add(s);
            }
            for(Statement s : getExpectedValidationReport().listProperties(SH.result).toList()) {
                for(Statement t : s.getResource().listProperties().toList()) {
                    if(t.getPredicate().equals(DASH.suggestion)) {
                        GraphValidationTestCaseType.addStatements(expectedModel, t);
                    }
                    else if(SH.resultPath.equals(t.getPredicate())) {
                        expectedModel.add(t.getSubject(), t.getPredicate(),
                                SHACLPaths.clonePath(t.getResource(), expectedModel));
                    }
                    else {
                        expectedModel.add(t);
                    }
                }
            }

            return expectedModel;
        }

        private Resource findAction() {

            if(stream(validateNode.listProperties(DATA_ACCESS_TESTS.action)).count() != 1) {

                throw new RuntimeException("multiple or no actions");
            }

            return validateNode.getPropertyResourceValue(DATA_ACCESS_TESTS.action);
        }

        @SneakyThrows(URISyntaxException.class)
        private String extractId() {

            val testIRI = new URI(validateNode.getURI());

            val pathSegments = Arrays.asList(testIRI.getPath().split("/"));

            return pathSegments.get(pathSegments.size() - 1);
        }

        private Resource extractExpectedValidationReport() {

            return validateNode.getPropertyResourceValue(DATA_ACCESS_TESTS.result);
        }



        private Model modelForResourceValue(Property property) {

            val graphResource = getAction().getPropertyResourceValue(property);

            val graphPath = getManifest().resolvePathURI(graphResource.getURI());

            return JenaUtils.readModel(graphPath.toUri());
        }
    }

    @RequiredArgsConstructor
    public static class Manifest {

        @Getter @NonNull private final Path sourceFile;

        @Getter(lazy = true) @NonNull private final Model model = JenaUtils.readModel(sourceFile.toUri());

        @Getter(lazy = true) @NonNull private final Resource manifestResource = findManifestResource();

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        public Stream<Manifest> getIncludes() {

            return stream(getManifestResource().listProperties(DATA_ACCESS_TESTS.include))
                    .map(stmt -> resolvePathURI(stmt.getResource().getURI()))
                    .sorted()
                    .map(path -> new Manifest(path));
        }

        public Stream<TestCase> getTestCases() {

            return stream(getManifestResource().listProperties(DATA_ACCESS_TESTS.entries))
                    .flatMap(stmt -> Streams.stream(stmt.getObject().as(RDFList.class).iterator()))
                    .filter(Resource.class::isInstance).map(Resource.class::cast)
                    .sorted(comparing(res -> res.getURI()))
                    .map(res -> new TestCase(res, this));
        }

        public Stream<TestCase> getTestCasesRecursive() {

            return Stream.concat(getTestCases(), getIncludes().flatMap(m -> m.getTestCasesRecursive()));
        }

        private Resource findManifestResource() {

            val manifestTypeStmts = getModel().listStatements(null, RDF.type, DATA_ACCESS_TESTS.Manifest).toList();

            if(manifestTypeStmts.size() != 1) {
                throw new RuntimeException("no Manifest resource");
            }

            return manifestTypeStmts.get(0).getSubject();
        }

        @SneakyThrows(URISyntaxException.class)
        private Path resolvePathURI(String includeURI) {

            val includePath = Paths.get(new URI(includeURI));

            if(includePath.isAbsolute()) {
                return  includePath;
            } else {
                // sht:dataGraph <> => /manifest/path/manifest.ttl
                // sht:dataGraph <shared-data.ttl> => /manifest/path/shared-data.ttl
                return includeURI.isEmpty()? getSourceFile() : getSourceFile().getParent().resolve(includePath);
            }
        }
    }

    @Getter @NonNull @Singular ImmutableList<TestCase> testCases;


    public static W3CShaclTestSuite load(Path rootManifest, boolean skipMarkedTests) {

        ImmutableList<TestCase> tests = new Manifest(rootManifest).getTestCasesRecursive()
                .collect(ImmutableList.toImmutableList());

        return new W3CShaclTestSuite(tests);
    }

    public static void main(String[] args) {

        val rootManifestPath = Paths.get("/home/mack/projects/ALIGNED/RDFUnit/data-shapes-repo/" +
                "data-shapes-test-suite/tests/manifest.ttl");

        log.info("{} test cases found.", new Manifest(rootManifestPath).getTestCasesRecursive().count());

        val suite = W3CShaclTestSuite.load(rootManifestPath, false);

        suite.getTestCases().parallelStream().forEach(tc -> tc.getExecution());

        val failureCount = suite.getTestCases().stream().filter(t -> t.getExecution().isFailure()).count();

        log.info("{} tests had failures.", failureCount);
    }
}
