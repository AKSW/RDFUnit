package org.aksw.rdfunit.webdemo.utils;

import com.vaadin.ui.UI;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;

/**
 * Used to instantiate common variables shared across all sessions
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 4:14 PM

 */
public class CommonAccessUtils {

    private static class PatternsAndGenerators {
        private static volatile RDFUnit rdfUnit;

        static synchronized RDFUnit getInstance() {

            if (rdfUnit == null) {
                rdfUnit = new RDFUnit();
                try {
                    rdfUnit.init();
                } catch (IllegalArgumentException e) {
                    // show error
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

        static synchronized void initialize() {

            if (!initialized) {
                initialized = true;
                //Fill the service schema
                RDFUnitUtils.fillSchemaServiceFromLOV();
                RDFUnitUtils.fillSchemaServiceFromFile(RDFUnitDemoSession.getBaseDir() + "schemaDecl.csv");
            }
        }

    }

    public static void initializeSchemaServices() {
        SchemaServices.initialize();
    }

    public static void pushToClient() {
        try {
            UI.getCurrent().push();
        } catch (Exception e) {
            // Do Nothing
        }
    }
}
