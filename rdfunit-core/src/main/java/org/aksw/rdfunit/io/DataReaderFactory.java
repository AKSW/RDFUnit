package org.aksw.rdfunit.io;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 9:01 AM
 */
public class DataReaderFactory {
    public static DataReader createFileOrDereferenceReader(String filenameOrUri) {
        return createFileOrDereferenceReader(filenameOrUri, filenameOrUri);
    }

    public static DataReader createFileOrDereferenceReader(String filename, String uri) {
        /* String baseFolder, TestAppliesTo schemaType, String uri, String prefix */
        java.util.Collection<DataReader> readers = new ArrayList<DataReader>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFDereferenceReader(uri));

        DataReader r = new DataFirstSuccessReader(readers);
        DataWriter w = new RDFFileWriter(filename, true);
        return new DataReadAndCacheReader(r, w);

    }

    public static DataReader createFileOrResourceReader(String filename, String resource) {
        Collection<DataReader> readers = new ArrayList<>();
        readers.add(new RDFStreamReader(filename));
        readers.add(new RDFStreamReader(DataReaderFactory.class.getResourceAsStream(resource)));

        return new DataFirstSuccessReader(readers);
    }
}
