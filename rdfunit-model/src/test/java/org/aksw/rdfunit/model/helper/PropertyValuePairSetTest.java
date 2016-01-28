package org.aksw.rdfunit.model.helper;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 14/1/2016 9:30 πμ
 */
public class PropertyValuePairSetTest {

    @Test
    public void testGetAnnotations() throws Exception {

        //add rdf:type rdfs:Class
        PropertyValuePairSet.PropertyValuePairSetBuilder an1 = PropertyValuePairSet.builder();
        an1.annotation(PropertyValuePair.create(RDF.type, RDFS.Class));
        assertThat(an1.build().getAnnotations().size())
                .isEqualTo(1);
        List<PropertyValuePair> annotations = new ArrayList<>(an1.build().getAnnotations());
        assertThat(annotations.get(0).getProperty())
                .isEqualTo(RDF.type);
        assertThat(annotations.get(0).getValues().size())
                .isEqualTo(1);
        assertThat(((new ArrayList<RDFNode>(annotations.get(0).getValues())).get(0)))
                .isEqualTo(RDFS.Class);

        //add rdf:type rdfs:Literal
        an1.annotation(PropertyValuePair.create(RDF.type, RDFS.Literal));
        assertThat(an1.build().getAnnotations().size())
                .isEqualTo(1);
        annotations = new ArrayList<>(an1.build().getAnnotations());
        assertThat(annotations.get(0).getProperty())
                .isEqualTo(RDF.type);
        assertThat(annotations.get(0).getValues().size())
                .isEqualTo(2);

        //add rdf:subject rdfs:Datatype
        an1.annotation(PropertyValuePair.create(RDF.subject, RDFS.Datatype));
        assertThat(an1.build().getAnnotations().size())
                .isEqualTo(2);


    }
}