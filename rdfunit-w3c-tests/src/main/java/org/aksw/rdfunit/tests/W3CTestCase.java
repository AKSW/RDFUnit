package org.aksw.rdfunit.tests;


import com.google.common.collect.ImmutableSet;
import io.vavr.control.Try;
import lombok.*;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.io.writer.RdfStreamWriter;
import org.aksw.rdfunit.io.writer.RdfWriterException;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.readers.shacl.ShapePathReader;
import org.aksw.rdfunit.model.writers.results.TestExecutionWriter;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.aksw.rdfunit.vocabulary.DATA_ACCESS_TESTS;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.aksw.rdfunit.vocabulary.SHACL_TEST;
import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.vocabulary.EARL;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.testcases.GraphValidationTestCaseType;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Streams.stream;
import static org.aksw.rdfunit.enums.TestCaseExecutionType.shaclTestCaseResult;

@RequiredArgsConstructor
@Slf4j
public class W3CTestCase {

    @Getter @NonNull public final Resource validateNode;

    @Getter @NonNull public final W3CTestManifest manifest;

    @Getter(lazy = true) @NonNull private final Resource action = findAction();

    @Getter(lazy = true) @NonNull private final String id = extractId();

    @Getter(lazy = true) @NonNull private final Try<TestExecution> execution = runShapes();

    @Getter(lazy = true) @NonNull private final Resource expectedValidationReport =
            extractExpectedValidationReport();

    @Getter(value = AccessLevel.PRIVATE, lazy = true) @NonNull private final Model adjustedExpectedReport =
            computeAdjustedExpectedReport();

    private final Property dashSuggestionProp =
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
                    new RdfModelReader(getShapesGraph())
            );

            Set<GenericTestCase> tests = ImmutableSet.copyOf(new ShaclTestGenerator().generate(shapesSource));
            val testSuite = new TestSuite(tests);

            return RDFUnitStaticValidator.validate(shaclTestCaseResult, getDataGraph(), testSuite);
        });
    }

    /**
     * This has been adopted with small adjustments from
     * {@code https://github.com/TopQuadrant/shacl/blob/master/src/main/java/org/topbraid/shacl/testcases/W3CTestRunner.java}
     */
    private Model adjustActualReport(Model actualReport) {

        val adjustedReport = ModelFactory.createDefaultModel();

        val details = actualReport.listObjectsOfProperty(SHACL.detail).toList();

        //remove RDFUnit types
        val keepOnlyTypes = ImmutableSet.of(SHACL.ValidationReport.getURI(), SHACL.ValidationResult.getURI());
        val keepOnlyPredicates = ImmutableSet.of(SHACL.result, SHACL.conforms,
                SHACL.focusNode,
                SHACL.resultPath,
                SHACL.resultSeverity,
                SHACL.resultMessage,
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
            if(! details.contains(s.getSubject())) {
                if (keepOnlyPredicates.contains(s.getPredicate().getURI())) {
                    adjustedReport.add(s);
                }
                if (RDF.type.equals(s.getPredicate())) {
                    if (keepOnlyTypes.contains(s.getObject().toString())) {
                        adjustedReport.add(s);
                    }
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

        Model expectedModel = ModelFactory.createDefaultModel();

        for(Statement s : getExpectedValidationReport().listProperties().toList()) {
            expectedModel.add(s);
        }
        for(Statement s : getExpectedValidationReport().listProperties(SHACL.result).toList()) {
            for(Statement t : s.getResource().listProperties().toList()) {
                if(t.getPredicate().equals(dashSuggestionProp)) {
                    GraphValidationTestCaseType.addStatements(expectedModel, t);
                }
                else if(SHACL.resultPath.equals(t.getPredicate())) {
                    Resource path = ShapePathReader.create().read(t.getResource()).getPathAsRdf();
                    expectedModel.add(path.getModel());
                    expectedModel.add(t.getSubject(), t.getPredicate(),path);
                }
                else {
                    expectedModel.add(t);
                }
            }
        }

        return expectedModel;
    }

    Resource computeOutcome(boolean saveExpectedVsActualResults) {

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

        // is isomorphic
        if (getAdjustedExpectedReport().isIsomorphicWith(adjustedActualReport)) {
            return EARL.passed;
        } else {
            int expectedViolations = getAdjustedExpectedReport().listSubjectsWithProperty(RDF.type, SHACL.ValidationResult).toList().size();
            int actualViolations = adjustedActualReport.listSubjectsWithProperty(RDF.type, SHACL.ValidationResult).toList().size();

            Resource returnResource = EARL.failed;
            String partialVsFailed = "failed";
            // check for partial
            if ((!getAdjustedExpectedReport().isEmpty() && (
                    expectedViolations == 0 && actualViolations == 0 )
                    || (expectedViolations > 0 && actualViolations > 0 )))
            {
                returnResource = W3CTestManifest.PartialResult;
                partialVsFailed = "partial";
            }

            if(saveExpectedVsActualResults){
                writeExpectedVsActual(partialVsFailed, adjustedActualReport);
            }
            else{
                showExpectedVsActual(adjustedActualReport);
            }
            return returnResource;
        }
    }

    private void showExpectedVsActual(Model adjustedActualReport){
        val writer = new RdfStreamWriter(System.err);
        try {
            System.err.println("Expected Violation report:");
            writer.write(getAdjustedExpectedReport());
            System.err.println("");
            System.err.println("Actual Violation report:");
            writer.write(adjustedActualReport);
        } catch (RdfWriterException e) {
            e.printStackTrace();
        }
    }

    private void writeExpectedVsActual(String partialOrFail, Model adjustedActualReport){
        try {
            String file = this.manifest.sourceFile.toString().replace("/", "_");
            file = file.substring(file.lastIndexOf("tests")+6);
            File dirs1 = new File(W3CTestManifest.resultFolder  + "/partial");
            dirs1.mkdirs();

            new RdfFileWriter(W3CTestManifest.resultFolder  + "/" + partialOrFail + "/" + file + ".expected.ttl").write(getAdjustedExpectedReport());
            new RdfFileWriter(W3CTestManifest.resultFolder + "/" + partialOrFail + "/" + file + ".actual.ttl").write(adjustedActualReport);

        } catch (RdfWriterException e) {
            e.printStackTrace();
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

        assert pathSegments.size() >= 3;

        val p1 = pathSegments.get(pathSegments.size() - 3);
        val p2 = pathSegments.get(pathSegments.size() - 2);
        val p3 = pathSegments.get(pathSegments.size() - 1);
        return "/" + p1 + "/" + p2 + "/" + p3;
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