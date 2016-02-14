package org.aksw.rdfunit.webdemo.utils;

import com.vaadin.ui.UI;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;

/**
 * Used to instantiate common variables shared across all sessions
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 4:14 PM
 * @version $Id: $Id
 */
public class CommonAccessUtils {

    private static class PatternsAndGenerators {
        private static volatile RDFUnit rdfUnit;

        static synchronized RDFUnit getInstance() {

            if (rdfUnit == null) {
                rdfUnit = new RDFUnit();
                try {
                    rdfUnit.init();
                } catch (RdfReaderException e) {
                    // show error
                }
            }
            return rdfUnit;
        }
    }

    /**
     * <p>getRDFUnit.</p>
     *
     * @return a {@link org.aksw.rdfunit.RDFUnit} object.
     */
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

    /**
     * <p>initializeSchemaServices.</p>
     */
    public static void initializeSchemaServices() {
        SchemaServices.initialize();
    }

    /**
     * <p>pushToClient.</p>
     */
    public static void pushToClient() {
        try {
            UI.getCurrent().push();
        } catch (Exception e) {
            // Do Nothing
        }
    }
}
