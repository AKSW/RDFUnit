package org.aksw.rdfunit.dqv;

import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;

import java.util.Map;

/**
 * @author Dimitris Kontokostas
 * @since 26/1/2016 12:31 πμ
 */
@ToString
@EqualsAndHashCode
public class MetricMapper {
    private final ImmutableMap<String, String> metricMap;


    private MetricMapper(ImmutableMap<String, String> metricMap) {
        this.metricMap = ImmutableMap.copyOf(metricMap);
    }

    /**
     * returns an immutable Map<String, String>
     */
    public Map<String, String> getMetricMap() {
        return metricMap;
    }

    public static MetricMapper createDefault() {

        Model model;
        try {
            model = RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/dqv/metricMappings.ttl").read();
        } catch (RdfReaderException e) {
            throw new IllegalArgumentException("Cannot read default metric mappings", e);
        }

        ImmutableMap.Builder<String, String> builder = new ImmutableMap.Builder<>();
        model.listStatements().toList().stream()
                .filter(smt -> smt.getPredicate().equals(RDFUNITv.metric))
                .filter(smt -> smt.getObject().isResource())
                .forEach(smt -> builder.put(smt.getSubject().getURI(), smt.getObject().asResource().getURI()));

        return new MetricMapper(builder.build());
    }


}
