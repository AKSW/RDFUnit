package org.aksw.rdfunit.io.reader;

import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.writer.RDFFileWriter;
import org.aksw.rdfunit.io.writer.RDFWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * <p>RDFReaderFactory class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11/14/13 9:01 AM
 * @version $Id: $Id
 */
public final class RDFReaderFactory {

    private RDFReaderFactory() {
    }

//    public static RDFReader createFileOrDereferenceReader(String filenameOrUri) {
//        return createFileOrDereferenceReader(filenameOrUri, filenameOrUri);
//    }

    /**
     * <p>createFileOrDereferenceReader.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader createFileOrDereferenceReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        Collection<RDFReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFDereferenceReader(uri));

        RDFReader r = new RDFFirstSuccessReader(readers);
        RDFWriter w = new RDFFileWriter(filename, true);
        return new RDFReadAndCacheReader(r, w);

    }

    /**
     * <p>createResourceReader.</p>
     *
     * @param resource a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader createResourceReader(String resource) {
        return new RDFStreamReader(RDFReaderFactory.class.getResourceAsStream(resource), FormatService.getFormatFromExtension(resource));
    }

    /**
     * <p>createFileOrResourceReader.</p>
     *
     * @param filename a {@link java.lang.String} object.
     * @param resource a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader createFileOrResourceReader(String filename, String resource) {
        Collection<RDFReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(createResourceReader(resource));

        return new RDFFirstSuccessReader(readers);
    }

    /**
     * <p>createResourceOrFileOrDereferenceReader.</p>
     *
     * @param uri a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     * @since 0.7.20
     */
    public static RDFReader createResourceOrFileOrDereferenceReader(String uri) {
        Collection<RDFReader> readers = new ArrayList<>();
        readers.add(createResourceReader(uri));
        readers.add(new RDFStreamReader(uri));
        readers.add(new RDFDereferenceReader(uri));

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
        if (!IOUtils.isFile(uri)) {
            readers.add(new RDFDereferenceReader(uri));
            readers.add(new RDFaReader(uri));
        } else {
            readers.add(new RDFStreamReader(uri));
            readers.add(RDFReaderFactory.createResourceReader(uri));
        }

        return new RDFFirstSuccessReader(readers);
    }

    /**
     * <p>createReaderFromText.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @param format a {@link java.lang.String} object.
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     */
    public static RDFReader createReaderFromText(String text, String format) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(text.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid source name: " + text, e);
        }
        return new RDFStreamReader(is, format);
    }

    /**
     * <p>createEmptyReader.</p>
     *
     * @return a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     * @since 0.7.19
     */
    public static RDFReader createEmptyReader() {
        return RDFReaderFactory.createResourceReader("/org/aksw/rdfunit/io/empty.ttl");
    }

}
