package org.aksw.rdfunit.model.impl.shacl;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.shacl.ShapePath;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.path.PathParser;
import org.junit.Test;


/**
 * @author Dimitris Kontokostas
 * @since 7/8/17
 */
@Slf4j
public class ShapePathImplTest {

  @Test
  public void testSimplePath() {
    ShapePath p = fromString("<http://example.com/p1>");
    assertThat(p.isPredicatePath()).isTrue();
    log.info("Simple path: {}", p.asSparqlPropertyPath());
  }

  @Test
  public void testComplexPath() {
    ShapePath p = fromString(
        "<http://example.com/p1> / <http://example.com/p2> / <http://example.com/p3>");
    assertThat(p.isPredicatePath()).isFalse();
    assertThat(p.getUsedProperties()).hasSize(3);
    log.info("Complex path: {}", p.asSparqlPropertyPath());
  }


  @Test
  public void testComplexPathStar() {
    ShapePath p = fromString("<http://example.com/p1>+ / <http://example.com/p2>*");
    assertThat(p.isPredicatePath()).isFalse();
    assertThat(p.getUsedProperties()).hasSize(2);
    log.info("Complex path: {}", p.asSparqlPropertyPath());
  }

  @Test
  public void testEquality() {
    String property = "<http://example.com/p1>";
    ShapePath path = fromString(property);
    assertThat(property).isEqualTo(path.asSparqlPropertyPath());
    assertThat(path.getUsedProperties()).hasSize(1);
  }


  @Test(expected = Exception.class)
  public void testInvalidPath() {
    ShapePath p = fromString("<http://example.com/p1> / /<http://example.com/p2>");

  }


  private ShapePath fromString(String propertyPathString) {
    return new ShapePathImpl(ResourceFactory.createResource("http://example.com/"),
        PathParser.parse(propertyPathString, PrefixMapping.Standard));
  }

}