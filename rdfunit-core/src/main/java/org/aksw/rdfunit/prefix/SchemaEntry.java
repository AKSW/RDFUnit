package org.aksw.rdfunit.prefix;

import com.google.common.base.Objects;

/**
 * Encapsulates an LOV Entry
 *
 * @author jim
 * @version $Id: $Id
 * @since 0.7.6
 */
public final class SchemaEntry implements Comparable<SchemaEntry>{

    private final String prefix; // prefix is a unique identifier for all entries
    private final String vocabularyURI;
    private final String vocabularyNamespace;
    private final String vocabularyDefinedBy; // optional if URI does not dereference properly (provided by LOV)

    /**
     * Default constructor
     *
     * @param prefix the prefix
     * @param vocabularyURI the vocabulary uRI
     * @param vocabularyNamespace the vocabulary namespace
     * @param vocabularyDefinedBy the vocabulary defined by
     */
    public SchemaEntry(String prefix, String vocabularyURI, String vocabularyNamespace, String vocabularyDefinedBy) {
        this.prefix = prefix;
        this.vocabularyURI = vocabularyURI;
        this.vocabularyNamespace = vocabularyNamespace;
        this.vocabularyDefinedBy = vocabularyDefinedBy;
    }

    /**
     * Constructor where DefinedBy is same as vocabulary URI
     *
     * @param prefix the prefix
     * @param vocabularyURI the vocabulary uRI
     * @param vocabularyNamespace the vocabulary namespace
     */
    public SchemaEntry(String prefix, String vocabularyURI, String vocabularyNamespace) {
        this(prefix, vocabularyURI, vocabularyNamespace, vocabularyNamespace);
    }

    /**
     * Gets prefix.
     *
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets vocabulary uRI.
     *
     * @return the vocabulary uRI
     */
    public String getVocabularyURI() {
        return vocabularyURI;
    }

    /**
     * Gets vocabulary namespace.
     *
     * @return the vocabulary namespace
     */
    public String getVocabularyNamespace() {
        return vocabularyNamespace;
    }

    /**
     * Gets vocabulary defined by.
     *
     * @return the vocabulary defined by
     */
    public String getVocabularyDefinedBy() {
        return vocabularyDefinedBy;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hashCode(prefix, vocabularyURI, vocabularyNamespace, vocabularyDefinedBy);
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
        final SchemaEntry other = (SchemaEntry) obj;
        return Objects.equal(this.prefix, other.prefix)
                && Objects.equal(this.vocabularyURI, other.vocabularyURI)
                && Objects.equal(this.vocabularyNamespace, other.vocabularyNamespace)
                && Objects.equal(this.vocabularyDefinedBy, other.vocabularyDefinedBy);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "SchemaEntry{" +
                "prefix='" + prefix + '\'' +
                ", vocabularyURI='" + vocabularyURI + '\'' +
                ", vocabularyNamespace='" + vocabularyNamespace + '\'' +
                ", vocabularyDefinedBy='" + vocabularyDefinedBy + '\'' +
                '}';
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(SchemaEntry o) {
        return this.prefix.compareTo(o.getPrefix());
    }
}
