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
 * <p>TestSuite class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 1/6/14 8:33 AM
 * @version $Id: $Id
 */
public class TestSuite {
    private final Collection<TestCase> testCases;

    /**
     * <p>Constructor for TestSuite.</p>
     *
     * @param testCases a {@link java.util.Collection} object.
     */
    public TestSuite(Collection<TestCase> testCases) {
        //First, let's order the TestSuit alphabetically by TestURI,
        //so the results will always be shown in the same order (makes it easier to compare different runs)
        List<TestCase> list = new ArrayList<>(testCases);
        Comparator<TestCase> comparator = (left, right) -> {
            //sorting by AbrTestURI (ignoring case)
            return left.getAbrTestURI().compareToIgnoreCase(right.getAbrTestURI());
        };
        list.sort(comparator); //sort the list with our comparator
        this.testCases = list; //return the sorted list
    }

    /**
     * <p>Getter for the field <code>testCases</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<TestCase> getTestCases() {
        return testCases;
    }

    /**
     * <p>Setter for the field <code>testCases</code>.</p>
     *
     * @param testCases a {@link java.util.Collection} object.
     */
    public void setTestCases(Collection<TestCase> testCases) {
        this.testCases.clear();
        this.testCases.addAll(testCases);
    }

    /**
     * <p>size.</p>
     *
     * @return a int.
     */
    public int size() {
        return testCases.size();
    }

    /**
     * <p>serialize.</p>
     *
     * @param model a {@link org.apache.jena.rdf.model.Model} object.
     * @return a {@link org.apache.jena.rdf.model.Resource} object.
     */
    public Resource serialize(Model model) {
        Resource resource = model.createResource(JenaUtils.getUniqueIri(PrefixNSService.getNSFromPrefix("ruts")))
                .addProperty(RDF.type, model.createResource(PrefixNSService.getURIFromAbbrev("rut:TestSuite")))
                .addProperty(RDF.type, PROV.Collection);

        for (TestCase tc : testCases) {
            resource.addProperty(PROV.hadMember, model.createResource(tc.getTestURI()));
        }
        // TODO check whether to dump the complete test

        return resource;
    }
}
