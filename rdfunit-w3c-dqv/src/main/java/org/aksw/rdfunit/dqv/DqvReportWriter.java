package org.aksw.rdfunit.dqv;

import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.utils.JenaUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class DqvReportWriter implements ElementWriter {

    private final static String dqvNS = "http://www.w3.org/ns/dqv#";
    private final Collection<QualityMeasure> measures;

    private DqvReportWriter(Collection<QualityMeasure> measures) {
        this.measures = measures;
    }

    public static DqvReportWriter create(Collection<QualityMeasure> measures) {return new DqvReportWriter(measures);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {

        Resource te = null;
        for (QualityMeasure m: measures) {
            te = model.createResource(m.getTestExecutionUri());
            Resource measureResource = model.createResource(JenaUtils.getUniqueIri())
                    .addProperty(ResourceFactory.createProperty(dqvNS + "computedOn"), te)
                    .addProperty(ResourceFactory.createProperty(dqvNS + "hasMetric"), ResourceFactory.createResource(m.getDqvMetricUri()))
                    .addProperty(ResourceFactory.createProperty(dqvNS + "value"), "" + m.getValue())
                    ;

            te.addProperty(ResourceFactory.createProperty(dqvNS + "dqv:hasQualityMeasure"), measureResource );

        }

        return te;
    }
}
