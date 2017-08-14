package org.aksw.rdfunit.tests;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.sources.TestSourceBuilder;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.vocabulary.DATA_ACCESS_TESTS;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.aksw.rdfunit.vocabulary.SHACL_TEST;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.EARL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.arq.SHACLPaths;
import org.topbraid.shacl.testcases.GraphValidationTestCaseType;
import org.topbraid.spin.util.JenaUtil;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.stream;
import static java.util.Comparator.comparing;
import static org.aksw.rdfunit.enums.TestCaseExecutionType.shaclFullTestCaseResult;

/**
 *
 * @author Markus Ackermann (including parts taken from Holger Knublauch)
 * @since 14/7/2017
 */

@RequiredArgsConstructor
@Slf4j
public class W3CShaclTestSuite {

    private static final Resource PartialResult = ResourceFactory.createResource("http://www.w3.org/ns/shacl-test#partial");

    @RequiredArgsConstructor
    public static class TestCase {

        @Getter @NonNull public final Resource validateNode;

        @Getter @NonNull public final Manifest manifest;

        @Getter(lazy = true) @NonNull private final Resource action = findAction();

        @Getter(lazy = true) @NonNull private final String id = extractId();

        @Getter(lazy = true) @NonNull private final Try<TestExecution> execution = runShapes();

        @Getter(lazy = true) @NonNull private final Resource expectedValidationReport =
                extractExpectedValidationReport();

        @Getter(lazy = true) @NonNull private final Resource earlOutcome = computeOutcome();

        @Getter(value = AccessLevel.PRIVATE, lazy = true) @NonNull private final Model adjustedExpectedReport =
                computeAdjustedExpectedReport();

        private static final Property dashSuggestionProp =
                ResourceFactory.createProperty("http://datashapes.org/dash#suggestion");

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

            val adjustedReport = ModelFactory.createDefaultModel();

            //remove RDFUnit types
            val keepOnlyTypes = ImmutableSet.of(SHACL.ValidationReport.getURI(), SHACL.ValidationResult.getURI());
            val keepOnlyPredicates = ImmutableSet.of(SHACL.result, SHACL.conforms,
                                                        SHACL.focusNode,
                                                        SHACL.resultPath,
                                                        SHACL.resultSeverity,
                                                        SHACL.sourceConstraint,
                                                        SHACL.sourceConstraintComponent,
                                                        SHACL.sourceShape,
                                                        SHACL.value,
                                                        RDF.first,
                                                        RDF.nil,
                                                        RDF.rest,
                                                        SHACL.inversePath,
                                                        SHACL.zeroOrMorePath,
                                                        SHACL.oneOrMorePath,
                                                        SHACL.zeroOrOnePath,
                                                        SHACL.alternativePath).stream().map(Resource::getURI).collect(Collectors.toSet());

            actualReport.listStatements().forEachRemaining(s -> {
                if (keepOnlyPredicates.contains(s.getPredicate().getURI())) {
                    adjustedReport.add(s);
                }
                if (RDF.type.equals(s.getPredicate())) {
                    if (keepOnlyTypes.contains(s.getObject().toString())) {
                        adjustedReport.add(s);
                    }
                }
            });

            // convert to blank nodes
            Set<Statement> statementsForRemoval = new HashSet<>();
            adjustedReport.listObjectsOfProperty(SHACL.result).forEachRemaining( n -> {
                if (n.isResource()) {
                    Resource r = n.asResource();
                    Resource newBNode = ResourceFactory.createResource();
                    adjustedReport.listStatements(r, null, (RDFNode)null).forEachRemaining( s-> {
                        adjustedReport.add(newBNode, s.getPredicate(), s.getObject());
                        statementsForRemoval.add(s);
                    });
                    adjustedReport.listStatements(null, null, r).forEachRemaining( s-> {
                        adjustedReport.add(s.getSubject(), s.getPredicate(), newBNode);
                        statementsForRemoval.add(s);
                    });
                }
            });
            statementsForRemoval.forEach(adjustedReport::remove);
            statementsForRemoval.clear();
            adjustedReport.listSubjectsWithProperty(RDF.type, SHACL.ValidationReport).forEachRemaining( r-> {
                Resource newBNode = ResourceFactory.createResource();
                adjustedReport.listStatements(r, null, (RDFNode)null).forEachRemaining( sn-> {
                    adjustedReport.add(newBNode, sn.getPredicate(), sn.getObject());
                    statementsForRemoval.add(sn);
                });
            });

            statementsForRemoval.forEach(adjustedReport::remove);

            adjustedReport.removeAll(null, SHACL.message, null);

            if (!getAdjustedExpectedReport().contains(null, SHACL.resultMessage, (RDFNode)null)) {
                for(Statement s : adjustedReport.listStatements(null, SHACL.resultMessage, (RDFNode) null).toList()) {
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
            for(Statement s : getExpectedValidationReport().listProperties(SHACL.result).toList()) {
                for(Statement t : s.getResource().listProperties().toList()) {
                    if(t.getPredicate().equals(dashSuggestionProp)) {
                        GraphValidationTestCaseType.addStatements(expectedModel, t);
                    }
                    else if(SHACL.resultPath.equals(t.getPredicate())) {
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

        private Resource computeOutcome() {

            if (getExecution().isFailure()) {
                // expected error
                if (getAdjustedExpectedReport().isEmpty()) {
                    return EARL.passed;
                }
                log.error("test case raised an error during execution: {}", getId());
                log.error("error was: ", getExecution().failed().get());

                return EARL.failed;
            }

            final Model originalActualReport = ModelFactory.createDefaultModel();
            TestExecutionWriter.create(getExecution().get()).write(originalActualReport);
            final Model adjustedActualReport = this.adjustActualReport(originalActualReport);

            val isIsomorphic = getAdjustedExpectedReport().isIsomorphicWith(adjustedActualReport);



                if (isIsomorphic) {
                    return EARL.passed;
                } else {
                    // check for partial

                    int expectedViolations = getAdjustedExpectedReport().listSubjectsWithProperty(RDF.type, SHACL.ValidationResult).toList().size();
                    int actualViolations = adjustedActualReport.listSubjectsWithProperty(RDF.type, SHACL.ValidationResult).toList().size();
                    String file = this.manifest.sourceFile.toString().replace("/", "_");
                    file = file.substring(file.lastIndexOf("tests")+6);
                    File dirs1 = new File("results/partial");
                    dirs1.mkdirs();
                    File dirs2 = new File("results/failed");
                    dirs2.mkdirs();

                    if (
                            (!getAdjustedExpectedReport().isEmpty() && (
                                    expectedViolations == 0 && actualViolations == 0 )
                            || (expectedViolations > 0 && actualViolations > 0 )))
                    {
                        try {
                            new RdfFileWriter("results/partial/" + file + ".expected.ttl").write(getAdjustedExpectedReport());
                            new RdfFileWriter("results/partial/" + file + ".actual.before.ttl").write(originalActualReport);
                            new RdfFileWriter("results/partial/" + file + ".actual.ttl").write(adjustedActualReport);
                        } catch (RdfWriterException e) {
                            e.printStackTrace();
                        }
                        return W3CShaclTestSuite.PartialResult;
                    }

                    try {
                        new RdfFileWriter("results/fail/" + file + ".expected.ttl").write(getAdjustedExpectedReport());
                        //new RdfFileWriter("results/fail/" +this.getId() + ".actual.before.ttl").write(originalActualReport);
                        new RdfFileWriter("results/fail/" + file + ".actual.ttl").write(adjustedActualReport);

                    } catch (RdfWriterException e) {
                        e.printStackTrace();
                    }

                    log.error("test case failed {}", this.getManifest().sourceFile);
                    return EARL.Fail;
                }


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

            java.util.List<Statement> manifestTypeStmts =
                    getModel().listStatements(null, RDF.type, DATA_ACCESS_TESTS.Manifest).toList();

            if(manifestTypeStmts.size() != 1) {
                throw new RuntimeException("no Manifest resource or ambiguity due to several Manifests");
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

        val rootManifestPath = Paths.get("tests/manifest.ttl");

        log.info("{} test cases found.", new Manifest(rootManifestPath).getTestCasesRecursive().count());

        val suite = W3CShaclTestSuite.load(rootManifestPath, false);

        suite.getTestCases().parallelStream().forEach(tc -> tc.getExecution());

        val failureCount = suite.getTestCases().stream().filter(t -> t.getExecution().isFailure()).count();

        log.info("{} tests had failures.", failureCount);

        val hist = List.ofAll(suite.getTestCases()).groupBy(tc -> tc.getEarlOutcome()).mapValues(l -> l.size());

        log.info("EARL outcome histogram: {}", hist);
    }
}
