package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * <p>RLOGLevel class.</p>
 *
 * @author Dimitris Kontokostas
 *         Defines all available log levels
 *         Copied from NLP2RDF (https://github.com/NLP2RDF/software/blob/master/java-maven/core/jena/src/main/java/org/nlp2rdf/core/vocab/RLOGIndividuals.java)
 * @since 6/17/14 9:49 AM
 * @version $Id: $Id
 */
public enum RLOGLevel {

    /**
     * ERROR: The ERROR level designates error events that might still allow the application to continue running.
     */
    ERROR,

    /**
     * TRACE: The DEBUG Level designates fine-grained informational events that are most useful to debug an application.
     */
    DEBUG,

    /**
     * OFF: The OFF has the highest possible rank and is intended to turn off logging.
     */
    OFF,

    /**
     * FATAL: The FATAL level designates very severe error events that will presumably lead the application to abort.
     */
    FATAL,

    /**
     * WARN: The WARN level designates potentially harmful situations.
     */
    WARN,

    /**
     * TRACE: The TRACE Level designates finer-grained informational events than the DEBUG.
     */
    TRACE,

    /**
     * INFO: The INFO level designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    INFO,

    /**
     * ALL: The ALL has the lowest possible rank and is intended to turn on all logging.
     */
    ALL;

    /**
     * Holds the prefix to resolve this enum
     */
    private static final String schemaPrefix = "rlog";

    /**
     * <p>getUri.</p>
     *
     * @return a full URI/IRI as a String
     */
    public String getUri() {
        return PrefixNSService.getNSFromPrefix(schemaPrefix) + name();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getUri();
    }

    /**
     * Resolves a full URI/IRI to an enum
     *
     * @param value the URI/IRI we want to resolve
     * @return the equivalent enum type or null if it cannot resolve
     */
    public static RLOGLevel resolve(String value) {

        String qName = PrefixNSService.getLocalName(value, schemaPrefix);
        for (RLOGLevel level : values()) {
            if (qName.equals(level.name())) {
                return level;
            }
        }
        return null;

    }
}
