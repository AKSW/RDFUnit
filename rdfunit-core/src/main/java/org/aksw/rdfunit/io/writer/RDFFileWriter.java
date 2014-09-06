package org.aksw.rdfunit.io.writer;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.Utils.SparqlUtils;
import org.aksw.rdfunit.services.PrefixNSService;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Dimitris Kontokostas
 *         Writes a Model to a file
 * @since 11/14/13 1:01 PM
 */
public class RDFFileWriter extends RDFWriter {
    private final String filename;
    private final String filetype;
    private final boolean skipIfExists;
    private final boolean createParentDirectories;
    private final boolean overwrite;


    public RDFFileWriter(String filename) {
        this(filename, "TURTLE", false, true, true);
    }

    public RDFFileWriter(String filename, String filetype) {
        this(filename, filetype, false, true, true);
    }

    public RDFFileWriter(String filename, boolean skipIfExists) {
        this(filename, "TURTLE", skipIfExists, true, true);
    }

    public RDFFileWriter(String filename, String filetype, boolean skipIfExists, boolean createParentDirectories, boolean overwrite) {
        super();
        this.filename = filename;
        this.filetype = filetype;
        this.skipIfExists = skipIfExists;
        this.createParentDirectories = createParentDirectories;
        this.overwrite = overwrite;
    }

    @Override
    public void write(QueryExecutionFactory qef) throws RDFWriterException {
        try {
            File file = new File(filename);

            if (file.exists() && skipIfExists) {
                return;
            }

            if (file.exists() && !overwrite) {
                throw new RDFWriterException("File already exists and cannot overwrite");
            }

            if (createParentDirectories) {
                File parentF = file.getParentFile();
                if (parentF != null && !parentF.exists()) {
                    if (!parentF.mkdirs()) {
                        throw new RDFWriterException("Cannot create new directory structure for file: " + filename);
                    }
                }
            }
            Model model = SparqlUtils.getModelFromQueryFactory(qef);
            PrefixNSService.setNSPrefixesInModel(model);
            model.write(new FileOutputStream(file), filetype);

        } catch (Exception e) {
            throw new RDFWriterException("Error writing file: " + e.getMessage(), e);
        }

    }
}
