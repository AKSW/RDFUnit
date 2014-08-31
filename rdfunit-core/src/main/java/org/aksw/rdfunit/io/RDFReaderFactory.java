package org.aksw.rdfunit.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 9:01 AM
 */
public final class RDFReaderFactory {

    private RDFReaderFactory() {
    }

    public static RDFReader createFileOrDereferenceReader(String filenameOrUri) {
        return createFileOrDereferenceReader(filenameOrUri, filenameOrUri);
    }

    public static RDFReader createFileOrDereferenceReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        Collection<RDFReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFDereferenceReader(uri));

        RDFReader r = new RDFFirstSuccessReader(readers);
        RDFWriter w = new RDFFileWriter(filename, true);
        return new RDFReadAndCacheReader(r, w);

    }

    public static RDFReader createResourceReader(String resource) {
        return new RDFStreamReader(RDFReaderFactory.class.getResourceAsStream(resource));
    }

    public static RDFReader createFileOrResourceReader(String filename, String resource) {
        Collection<RDFReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(createResourceReader(resource));

        return new RDFFirstSuccessReader(readers);
    }

    /**
     * Generates a Dereference reader. This can be either a remote url, a local file or a resource
     *
     * @param uri a uri that can be a remote (http) resource, a local file or a java resource object
     * @return a RDFFirstSuccessReader that tries to resolve 1) remote 2) local 3) resources
     */
    public static RDFReader createDereferenceReader(String uri) {
        Collection<RDFReader> readers = new ArrayList<>();
        if (uri.contains("://")) {
            readers.add(new RDFDereferenceReader(uri));
        } else {
            readers.add(new RDFStreamReader(uri));
            readers.add(new RDFStreamReader(RDFReaderFactory.class.getResourceAsStream(uri)));
        }

        return new RDFFirstSuccessReader(readers);
    }

    public static RDFReader createReaderFromText(String text, String format) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(text.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid source name: " + text, e);
        }
        return new RDFStreamReader(is, format);
    }
}
