package org.aksw.rdfunit.io.format;

import java.util.HashSet;
import java.util.Set;

/**
 * Holds a format serialization description
 *
 * @author Dimitris Kontokostas
 * @since 6 /18/14 4:05 PM
 */
public class SerializationFormat {
    /**
     * The "official" name for the format e.g. "TURTLE"
     */
    private final String name;

    /**
     * Accorting to {@code SerializationFormatType} this can be an
     * input / output or input & output serialization format
     */
    private final SerializationFormatType type;

    /**
     * The default extension e.g. "ttl" for turtle
     */
    private final String extension;

    /**
     * Mimetype for this format e.g. "text/turtle" for turtle
     */
    private final String headerType;

    /**
     * A list of synonyms to make format parsing easier
     * for NTRIPLES format it can be NTRIPLES, NTRIPLE, NT, NT-RIPLES, NT-RIPLE,
     * Case does not matter here!
     */
    private final Set<String> synonyms;

    /**
     * Constructor
     */
    public SerializationFormat(String name, SerializationFormatType type, String extension, String headerType) {
        this(name, type, extension, headerType, new HashSet<String>());
    }

    /**
     * Constructor
     *
     * @param name       the name
     * @param type       the type
     * @param extension  the extension
     * @param headerType the header type
     * @param synonyms   the synonyms
     */
    public SerializationFormat(String name, SerializationFormatType type, String extension, String headerType, Set<String> synonyms) {
        this.name = name;
        this.type = type;
        this.extension = extension;
        this.headerType = headerType;
        // Convert all to lowercase
        this.synonyms = new HashSet<>();
        for (String synonym : synonyms) {
            this.synonyms.add(synonym.toLowerCase());
        }
    }

    /**
     * Checks if this format is an input format (or input and output) and matches one of the synonyms
     *
     * @param format the format e.g. "nt"
     * @return the boolean true if the format matches or false if it doesn't
     */
    public boolean isAcceptedAsInput(String format) {
        return isAcceptedAsAny(format, SerializationFormatType.output);

    }

    /**
     * Checks if this format is an output format (or input and output) and matches one of the synonyms
     *
     * @param format the format e.g. "nt"
     * @return the boolean true if the format matches or false if it doesn't
     */
    public boolean isAcceptedAsOutput(String format) {
        return isAcceptedAsAny(format, SerializationFormatType.input);

    }

    private boolean isAcceptedAsAny(String format, SerializationFormatType formatType) {
        return !type.equals(formatType) && containsFormatName(format);
    }

    /**
     * Helper function that matches only the synonyms
     */
    private boolean containsFormatName(String format) {
        return name.equalsIgnoreCase(format) || synonyms.contains(format.toLowerCase());

    }

    /**
     * Gets the default serialization name.
     *
     * @return the serialization name. e.g. TURTLE
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the serialization extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Gets the serialization mimetype
     *
     * @return the header type
     */
    public String getHeaderType() {
        return headerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerializationFormat)) return false;

        SerializationFormat that = (SerializationFormat) o;

        if (!extension.equals(that.extension)) return false;
        return type.equals(that.type) && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + extension.hashCode();
        return result;
    }
}
