package org.aksw.rdfunit.model.impl;

import org.aksw.rdfunit.model.interfaces.ShaclCCParameter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 8:36 PM
 */
public class ShaclCCParameterImplTest {

    private final Resource element = ResourceFactory.createResource("http://example.com/argument/11");
    private final Property predicate = ResourceFactory.createProperty(SHACL.namespace + "arg1");

    private final ShaclCCParameter argDef = ShaclCCParameterImpl.builder().element(element).predicate(predicate).build();

    @Test
    public void testGetElement()  {
        assertThat(argDef.getElement())
                .isEqualTo(element);
    }


    @Test
    public void testIsOptional()  {

        assertThat(argDef.isOptional())
                .isFalse();

        ShaclCCParameterImpl arg2 = ShaclCCParameterImpl.builder().element(element).predicate(predicate).isOptional(true).build();
        assertThat(arg2.isOptional())
                .isTrue();

        ShaclCCParameterImpl arg3 = ShaclCCParameterImpl.builder().element(element).predicate(predicate).isOptional(false).build();
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
        ShaclCCParameterImpl arg2 = ShaclCCParameterImpl.builder().element(element).predicate(predicate).defaultValue(node).build();

        assertThat(arg2.getDefaultValue().get())
                .isEqualTo(node);

    }
}