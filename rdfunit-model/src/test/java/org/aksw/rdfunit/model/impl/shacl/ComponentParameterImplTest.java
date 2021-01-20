package org.aksw.rdfunit.model.impl.shacl;

import static org.assertj.core.api.Assertions.assertThat;

import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 8:36 PM
 */
public class ComponentParameterImplTest {

  private final Resource element = ResourceFactory.createResource("http://example.com/argument/11");
  private final Property predicate = ResourceFactory.createProperty(SHACL.namespace + "arg1");

  private final ComponentParameter argDef = ComponentParameterImpl.builder().element(element)
      .predicate(predicate).build();

  @Test
  public void testGetElement() {
    assertThat(argDef.getElement())
        .isEqualTo(element);
  }


  @Test
  public void testIsOptional() {

    assertThat(argDef.isOptional())
        .isFalse();

    ComponentParameterImpl arg2 = ComponentParameterImpl.builder().element(element)
        .predicate(predicate).isOptional(true).build();
    assertThat(arg2.isOptional())
        .isTrue();

    ComponentParameterImpl arg3 = ComponentParameterImpl.builder().element(element)
        .predicate(predicate).isOptional(false).build();
    assertThat(arg3.isOptional())
        .isFalse();
  }

  @Test
  public void testGetPredicate() {
    assertThat(argDef.getPredicate())
        .isEqualTo(predicate);
  }


  @Test
  public void testGetDefaultValue() {
    assertThat(argDef.getDefaultValue().isPresent())
        .isFalse();

    RDFNode node = ResourceFactory.createResource("http://example.com");
    ComponentParameterImpl arg2 = ComponentParameterImpl.builder().element(element)
        .predicate(predicate).defaultValue(node).build();

    assertThat(arg2.getDefaultValue().get())
        .isEqualTo(node);

  }
}