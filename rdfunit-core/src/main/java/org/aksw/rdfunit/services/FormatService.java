package org.aksw.rdfunit.services;

import org.aksw.rdfunit.io.format.SerialiazationFormatFactory;
import org.aksw.rdfunit.io.format.SerializationFormat;

import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 *         Static service that contains all the supported formats for input/output
 * @since 6/18/14 7:23 PM
 */
public final class FormatService {
    private static Collection<SerializationFormat> serializationFormats = null;


    private static Collection<SerializationFormat> getSerializationFormats() {
        // initialize formatTypes
        if (serializationFormats == null) {
            synchronized (FormatService.class) {
                if (serializationFormats == null) {
                    serializationFormats = SerialiazationFormatFactory.getAllFormats();
                }
            }
        }
        return serializationFormats;
    }

    /**
     * returns an input FormatType for a given name
     *
     * @param name the name of the format (e.g. 'ttl')
     * @return a FormatType that corresponds to the format name or null otherwise
     */
    public static SerializationFormat getInputFormat(String name) {
        for (SerializationFormat ft : getSerializationFormats()) {
            if (ft.isAcceptedAsInput(name)) {
                return ft;
            }
        }
        return null;
    }

    /**
     * returns an output FormatType for a given name
     *
     * @param name the name of the format (e.g. 'ttl')
     * @return a FormatType that corresponds to the format name or null otherwise
     */
    public static SerializationFormat getOutputFormat(String name) {
        for (SerializationFormat ft : getSerializationFormats()) {
            if (ft.isAcceptedAsOutput(name)) {
                return ft;
            }
        }
        return null;
    }
}
