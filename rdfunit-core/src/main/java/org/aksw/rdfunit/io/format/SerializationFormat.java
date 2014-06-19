package org.aksw.rdfunit.io.format;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Dimitris Kontokostas
 * Holds a format description
 * <p/>
 * Created: 6/18/14 4:05 PM
 */
public class SerializationFormat {
    private final String name;
    private final SerializationFormatType type;
    private final String extension;
    private final String headerType;
    private final Set<String> synonyms;

    public SerializationFormat(String name, SerializationFormatType type, String extension, String headerType) {
        this(name, type, extension, headerType, new HashSet<String>());
    }

    public SerializationFormat(String name, SerializationFormatType type, String extension, String headerType, Set<String> synonyms) {
        this.name = name;
        this.type = type;
        this.extension = extension;
        this.headerType = headerType;
        // Convert all to lowercase
        this.synonyms = new HashSet<String>();
        for (String synonym : synonyms) {
            this.synonyms.add(synonym.toLowerCase());
        }
    }

    public boolean isAcceptedAsInput(String format) {
        if (type.equals(SerializationFormatType.output))
            return false;

        return containsFormatName(format);
    }

    public boolean isAcceptedAsOutput(String format) {
        if (type.equals(SerializationFormatType.input))
            return false;

        return containsFormatName(format);
    }

    private boolean containsFormatName(String format) {
        if (name.equalsIgnoreCase(format))
            return true;

        return synonyms.contains(format.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    public String getHeaderType() {
        return headerType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SerializationFormat)) return false;

        SerializationFormat that = (SerializationFormat) o;

        if (!extension.equals(that.extension)) return false;
        if (!type.equals(that.type)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + extension.hashCode();
        return result;
    }
}
