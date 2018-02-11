package org.aksw.rdfunit.io.writer;

import lombok.extern.slf4j.Slf4j;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.io.IOUtils;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.rdf.model.Model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Writes a QEF / Model to an arbitrary OutputStream (RDFFileWriter does more logic and provided separate)
 *
 * @author Dimitris Kontokostas
 * @since 5/27/14 5:46 PM
 */
@Slf4j
public class RdfStreamWriter implements RdfWriter {

    private final OutputStream outputStream;
    private final String filetype;

    public RdfStreamWriter(OutputStream outputStream, String filetype) {
        super();
        this.outputStream = outputStream;
        this.filetype = filetype;
    }

    public RdfStreamWriter(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
        this.filetype = "TURTLE";
    }


    @Override
    public void write(QueryExecutionFactory qef) throws RdfWriterException {
        try {
            Model model = IOUtils.getModelFromQueryFactory(qef);
            PrefixNSService.setNSPrefixesInModel(model);
            model.write(outputStream, filetype);

        } catch (Exception e) {
            throw new RdfWriterException("Error writing in OutputStream: " + e.getMessage(), e);
        }
    }

    // Returns a FileInputStream or null in case of exception
    // We want to raise the exception only at ready time
    public static OutputStream getOutputStreamFromFilename(String filename) {
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            log.debug("Error initializing RdfStreamReader, file '{}' not found", filename);
            // do not handle exception at construction time
            return null;
        }
    }
}
