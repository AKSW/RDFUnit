package org.aksw.rdfunit.prefix;

/**
 * Encapsulates an LOV Entry
 *
 * @author jim
 * @version $Id: $Id
 * @since 0.7.6
 */
public final class LOVEntry implements Comparable<LOVEntry>{

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
    public LOVEntry(String prefix, String vocabularyURI, String vocabularyNamespace, String vocabularyDefinedBy) {
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
    public LOVEntry(String prefix, String vocabularyURI, String vocabularyNamespace) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LOVEntry lovEntry = (LOVEntry) o;

        return prefix.equals(lovEntry.prefix);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = prefix.hashCode();
        result = 31 * result + vocabularyURI.hashCode();
        result = 31 * result + vocabularyNamespace.hashCode();
        result = 31 * result + vocabularyDefinedBy.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "LOVEntry{" +
                "prefix='" + prefix + '\'' +
                ", vocabularyURI='" + vocabularyURI + '\'' +
                '}';
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(LOVEntry o) {
        return this.prefix.compareTo(o.getPrefix());
    }
}
