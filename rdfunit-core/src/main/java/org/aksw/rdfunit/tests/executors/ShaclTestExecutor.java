package org.aksw.rdfunit.tests.executors;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.exceptions.TestCaseExecutionException;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.results.TestCaseResult;
import org.aksw.rdfunit.sources.TestSource;
import org.aksw.rdfunit.tests.query_generation.QueryGenerationFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Extended Test Executor extends RLOG Executor but provides richer error metadata
 * TODO: At the moment this is partially
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 6:13 PM

 */
public class ShaclTestExecutor extends ShaclSimpleTestExecutor {

    /**
     * Instantiates a new ExtendedTestExecutor
     *
     * @param queryGenerationFactory a QueryGenerationFactory
     */
    public ShaclTestExecutor(QueryGenerationFactory queryGenerationFactory) {
        super(queryGenerationFactory);
    }

    @Override
    protected Collection<TestCaseResult> executeSingleTest(TestSource testSource, TestCase testCase) throws TestCaseExecutionException {

        Collection<TestCaseResult> testCaseResults = new ArrayList<>();
        PropertyValuePairSet.PropertyValuePairSetBuilder annotationSetBuilder = PropertyValuePairSet.builder();


        try (QueryExecution qe = testSource.getExecutionFactory().createQueryExecution(queryGenerationFactory.getSparqlQuery(testCase))){

            ResultSet results = qe.execSelect();

            while (results.hasNext()) {

                QuerySolution qs = results.next();

                RDFNode focusNode = testCase.getFocusNode(qs);
                assert(focusNode != null);

                String message = testCase.getResultMessage();
                if (qs.contains("message")) {
                    message = qs.get("message").toString();
                }
                RLOGLevel logLevel = testCase.getLogLevel();

                ShaclTestCaseResultImpl.Builder resultBuilder = new ShaclTestCaseResultImpl.Builder(testCase.getElement(), logLevel, message, focusNode);

                annotationSetBuilder = PropertyValuePairSet.builder(); //reset

                // get static annotations for new test
                for (ResultAnnotation resultAnnotation : testCase.getResultAnnotations()) {
                    // Get values
                    if (resultAnnotation.getAnnotationValue().isPresent()) {
                        // FIXME, I don't understand why we don't just use class ResultAnnotation as result annotations instead of PropertyValuePair (since we basically just copy them) ?
                        annotationSetBuilder.annotation(
                                PropertyValuePair.create(resultAnnotation.getAnnotationProperty(), resultAnnotation.getAnnotationValue().get()));
                    }
                }
                // get annotations from the SPARQL query
                for (ResultAnnotation resultAnnotation : testCase.getVariableAnnotations()) {
                    // Get the variable name
                    if (resultAnnotation.getAnnotationVarName().isPresent()) {
                        String variable = resultAnnotation.getAnnotationVarName().get().trim();
                        //If it exists, add it in the Set
                        if (qs.contains(variable)) {
                            annotationSetBuilder.annotation(
                                    PropertyValuePair.create(resultAnnotation.getAnnotationProperty(), qs.get(variable)));
                        }
                    }
                }

                testCaseResults.add(resultBuilder
                        .setResultAnnotations(annotationSetBuilder.build().getAnnotations())
                        .build());
            }

        } catch (QueryExceptionHTTP e) {
            checkQueryResultStatus(e);
        }

        return testCaseResults;
    }
}
