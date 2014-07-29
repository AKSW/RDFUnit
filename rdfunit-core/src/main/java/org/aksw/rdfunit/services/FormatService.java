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

    private FormatService(){}

    /**
     * keeps all the available formats we define (lazy init)
     */
    private static class Instance {
        private static final Collection<SerializationFormat> serializationFormats = SerialiazationFormatFactory.getAllFormats();
    }

    /**
     * returns an input FormatType for a given name
     *
     * @param name the name of the format (e.g. 'ttl')
     * @return a FormatType that corresponds to the format name or null otherwise
     */
    public static SerializationFormat getInputFormat(String name) {
        for (SerializationFormat ft : Instance.serializationFormats) {
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
        for (SerializationFormat ft : Instance.serializationFormats) {
            if (ft.isAcceptedAsOutput(name)) {
                return ft;
            }
        }
        return null;
    }
}
