package org.aksw.rdfunit.utils;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.sources.SchemaService;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@Slf4j
public final class RDFUnitUtils {

    private RDFUnitUtils() {
    }

    public static void fillSchemaServiceFromFile(String additionalCSV) throws IOException {

        try (InputStream inputStream = new FileInputStream(additionalCSV)) {
            fillSchemaServiceFromFile(inputStream);
        } catch (IOException e) {
            log.error("Error loading schemas from " + additionalCSV + "!", e);
            throw e;
        }
    }

    private static void fillSchemaServiceFromResource(String additionalCSVAsResource) throws IOException {

        URL resourceUrl = Resources.getResource(additionalCSVAsResource);
        try (
            InputStream inputStream = resourceUrl.openStream()) {
            fillSchemaServiceFromFile(inputStream);
        } catch (IOException e) {
            log.error("Error loading schemas from " + additionalCSVAsResource + "!", e);
            throw e;
        }
    }

    private static void fillSchemaServiceFromFile(InputStream additionalCSV) throws IOException {

        int count = 0;

        if (additionalCSV != null) {

            try (BufferedReader in = new BufferedReader(new InputStreamReader(additionalCSV, StandardCharsets.UTF_8))) {

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


    public static void fillSchemaServiceFromSchemaDecl() throws IOException {
        log.info("Adding manual schema entries (or overriding LOV)!");
        RDFUnitUtils.fillSchemaServiceFromResource("org/aksw/rdfunit/configuration/schemaDecl.csv");
    }

    public static void fillSchemaServiceFromLOV() throws IOException {

        log.info("Loading cached schema entries from LOV!");
        RDFUnitUtils.fillSchemaServiceFromResource("org/aksw/rdfunit/configuration/schemaLOV.csv");
    }



    public static <T> T getFirstItemInCollection(Collection<T> collection) {
        return collection.stream().findFirst().orElseGet(null);
    }
}
