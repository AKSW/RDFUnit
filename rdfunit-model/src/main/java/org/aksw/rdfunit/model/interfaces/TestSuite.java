package org.aksw.rdfunit.model.interfaces;

import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.PROV;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author Dimitris Kontokostas
 * @since 1/6/14 8:33 AM
 */
public class TestSuite {
    private final Collection<GenericTestCase> testCases;

    public TestSuite(Collection<GenericTestCase> testCases) {
        //First, let's order the TestSuit alphabetically by TestURI,
        //so the results will always be shown in the same order (makes it easier to compare different runs)
        List<GenericTestCase> list = new ArrayList<>(testCases);
        Comparator<GenericTestCase> comparator = (left, right) -> {
            //sorting by AbrTestURI (ignoring case)
            return left.getAbrTestURI().compareToIgnoreCase(right.getAbrTestURI());
        };
        list.sort(comparator); //sort the list with our comparator
        this.testCases = list; //return the sorted list
    }

    public Collection<GenericTestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(Collection<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    public int size() {
        return testCases.size();
    }

    public Resource serialize(Model model) {
        Resource resource = model.createResource(JenaUtils.getUniqueIri(PrefixNSService.getNSFromPrefix("ruts")))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:TestSuite")))
                .addProperty(RDF.type, PROV.Collection);

        for (GenericTestCase tc : testCases) {
            resource.addProperty(PROV.hadMember, model.createResource(tc.getTestURI()));
        }
        // TODO check whether to dump the complete test

        return resource;
    }
}
