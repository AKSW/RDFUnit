package org.aksw.rdfunit.dqv;

import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author Dimitris Kontokostas
 * @since 21/1/2016 9:48 πμ
 */
@Value
public class DqvReport {

    private static final String undefinedMetric = "http://rdfunit.aksw.org/ns/rdqv#UndefinedMetric";
    private static final String unclassifiedMetric = "http://rdfunit.aksw.org/ns/rdqv#UnclassifiedMetric";

    @NonNull private final TestExecution testExecution;
    @NonNull private final MetricMapper metricMapper;

    public Collection<QualityMeasure> getQualityMeasures() {


        Collection<ShaclTestCaseResult> results =
            testExecution.getTestCaseResults().stream()   // go through all results
                .filter(t -> t instanceof ShaclTestCaseResult)
                .map(ShaclTestCaseResult.class::cast) // use only Shacl results
                .collect(Collectors.toList());

        return results.stream()
                // get source constraints or use undefined
                .map(r -> getSourceConstraintFromResult(r).orElse(undefinedMetric))
                // map to metrics or use unclassified metric
                .map(this::getMetricFromSourceConstraint)
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

    private Optional<String> getSourceConstraintFromResult(ShaclTestCaseResult r) {
        return r.getResultAnnotations().stream()
                .filter(p -> p.getProperty().equals(SHACL.sourceConstraint))
                .flatMap(p2 -> p2.getValues().stream())
                .filter(RDFNode::isResource)
                .map(n -> n.asResource().getURI())
                .findFirst();

    }

    private String getMetricFromSourceConstraint(String sourceConstraint) {
        return metricMapper
                .getMetricMap()
                .getOrDefault(sourceConstraint, unclassifiedMetric);

    }


}
