package org.aksw.databugger.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.databugger.exceptions.TripleWriterException;

import java.io.File;
import java.io.FileOutputStream;

/**
 * User: Dimitris Kontokostas
 * Writes a Model to a file
 * Created: 11/14/13 1:01 PM
 */
public class TripleFileWriter extends TripleWriter {
    private final String filename;
    private final String filetype;
    private final boolean skipIfExists;
    private final boolean createParentDirectories;
    private final boolean overwrite;


    public TripleFileWriter(String filename) {
        this(filename, "TURTLE", false, true, true);
    }

    public TripleFileWriter(String filename, boolean skipIfExists) {
        this(filename, "TURTLE", skipIfExists, true, true);
    }

    public TripleFileWriter(String filename, String filetype, boolean skipIfExists, boolean createParentDirectories, boolean overwrite) {
        this.filename = filename;
        this.filetype = filetype;
        this.skipIfExists = skipIfExists;
        this.createParentDirectories = createParentDirectories;
        this.overwrite = overwrite;
    }

    @Override
    public void write(Model model) throws TripleWriterException {
        try {
            File file = new File(filename);

            if (file.exists() && skipIfExists)
                return;

            if (file.exists() && !overwrite)
                throw new TripleWriterException("File already exists and cannot overwrite");

            if (createParentDirectories) {
                File parentF = file.getParentFile();
                if (parentF != null && !parentF.exists())
                    file.getParentFile().mkdirs();
            }
            model.write(new FileOutputStream(file), filetype);

        } catch (Exception e) {
            throw new TripleWriterException("Error writing file: " + e.getMessage());
        }

    }
}
