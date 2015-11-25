package org.aksw.rdfunit.sources;


import org.aksw.rdfunit.enums.TestAppliesTo;


/**
 * A source can be various things like a dataset, a vocabulary or an application
 *
 * @author Dimitris Kontokostas
 * @version $Id: $Id
 */
public interface Source {

    /**
     * <p>Getter for the field <code>prefix</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getPrefix();

    /**
     * <p>Getter for the field <code>uri</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getUri() ;

    /**
     * <p>getSourceType.</p>
     *
     * @return a {@link org.aksw.rdfunit.enums.TestAppliesTo} object.
     */
    TestAppliesTo getSourceType();


}
