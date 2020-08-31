package org.aksw.rdfunit.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.aksw.rdfunit.sources.SchemaService;
import org.junit.Test;


public class RDFUnitUtilsTest {

  @Test
  public void fillSchemaServiceFromLOVTest() throws IOException {

    int currentSize = SchemaService.getSize();
    RDFUnitUtils.fillSchemaServiceFromLOV();

    assertThat(SchemaService.getSize())
        .isGreaterThan(currentSize);
  }


  @Test
  public void fillSchemaServiceFromDeclTest() throws IOException {

    int currentSize = SchemaService.getSize();
    RDFUnitUtils.fillSchemaServiceFromSchemaDecl();

    assertThat(SchemaService.getSize())
        .isGreaterThan(currentSize);
  }
}