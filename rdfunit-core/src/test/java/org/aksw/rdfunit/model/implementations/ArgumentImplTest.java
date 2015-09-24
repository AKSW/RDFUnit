package org.aksw.rdfunit.model.implementations;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.XSD;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.vocabulary.SHACL;
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
    private final Resource predicate = ResourceFactory.createResource(SHACL.namespace + "arg1");

    private final Argument argDef = new ArgumentImpl.Builder(element).setPredicate(predicate).build();

    @Test
    public void testGetResource() throws Exception {
        assertThat(argDef.getResource().get())
                .isEqualTo(element);
    }

    @Test
    public void testGetComment() throws Exception {
        assertThat(argDef.getComment()).isEmpty();

        final String comment = "asdf";
        ArgumentImpl arg2 = new ArgumentImpl.Builder(element).setPredicate(predicate).setComment(comment).build();
        assertThat(arg2.getComment())
                .isEqualTo(comment);

    }

    @Test
    public void testIsOptional() throws Exception {

        assertThat(argDef.isOptional())
                .isFalse();

        ArgumentImpl arg2 = new ArgumentImpl.Builder(element).setPredicate(predicate).setOptional(true).build();
        assertThat(arg2.isOptional())
                .isTrue();

        ArgumentImpl arg3 = new ArgumentImpl.Builder(element).setPredicate(predicate).setOptional(false).build();
        assertThat(arg3.isOptional())
                .isFalse();
    }

    @Test
    public void testGetPredicate() throws Exception {
        assertThat(argDef.getPredicate())
                .isEqualTo(predicate);
    }

    @Test
    public void testGetValueType() throws Exception {
        assertThat(argDef.getValueType().isPresent()).isFalse();

        Resource valueType = XSD.xstring;
        ArgumentImpl arg2 = new ArgumentImpl.Builder(element).setPredicate(predicate).setValueType(valueType, ValueKind.DATATYPE).build();

        assertThat(arg2.getValueType().get())
                .isEqualTo(valueType);
        assertThat(arg2.getValueKind().get())
                .isEqualTo(ValueKind.DATATYPE);

        ArgumentImpl arg3 = new ArgumentImpl.Builder(element).setPredicate(predicate).setValueType(valueType, ValueKind.IRI).build();

        assertThat(arg3.getValueKind().get())
                .isEqualTo(ValueKind.IRI);

    }

    @Test
    public void testGetValueKind() throws Exception {
        assertThat(argDef.getValueKind().isPresent())
                .isFalse();
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        assertThat(argDef.getDefaultValue().isPresent())
                .isFalse();

        RDFNode node = ResourceFactory.createResource("http://example.com");
        ArgumentImpl arg2 = new ArgumentImpl.Builder(element).setPredicate(predicate).setDefaultValue(node).build();

        assertThat(arg2.getDefaultValue().get())
                .isEqualTo(node);

    }
}