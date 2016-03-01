package org.aksw.rdfunit.model.impl;

import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.XSD;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 8:36 PM
 */
public class ArgumentImplTest {

    private final Resource element = ResourceFactory.createResource("http://example.com/argument/11");
    private final Property predicate = ResourceFactory.createProperty(SHACL.namespace + "arg1");

    private final Argument argDef = ArgumentImpl.builder().element(element).predicate(predicate).comment("").build();

    @Test
    public void testGetElement()  {
        assertThat(argDef.getElement())
                .isEqualTo(element);
    }

    @Test
    public void testGetComment() {
        assertThat(argDef.getComment()).isEmpty();

        final String comment = "dog";
        ArgumentImpl arg2 = ArgumentImpl.builder().element(element).predicate(predicate).comment(comment).build();
        assertThat(arg2.getComment())
                .isEqualTo(comment);

    }

    @Test
    public void testIsOptional()  {

        assertThat(argDef.isOptional())
                .isFalse();

        ArgumentImpl arg2 = ArgumentImpl.builder().element(element).predicate(predicate).comment("").isOptional(true).build();
        assertThat(arg2.isOptional())
                .isTrue();

        ArgumentImpl arg3 = ArgumentImpl.builder().element(element).predicate(predicate).comment("").isOptional(false).build();
        assertThat(arg3.isOptional())
                .isFalse();
    }

    @Test
    public void testGetPredicate() {
        assertThat(argDef.getPredicate())
                .isEqualTo(predicate);
    }

    @Test
    public void testGetValueType()  {
        assertThat(argDef.getValueType().isPresent()).isFalse();

        Resource valueType = XSD.xstring;
        ArgumentImpl arg2 = ArgumentImpl.builder().element(element).predicate(predicate).comment("").valueType(valueType).valueKind(ValueKind.DATATYPE).build();

        assertThat(arg2.getValueType().get())
                .isEqualTo(valueType);
        assertThat(arg2.getValueKind().get())
                .isEqualTo(ValueKind.DATATYPE);

        ArgumentImpl arg3 = ArgumentImpl.builder().element(element).predicate(predicate).comment("").valueType(valueType).valueKind(ValueKind.IRI).build();

        assertThat(arg3.getValueKind().get())
                .isEqualTo(ValueKind.IRI);

    }

    @Test
    public void testGetValueKind() {
        assertThat(argDef.getValueKind().isPresent())
                .isFalse();
    }

    @Test
    public void testGetDefaultValue() {
        assertThat(argDef.getDefaultValue().isPresent())
                .isFalse();

        RDFNode node = ResourceFactory.createResource("http://example.com");
        ArgumentImpl arg2 = ArgumentImpl.builder().element(element).predicate(predicate).comment("").defaultValue(node).build();

        assertThat(arg2.getDefaultValue().get())
                .isEqualTo(node);

    }
}