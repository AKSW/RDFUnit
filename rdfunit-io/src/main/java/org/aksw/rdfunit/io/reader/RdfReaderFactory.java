package org.aksw.rdfunit.io.reader;

import org.aksw.rdfunit.Resources;
import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.io.writer.RdfWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Dimitris Kontokostas
 * @since 11/14/13 9:01 AM
 */
public final class RdfReaderFactory {

    private RdfReaderFactory() {
    }

    public static RdfReader createFileOrDereferenceReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        Collection<RdfReader> readers = new ArrayList<>();
        readers.add(new RdfStreamReader(filename));
        readers.add(new RdfDereferenceReader(uri));

        RdfReader r = new RdfFirstSuccessReader(readers);
        RdfWriter w = new RdfFileWriter(filename, true);
        return new RdfReadAndCacheReader(r, w);

    }

    public static RdfReader createResourceReader(String resource) {
        InputStream is = Resources.class.getResourceAsStream(resource);
        //checkArgument(is != null, "Could not load resource file %s", resource);
        return new RdfStreamReader(is, FormatService.getFormatFromExtension(resource));
    }

    public static RdfReader createFileOrResourceReader(String filename, String resource) {
        Collection<RdfReader> readers = new ArrayList<>();
        readers.add(new RdfStreamReader(filename));
        readers.add(createResourceReader(resource));

        return new RdfFirstSuccessReader(readers);
    }

    public static RdfReader createResourceOrFileOrDereferenceReader(String uri) {
        Collection<RdfReader> readers = new ArrayList<>();
        readers.add(createResourceReader(uri));
        readers.add(new RdfStreamReader(uri));
        readers.add(new RdfDereferenceReader(uri));

        return new RdfFirstSuccessReader(readers);
    }

    /**
     * Generates a Dereference reader. This can be either a remote url, a local file or a resource
     *
     * @param uri a uri that can be a remote (http) resource, a local file or a java resource object
     * @return a RDFFirstSuccessReader that tries to resolve 1) remote 2) local 3) resources
     */
    public static RdfReader createDereferenceReader(String uri) {
        Collection<RdfReader> readers = new ArrayList<>();
        if (!IOUtils.isFile(uri)) {
            readers.add(new RdfDereferenceReader(uri));
            //readers.add(new RDFaReader(uri));
        } else {
            readers.add(new RdfStreamReader(uri));
            readers.add(RdfReaderFactory.createResourceReader(uri));
        }

        return new RdfFirstSuccessReader(readers);
    }

    public static RdfReader createReaderFromText(String text, String format) {
        InputStream is;
        try {
            is = new ByteArrayInputStream(text.getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Invalid source name: " + text, e);
        }
        return new RdfStreamReader(is, format);
    }

    public static RdfReader createEmptyReader() {
        return RdfReaderFactory.createResourceReader("/org/aksw/rdfunit/validate/data/empty.ttl");
    }
}
