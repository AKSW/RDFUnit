package org.aksw.rdfunit.io.reader;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Checks the size of the remote location and continues only if it is below a certain limit
 * If the size cannot be determined, {@code strict} determines what to do
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 11 /14/13 8:48 AM
 * @version $Id: $Id
 */
public class RdfDereferenceLimitReader implements RdfReader {

    /**
     * the URI to be dereferenced
     */
    private final String uri;

    /**
     * The limit size in ytes
     */
    private final long limitInBytes;

    /**
     * When strict = true and size = -1 we still throw an exception
     */
    private final boolean strict;

    /**
     * Instantiates a new RDF dereference limit reader.
     *
     * @param uri the uri
     * @param limitInBytes the limit in bytes
     */
    public RdfDereferenceLimitReader(String uri, long limitInBytes) {
        this(uri, limitInBytes, true);
    }

    /**
     * Instantiates a new RDF dereference limit reader.
     *
     * @param uri the uri
     * @param limitInBytes the limit in bytes
     * @param strict to fail even when size cannot be determined
     */
    public RdfDereferenceLimitReader(String uri, long limitInBytes, boolean strict) {
        super();
        this.uri = uri;
        this.limitInBytes = limitInBytes;
        this.strict = strict;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RdfReaderException {
        checkUriSizeOrThrowExceprion();

        // continue with a normal Dereference Reader
        RdfReaderFactory.createDereferenceReader(uri).read(model);
    }

    /** {@inheritDoc} */
    @Override
    public void readDataset(Dataset dataset) throws RdfReaderException {
        checkUriSizeOrThrowExceprion();

        // continue with a normal Dereference Reader
        RdfReaderFactory.createDereferenceReader(uri).readDataset(dataset);
    }


    private void checkUriSizeOrThrowExceprion() throws RdfReaderException {
        long size = getUriSize(uri);
        if (size > limitInBytes || !strict || size < 0) {
            throw new RdfReaderException("'" + uri + "' size (" + size + ") bigger than " + limitInBytes);
        }
    }

    /**
     * Calculates the size of the remote resource
     * taken from http://stackoverflow.com/a/7673089
     *
     * @param urlStr the uri to check
     * @return the exact size or -1 in case of an error or unknown length
     */
    public static long getUriSize(String urlStr) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return -1;
        } finally {
            if (conn!=null) {
                conn.disconnect();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "RDFDereferenceLimitReader{" +
                "uri='" + uri + '\'' +
                ", limitInBytes=" + limitInBytes +
                ", strict=" + strict +
                '}';
    }
}
