package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.exceptions.TripleWriterException;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Dimitris Kontokostas
 *         Writes a QEF / Model to an arbitrary OutputStream (RDFFileWriter does more logic and provided separate)
 * @since 5/27/14 5:46 PM
 */
public class RDFStreamWriter extends DataWriter {
    private final OutputStream outputStream;
    private final String filetype;

    public RDFStreamWriter(OutputStream outputStream, String filetype) {
        this.outputStream = outputStream;
        this.filetype = filetype;
    }

    public RDFStreamWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.filetype = "TURTLE";
    }

    @Override
    public void write(QueryExecutionFactory qef) throws TripleWriterException {
        try {
            Model model = SparqlUtils.getModelFromQueryFactory(qef);
            PrefixNSService.setNSPrefixesInModel(model);
            model.write(outputStream, filetype);

        } catch (Exception e) {
            throw new TripleWriterException("Error writing in OutputStream: " + e.getMessage(), e);
        }
    }

    // Returns a FileInputStream or null in case of exception
    // We want to raise the exception only at ready time
    public static OutputStream getOutputStreamFromFilename(String filename) {
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            // do not handle exception at construction time
            return null;
        }
    }
}
