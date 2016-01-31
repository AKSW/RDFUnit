package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.enums.RLOGLevel;
import org.apache.jena.query.Query;

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
    String getResultMessage();

    /**
     * <p>getLogLevel.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.RLOGLevel} object.
     */
    RLOGLevel getLogLevel();

    /**
     * <p>getResultAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<ResultAnnotation> getResultAnnotations();

    /**
     * <p>getVariableAnnotations.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<ResultAnnotation> getVariableAnnotations();

    /**
     * <p>getSparqlPrevalenceQuery.</p>
     *
     * @return a {@link org.apache.jena.query.Query} object.
     */
    Query getSparqlPrevalenceQuery() ;

    /**
     * <p>Getter for the field <code>testURI</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getTestURI();

    /**
     * <p>getAbrTestURI.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getAbrTestURI();

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
