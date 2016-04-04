package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.Collection;

/**
 * <p>TestCase interface.</p>
 *
 * @author Dimitris Kontokostas
 * @since 9/23/13 6:31 AM
 * @version $Id: $Id
 */
public interface TestCase extends Element, Comparable<TestCase>{

    /**
     * <p>getSparqlWhere.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSparqlWhere();

    /**
     * <p>getSparqlPrevalence.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getSparqlPrevalence();

    /**
     * <p>getResultMessage.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getResultMessage()  {
        return getTestCaseAnnotation().getDescription();
    }

    /**
     * <p>getLogLevel.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     */
    default RLOGLevel getLogLevel()  {
        return getTestCaseAnnotation().getTestCaseLogLevel();
    }

    /**
     * <p>getResultAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    default Collection<ResultAnnotation> getResultAnnotations()  {
        return getTestCaseAnnotation().getResultAnnotations();
    }

    /**
     * <p>getVariableAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    default Collection<ResultAnnotation> getVariableAnnotations() {
        return getTestCaseAnnotation().getVariableAnnotations();
    }

    /**
     * <p>getSparqlPrevalenceQuery.</p>
     *
     * @return a {@link org.apache.jena.query.Query} object.
     */
    default Query getSparqlPrevalenceQuery() {
        if (getSparqlPrevalence().trim().isEmpty()) {
            return null;
        }
        return QueryFactory.create(PrefixNSService.getSparqlPrefixDecl() + getSparqlPrevalence());}

    /**
     * <p>Getter for the field <code>testURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getTestURI() {return getElement().getURI();}

    /**
     * <p>getAbrTestURI.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    default String getAbrTestURI() {return getElement().getLocalName();}

    /**
     * <p>getTestCaseAnnotation.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.interfaces.TestCaseAnnotation} object.
     */
    TestCaseAnnotation getTestCaseAnnotation();

    /**
     * URI is atm based on a hash of the sparql query TODO: change and compare the hash directly
     */
    @Override
    default int compareTo(TestCase o) {
        if (o == null) {
            return -1;
        }

        return this.getTestURI().compareTo(o.getTestURI());
    }

}
