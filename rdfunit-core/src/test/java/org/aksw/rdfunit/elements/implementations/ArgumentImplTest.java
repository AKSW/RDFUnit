package org.aksw.rdfunit.elements.implementations;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.XSD;
import org.aksw.rdfunit.elements.interfaces.Argument;
import org.aksw.rdfunit.enums.ValueKind;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 8:36 PM
 */
public class ArgumentImplTest {

    private final Resource element = ResourceFactory.createResource("http://example.com/argument/11");
    private final Resource predicate = ResourceFactory.createResource(SHACL.namespace + "arg1");

    private final Argument argDef = new ArgumentImpl.Builder(element, predicate).build();

    @Test
    public void testGetResource() throws Exception {
        assertEquals(argDef.getResource().get(), element);

        ArgumentImpl arg2 = new ArgumentImpl.Builder(predicate).build();
        assertFalse(arg2.getResource().isPresent());

    }

    @Test
    public void testGetComment() throws Exception {
        assert argDef.getComment().isEmpty();

        final String comment = "asdf";
        ArgumentImpl arg2 = new ArgumentImpl.Builder(predicate).setComment(comment).build();
        assert arg2.getComment().equals(comment);

    }

    @Test
    public void testIsOptional() throws Exception {

        assertFalse(argDef.isOptional());

        ArgumentImpl arg2 = new ArgumentImpl.Builder(predicate).setOptional(true).build();
        assertTrue(arg2.isOptional());

        ArgumentImpl arg3 = new ArgumentImpl.Builder(predicate).setOptional(false).build();
        assertFalse(arg3.isOptional());
    }

    @Test
    public void testGetPredicate() throws Exception {
        assert argDef.getPredicate().equals(predicate);
    }

    @Test
    public void testGetValueType() throws Exception {
        assertFalse(argDef.getValueType().isPresent());

        Resource valueType = XSD.xstring;
        ArgumentImpl arg2 = new ArgumentImpl.Builder(predicate).setValueType(valueType, ValueKind.DATATYPE).build();

        assertEquals(arg2.getValueType().get(), valueType);
        assertEquals(arg2.getValueKind().get(), ValueKind.DATATYPE);

        ArgumentImpl arg3 = new ArgumentImpl.Builder(predicate).setValueType(valueType, ValueKind.IRI).build();

        assertEquals(arg3.getValueKind().get(), ValueKind.IRI);

    }

    @Test
    public void testGetValueKind() throws Exception {
        assertFalse(argDef.getValueKind().isPresent());
    }

    @Test
    public void testGetDefaultValue() throws Exception {
        assertFalse(argDef.getDefaultValue().isPresent());

        RDFNode node = ResourceFactory.createResource("http://example.com");
        ArgumentImpl arg2 = new ArgumentImpl.Builder(predicate).setDefaultValue(node).build();

        assertEquals(arg2.getDefaultValue().get(), node);

    }
}