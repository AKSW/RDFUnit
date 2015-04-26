package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.writer.RDFWriter;
import org.aksw.rdfunit.io.writer.RDFWriterException;

/**
 * <p>RDFReadAndCacheReader class.</p>
 *
 * @author Dimitris Kontokostas
 *         reads from a RDFReader and caches result
 * @since 11/14/13 1:09 PM
 * @version $Id: $Id
 */
public class RDFReadAndCacheReader extends RDFReader {
    private final RDFReader reader;
    private final RDFWriter writer;

    /**
     * <p>Constructor for RDFReadAndCacheReader.</p>
     *
     * @param reader a {@link org.aksw.rdfunit.io.reader.RDFReader} object.
     * @param writer a {@link org.aksw.rdfunit.io.writer.RDFWriter} object.
     */
    public RDFReadAndCacheReader(RDFReader reader, RDFWriter writer) {
        super();
        this.reader = reader;
        this.writer = writer;
    }

    /** {@inheritDoc} */
    @Override
    public void read(Model model) throws RDFReaderException {
        reader.read(model);
        //If read succeeds try to write
        try {
            writer.write(model);
        } catch (RDFWriterException e) {

        }
    }
}
