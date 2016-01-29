package org.aksw.rdfunit.model.impl;

import lombok.*;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;

/**
 * <p>Abstract TestCase class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/23/13 6:31 AM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public abstract class AbstractTestCaseImpl implements TestCase, Comparable<AbstractTestCaseImpl> {

    @Getter @NonNull protected final Resource element;
    @Getter @NonNull protected final TestCaseAnnotation testCaseAnnotation;

    @Override
    public String getResultMessage() {
        return testCaseAnnotation.getDescription();
    }

    @Override
    public RLOGLevel getLogLevel() {
        return testCaseAnnotation.getTestCaseLogLevel();
    }

    @Override
    public Collection<ResultAnnotation> getResultAnnotations() {
        return testCaseAnnotation.getResultAnnotations();
    }

    @Override
    public Collection<ResultAnnotation> getVariableAnnotations() {
        return testCaseAnnotation.getVariableAnnotations();
    }

    @Override
    public Query getSparqlPrevalenceQuery() {
        if (getSparqlPrevalence().trim().isEmpty())
            return null;
        return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());
    }

    @Override
    public String getTestURI() {
        return element.getURI();
    }

    @Override
    public String getAbrTestURI() {
        return getTestURI().replace(PrefixNSService.getNSFromPrefix("rutt"), "rutt:");
    }



    /** {@inheritDoc} */
    @Override
    public int compareTo(AbstractTestCaseImpl o) {
        if (o == null) {
            return -1;
        }

        return this.getTestURI().compareTo(o.getTestURI());
    }

}
