package org.aksw.rdfunit.webdemo;

import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.Utils.RDFUnitUtils;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * Used to instantiate common variables shared across all sessions
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 4:14 PM
 */
public class RDFUnitDemoCommons {

    private static class PatternsAndGenerators {
        private static volatile RDFUnit rdfUnit;

        static RDFUnit getInstance() {

            if (rdfUnit == null) {
                synchronized (PatternsAndGenerators.class) {
                    if (rdfUnit == null) {
                        rdfUnit = new RDFUnit();
                        try {
                            rdfUnit.init();
                        } catch (TripleReaderException e) {
                            // show error
                        }
                    }
                }
            }
            return rdfUnit;
        }
    }

    public static RDFUnit getRDFUnit() {
        return PatternsAndGenerators.getInstance();
    }

    private static class SchemaServices {
        private static volatile boolean initialized = false;

        static void initialize() {
            if (!initialized) {
                synchronized (PatternsAndGenerators.class) {
                    if (!initialized) {
                        initialized = true;
                        //Fill the service schema
                        RDFUnitUtils.fillSchemaServiceFromLOV();
                        RDFUnitUtils.fillSchemaServiceFromFile(RDFUnitDemoSession.getBaseDir() + "schemaDecl.csv");
                    }
                }
            }
        }

    }

    public static void initializeSchemaServices() {
        SchemaServices.initialize();
    }
}
