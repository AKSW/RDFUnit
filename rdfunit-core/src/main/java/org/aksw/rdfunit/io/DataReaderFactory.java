package org.aksw.rdfunit.io;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 9:01 AM
 */
public final class DataReaderFactory {

    private DataReaderFactory() {
    }

    public static DataReader createFileOrDereferenceReader(String filenameOrUri) {
        return createFileOrDereferenceReader(filenameOrUri, filenameOrUri);
    }

    public static DataReader createFileOrDereferenceReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        java.util.Collection<DataReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFDereferenceReader(uri));

        DataReader r = new DataFirstSuccessReader(readers);
        DataWriter w = new RDFFileWriter(filename, true);
        return new DataReadAndCacheReader(r, w);

    }

    public static DataReader createFileOrResourceReader(String filename, String resource) {
        Collection<DataReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFStreamReader(DataReaderFactory.class.getResourceAsStream(resource)));

        return new DataFirstSuccessReader(readers);
    }

    /**
     * Generates a Dereference reader. This can be either a remote url, a local file or a resource
     *
     * @param uri a uri that can be a remote (http) resource, a local file or a java resource object
     * @return a DataFirstSuccessReader that tries to resolve 1) remote 2) local 3) resources
     */
    public static DataReader createDereferenceReader(String uri) {
        Collection<DataReader> readers = new ArrayList<>();
        if (uri.contains("://")) {
            readers.add(new RDFDereferenceReader(uri));
        } else {
            readers.add(new RDFStreamReader(uri));
            readers.add(new RDFStreamReader(DataReaderFactory.class.getResourceAsStream(uri)));
        }

        return new DataFirstSuccessReader(readers);
    }
}
