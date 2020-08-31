package org.aksw.rdfunit.webdemo.utils;

import com.vaadin.ui.UI;
import java.io.IOException;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.utils.RDFUnitUtils;

/**
 * Used to instantiate common variables shared across all sessions
 */
public class CommonAccessUtils {

  public static RDFUnit getRDFUnit() {
    return PatternsAndGenerators.getInstance();
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

  private static class PatternsAndGenerators {

    private static volatile RDFUnit rdfUnit;

    static synchronized RDFUnit getInstance() {

      if (rdfUnit == null) {

        try {
          rdfUnit = RDFUnit.createWithOwlAndShacl();
          rdfUnit.init();

        } catch (IllegalArgumentException e) {
          // show error
        }
      }
      return rdfUnit;
    }
  }

  private static class SchemaServices {

    private static volatile boolean initialized = false;

    static synchronized void initialize() {

      if (!initialized) {
        initialized = true;
        //Fill the service schema
        try {
          RDFUnitUtils.fillSchemaServiceFromLOV();
          RDFUnitUtils.fillSchemaServiceFromSchemaDecl();
          RDFUnitUtils.fillSchemaServiceWithStandardVocabularies();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }

  }
}
