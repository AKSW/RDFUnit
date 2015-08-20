package org.aksw.rdfunit.io.format;

/**
 * Defines different types of format type
 * (helper private enum for SerializationFormat)
 *
 * @author Dimitris Kontokostas
 * @since 6/18/14 6:50 PM
 * @version $Id: $Id
 */
public enum SerializationFormatIOType {
    /**
     * Only input format
     */
    input,

    /**
     * Only output format
     */
    output,

    /**
     * Input and Output format
     */
    inputAndOutput
}
