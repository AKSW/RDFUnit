package org.aksw.rdfunit.validate.integration;

import java.util.Arrays;
import java.util.Collection;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ShaclIntegrationTest {

  @Parameterized.Parameter
  public String testSource;
  @Parameterized.Parameter(value = 1)
  public int expectedErrors;

  @Parameterized.Parameters(name = "{index}: {0} ({1})")
  public static Collection<Object[]> resources() {
    return Arrays.asList(new Object[][]{
        {"shacl/sh.not-correct.ttl", 0},
        {"shacl/sh.not-wrong.ttl", 2},
        {"shacl/sh.xone-correct.ttl", 0},
        {"shacl/sh.xone-wrong.ttl", 9},
        {"shacl/sh.and-correct.ttl", 0},
        {"shacl/sh.and-wrong.ttl", 8},
        {"shacl/sh.or-correct.ttl", 0},
        {"shacl/sh.or-wrong.ttl", 28},

        {"shacl/sh.class-correct.ttl", 0},
        {"shacl/sh.class-wrong.ttl", 1},
        {"shacl/sh.class-correct-node.ttl", 0},
        {"shacl/sh.class-wrong-node.ttl", 2},

        {"shacl/sh.datatype-correct.ttl", 0},
        {"shacl/sh.datatype-wrong.ttl", 1},
        {"shacl/sh.datatype-correct-node.ttl", 0},
        {"shacl/sh.datatype-wrong-node.ttl", 1},

        {"shacl/sh.uniqueLang-correct.ttl", 0},
        {"shacl/sh.uniqueLang-wrong.ttl", 1},
        {"shacl/sh.languageIn-correct.ttl", 0},
        {"shacl/sh.languageIn-wrong.ttl", 1},

        {"shacl/sh.equals-correct.ttl", 0},
        {"shacl/sh.equals-wrong.ttl", 4},
        {"shacl/sh.disjoint-correct.ttl", 0},
        {"shacl/sh.disjoint-wrong.ttl", 4},

        {"shacl/sh.hasValue-correct.ttl", 0},
        {"shacl/sh.hasValue-wrong.ttl", 2},
        {"shacl/sh.in-correct.ttl", 0},
        {"shacl/sh.in-wrong.ttl", 2},

        {"shacl/sh.minCount-correct.ttl", 0},
        {"shacl/sh.maxCount-correct.ttl", 0},
        {"shacl/sh.minCount-wrong.ttl", 3},
        {"shacl/sh.maxCount-wrong.ttl", 3},

        {"shacl/sh.min.maxLength-correct.ttl", 0},
        {"shacl/sh.min.maxLength-wrong.ttl", 2},
        {"shacl/sh.min.maxInclusive-correct.ttl", 0},
        {"shacl/sh.min.maxInclusive-wrong.ttl", 2},
        {"shacl/sh.min.maxExclusive-correct.ttl", 0},
        {"shacl/sh.min.maxExclusive-wrong.ttl", 2},

        {"shacl/sh.pattern-correct.ttl", 0},
        {"shacl/sh.pattern-wrong.ttl", 2},

        {"shacl/sh.node-correct.ttl", 0},
        {"shacl/sh.node-wrong.ttl", 3},

        {"shacl/sh.nodeKind-correct.ttl", 0},
        {"shacl/sh.nodeKind-wrong-IRI.ttl", 2},
        {"shacl/sh.nodeKind-wrong-Literal.ttl", 2},
        {"shacl/sh.nodeKind-wrong-BlankNode.ttl", 2},
        {"shacl/sh.nodeKind-wrong-BlankNodeOrIri.ttl", 1},
        {"shacl/sh.nodeKind-wrong-BlankNodeOrLiteral.ttl", 1},
        {"shacl/sh.nodeKind-wrong-IriOrLiteral.ttl", 1},
    });
  }

  @Test
  public void test() throws RdfReaderException {

    IntegrationTestHelper.testMap(
        IntegrationTestHelper.getResourcePrefix() + testSource,
        expectedErrors,
        IntegrationTestHelper.getShaclTestSuite(),
        IntegrationTestHelper.getShaclSchemaSource());
  }
}
