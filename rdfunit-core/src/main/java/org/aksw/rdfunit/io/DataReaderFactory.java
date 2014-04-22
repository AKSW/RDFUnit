package org.aksw.rdfunit.io;

import java.util.ArrayList;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 9:01 AM
 */
public class DataReaderFactory {
    public static DataReader createFileOrDereferenceTripleReader(String filenameOrUri) {
        return createFileOrDereferenceTripleReader(filenameOrUri, filenameOrUri);
    }

    public static DataReader createFileOrDereferenceTripleReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        java.util.Collection<DataReader> readers = new ArrayList<DataReader>();
        readers.add(new RDFFileReader(filename));
        readers.add(new RDFDereferenceReader(uri));

        DataReader r = new DataFirstSuccessReader(readers);
        DataWriter w = new RDFFileWriter(filename, true);
        return new DataReadAndCacheReader(r, w);

    }
}
