package org.aksw.rdfunit.io.format;

import com.google.common.base.Objects;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Holds a format serialization description
 *
 * @author Dimitris Kontokostas
 * @since 6 /18/14 4:05 PM
 * @version $Id: $Id
 */
public class SerializationFormat {
    /**
     * The "official" name for the format e.g. "TURTLE"
     */
    private final String name;

    private final SerializationFormatIOType ioType;

    private final SerializationFormatGraphType graphType;

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
     *
     * @param name a {@link java.lang.String} object.
     * @param ioType a {@link org.aksw.rdfunit.io.format.SerializationFormatIOType} object.
     * @param extension a {@link java.lang.String} object.
     * @param headerType a {@link java.lang.String} object.
     * @param graphType a {@link org.aksw.rdfunit.io.format.SerializationFormatGraphType} object.
     */
    public SerializationFormat(String name, SerializationFormatIOType ioType, SerializationFormatGraphType graphType, String extension, String headerType) {
        this(name, ioType, graphType, extension, headerType, new HashSet<>());
    }

    /**
     * Constructor
     *
     * @param name       the name
     * @param ioType       the type
     * @param extension  the extension
     * @param headerType the header type
     * @param synonyms   the synonyms
     * @param graphType a {@link org.aksw.rdfunit.io.format.SerializationFormatGraphType} object.
     */
    public SerializationFormat(String name, SerializationFormatIOType ioType, SerializationFormatGraphType graphType, String extension, String headerType, Set<String> synonyms) {
        this.name = name;
        this.ioType = ioType;
        this.graphType = graphType;
        this.extension = extension;
        this.headerType = headerType;
        // Convert all to lowercase
        this.synonyms = new HashSet<>();
        this.synonyms.addAll(synonyms.stream().map(String::toLowerCase).collect(Collectors.toList()));
    }

    /**
     * Checks if this format is an input format (or input and output) and matches one of the synonyms
     *
     * @param format the format e.g. "nt"
     * @return the boolean true if the format matches or false if it doesn't
     */
    public boolean isAcceptedAsInput(String format) {
        return isAcceptedAsAny(format, SerializationFormatIOType.output);

    }

    /**
     * Checks if this format is an output format (or input and output) and matches one of the synonyms
     *
     * @param format the format e.g. "nt"
     * @return the boolean true if the format matches or false if it doesn't
     */
    public boolean isAcceptedAsOutput(String format) {
        return isAcceptedAsAny(format, SerializationFormatIOType.input);

    }

    private boolean isAcceptedAsAny(String format, SerializationFormatIOType formatType) {
        return !ioType.equals(formatType) && containsFormatName(format);
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hashCode(name, ioType, graphType, extension, headerType, synonyms);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SerializationFormat other = (SerializationFormat) obj;
        return Objects.equal(this.name, other.name)
                && Objects.equal(this.ioType, other.ioType)
                && Objects.equal(this.graphType, other.graphType)
                && Objects.equal(this.extension, other.extension)
                && Objects.equal(this.headerType, other.headerType)
                && Objects.equal(this.synonyms, other.synonyms);
    }

    /**
     * Accorting to {@code SerializationFormatIOType} this can be an
     * input / output or input and output serialization format
     *
     * @return a {@link org.aksw.rdfunit.io.format.SerializationFormatIOType} object.
     */
    public SerializationFormatIOType getIoType() {
        return ioType;
    }

    /**
     * Accorting to {@code SerializationFormatGraphType} this can be an
     * single graph or dataset
     *
     * @return a {@link org.aksw.rdfunit.io.format.SerializationFormatGraphType} object.
     */
    public SerializationFormatGraphType getGraphType() {
        return graphType;
    }
}
