package org.aksw.rdfunit.io.reader;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.io.writer.RDFWriter;
import org.aksw.rdfunit.io.writer.RDFWriterException;

/**
 * @author Dimitris Kontokostas
 *         reads from a RDFReader and caches result
 * @since 11/14/13 1:09 PM
 */
public class RDFReadAndCacheReader extends RDFReader {
    private final RDFReader reader;
    private final RDFWriter writer;

    public RDFReadAndCacheReader(RDFReader reader, RDFWriter writer) {
        super();
        this.reader = reader;
        this.writer = writer;
    }

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
