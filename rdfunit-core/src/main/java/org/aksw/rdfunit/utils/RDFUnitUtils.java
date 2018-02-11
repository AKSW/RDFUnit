package org.aksw.rdfunit.utils;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.prefix.LOVEndpoint;
import org.aksw.rdfunit.prefix.SchemaEntry;
import org.aksw.rdfunit.sources.SchemaService;

import java.io.*;
import java.util.Collection;

/**
 * @author Dimitris Kontokostas
 * @since 9/24/13 11:25 AM
 */
@Slf4j
public final class RDFUnitUtils {

    private RDFUnitUtils() {
    }

    public static void fillSchemaServiceFromFile(String additionalCSV) {

        try (InputStream inputStream = new FileInputStream(additionalCSV)) {
            fillSchemaServiceFromFile(inputStream);
        } catch (IOException e) {
            log.error("File " + additionalCSV + " not fount!", e);
        }
    }

    public static void fillSchemaServiceFromFile(InputStream additionalCSV) {

        int count = 0;

        if (additionalCSV != null) {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(additionalCSV, "UTF-8"))) {

                String line;

                while ((line = in.readLine()) != null) {
                    // skip comments & empty lines
                    if (line.startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }

                    count++;

                    String[] parts = line.split(",");
                    switch (parts.length) {
                        case 2:
                            SchemaService.addSchemaDecl(parts[0], parts[1]);
                            break;
                        case 3:
                            SchemaService.addSchemaDecl(parts[0], parts[1], parts[2]);
                            break;
                        default:
                            log.error("Invalid schema declaration in " + additionalCSV + ". Line: " + line);
                            count--;
                            break;
                    }
                }

            } catch (IOException e) {
                log.debug("IOException reading schemas", e);
                return;
            }

            log.info("Loaded " + count + " schema declarations from: " + additionalCSV);
        }

        if (additionalCSV != null) {
            try {
                additionalCSV.close();
            } catch (IOException e) {
                log.debug("IOException: ", e);
            }
        }
    }

    public static void fillSchemaServiceFromLOV() {

        int count = SchemaService.getSize();
        for (SchemaEntry entry : new LOVEndpoint().getAllLOVEntries()) {
            SchemaService.addSchemaDecl(entry.getPrefix(), entry.getVocabularyNamespace(), entry.getVocabularyDefinedBy());
        }

        count = SchemaService.getSize() - count;

        log.info("Loaded " + count + " additional schema declarations from LOV SPARQL Endpoint");
    }



    public static <T> T getFirstItemInCollection(Collection<T> collection) {
        return collection.stream().findFirst().orElseGet(null);
    }
}
