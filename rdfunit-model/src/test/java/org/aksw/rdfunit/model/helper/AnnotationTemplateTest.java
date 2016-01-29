package org.aksw.rdfunit.model.helper;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/28/15 9:10 PM
 */
public class AnnotationTemplateTest {


    private RDFNode rdfNode1 = ResourceFactory.createResource();
    private RDFNode rdfNode2 = ResourceFactory.createResource();
    private Collection<RDFNode> rdfNodes = Arrays.asList(rdfNode1, rdfNode2);

    private PropertyValuePair sa1 = PropertyValuePair.create(RDF.type, rdfNode1);
    private PropertyValuePair sa2 = PropertyValuePair.create(RDF.predicate, rdfNodes);


    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testAddTemplateMin() throws Exception {
        AnnotationTemplate at = AnnotationTemplate.create();

        assertThat(at.existsInTemplate(sa1)).isFalse();
        at.addTemplateMin(RDF.type, 2);
        assertThat(at.existsInTemplate(sa1)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa1)).isFalse();

        assertThat(at.existsInTemplate(sa2)).isFalse();
        at.addTemplateMin(RDF.predicate, 2);
        assertThat(at.existsInTemplate(sa2)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa2)).isTrue();

    }

    @Test
    public void testAddTemplateMax() throws Exception {
        AnnotationTemplate at = AnnotationTemplate.create();

        assertThat(at.existsInTemplate(sa1)).isFalse();
        at.addTemplateMax(RDF.type, 1);
        assertThat(at.existsInTemplate(sa1)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa1)).isTrue();


        assertThat(at.existsInTemplate(sa2)).isFalse();
        assertThat(at.isValidAccordingToTemplate(sa2)).isFalse();
        at.addTemplateMax(RDF.predicate, 1);
        assertThat(at.existsInTemplate(sa2)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa2)).isFalse();

    }

    @Test
    public void testAddTemplateMinMax() throws Exception {

        AnnotationTemplate at = AnnotationTemplate.create();

        assertThat(at.existsInTemplate(sa1)).isFalse();
        at.addTemplateMinMax(RDF.type, 1, 2);
        assertThat(at.existsInTemplate(sa1)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa1)).isTrue();


        assertThat(at.existsInTemplate(sa2)).isFalse();
        at.addTemplateMinMax(RDF.predicate, 3, 5);
        assertThat(at.existsInTemplate(sa2)).isTrue();
        assertThat(at.isValidAccordingToTemplate(sa2)).isFalse();

    }

    @Test
    public void testGetProprtiesAsSet() throws Exception {

        AnnotationTemplate at = AnnotationTemplate.create();
        at.addTemplateMinMax(RDF.type, 1,2);
        at.addTemplateMinMax(RDF.predicate, 3,5);

        assertThat(at.getProprtiesAsSet()).isNotEmpty();
        assertThat(at.getProprtiesAsSet().size()).isEqualTo(2);
    }

}