package org.aksw.rdfunit.model.interfaces;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;

import java.util.Collection;

/**
 * <p>TestCase interface.</p>
 *
 * @author Dimitris Kontokostas
 * @since 9/23/13 6:31 AM
 * @version $Id: $Id
 */
public interface TestCase {

    /**
     * <p>getUnitTestModel.</p>
     *
     * @return a {@link com.hp.hpl.jena.rdf.model.Model} object.
     */
    Model getUnitTestModel() ;

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
     * <p>serialize.</p>
     *
     * @param model a {@link com.hp.hpl.jena.rdf.model.Model} object.
     * @return a {@link com.hp.hpl.jena.rdf.model.Resource} object.
     */
    Resource serialize(Model model);

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
     * @return a {@link com.hp.hpl.jena.query.Query} object.
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


}
