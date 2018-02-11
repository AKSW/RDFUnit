package org.aksw.rdfunit.io.format;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import java.util.Collection;

/**
 * Static service that contains all the supported formats for input/output
 *
 * @author Dimitris Kontokostas
 * @since 6/18/14 7:23 PM

 */
@Slf4j
public final class FormatService {

    private FormatService() {
    }

    /**
     * keeps all the available formats we define (lazy init)
     */
    private static final class Instance {
        private static final Collection<SerializationFormat> serializationFormats = SerialiazationFormatFactory.getAllFormats();
        private Instance() {}
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

    public static String getFormatFromExtension(String filename) {
        String format = "TURTLE";
        try {
            // try to get if from Jena first
            String extension;
            Lang jenaLang = RDFLanguages.filenameToLang(filename);
            if (jenaLang != null) {
                extension = jenaLang.getFileExtensions().get(0);
            } else {
                int index = filename.lastIndexOf('.');
                extension = filename.substring(index + 1, filename.length());
            }
            SerializationFormat f = FormatService.getInputFormat(extension);
            if (f != null) {
                format = f.getName();
            }
        } catch (Exception e) {
            log.debug("No format found, using the default one", e);
            return "TURTLE";
        }
        return format;
    }
}
