package org.aksw.rdfunit.dqv;

import com.hp.hpl.jena.rdf.model.RDFNode;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 21/1/2016 9:48 πμ
 */
public class DqvReport {

    private final TestExecution testExecution;

    public DqvReport(TestExecution testExecution) {
        this.testExecution = testExecution;
    }

    Collection<QualityMeasure> getQualityMeasures() {


        return
        testExecution.getTestCaseResults().stream()   // go through all results
                .filter(t -> t instanceof ShaclTestCaseResult)
                .map(ShaclTestCaseResult.class::cast) // use only Shacl results
                .flatMap(r -> r.getResultAnnotations().stream())  // get annotations

                .filter(p -> p.getProperty().equals(SHACL.sourceConstraint))
                .flatMap(p2 -> p2.getValues().stream())
                .filter(RDFNode::isResource)
                .map(n -> n.asResource().getURI())  // get the metric IRIs

                // group same metrics and count
                .collect( Collectors.groupingBy( c -> c, Collectors.counting() ) )
                .entrySet().stream()

                // create a new measure per metric IRI
                .map(v ->
                        new QualityMeasure(
                                testExecution.getTestExecutionUri(),
                                v.getKey(),
                                (double)v.getValue())
                ).collect(Collectors.toList());



    }


}
