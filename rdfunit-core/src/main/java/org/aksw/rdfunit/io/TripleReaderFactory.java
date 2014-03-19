package org.aksw.rdfunit.io;

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

        TripleReader r = new TripleFirstSuccessReader(readers);
        TripleWriter w = new TripleFileWriter(filename, true);
        return new TripleReadAndCacheReader(r, w);

    }
}
