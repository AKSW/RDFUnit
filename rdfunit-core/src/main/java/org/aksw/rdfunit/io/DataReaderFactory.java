package org.aksw.rdfunit.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

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

    public static DataReader createFileOrResourceTripleReader(String filename, String resource) {
        Collection<DataReader> readers = new ArrayList<>();
        readers.add(new RDFFileReader(filename));
        readers.add(new RDFFileReader(DataReaderFactory.class.getResourceAsStream(resource)));

        return new DataFirstSuccessReader(readers);
    }
}
