package org.aksw.databugger.tripleReaders;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 9:01 AM
 */
public class TripleReaderFactory {
    public static TripleReader createFileOrDereferenceTripleReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        List<TripleReader> readers = new ArrayList<TripleReader>();
        readers.add(new TripleFileReader(filename));
        readers.add(new TripleDereferenceReader(uri));

        return new TripleFirstSuccessReader(readers);

    }

    public static TripleReader createTripleFileReader(String filename) {
        return new TripleFileReader(filename);

    }
}
