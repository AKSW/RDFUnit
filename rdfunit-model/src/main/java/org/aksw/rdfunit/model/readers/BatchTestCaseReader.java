package org.aksw.rdfunit.model.readers;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.vocabulary.RDF;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Reader a set of Patterns
 *
 * @author Dimitris Kontokostas
 * @since 9/26/15 12:33 PM

 */
@Slf4j
public final class BatchTestCaseReader {

    private BatchTestCaseReader(){}

    public static BatchTestCaseReader create() { return new BatchTestCaseReader();}

    public Collection<GenericTestCase> getTestCasesFromModel(Model model) {
        ConcurrentLinkedQueue<TestCase> testCases = new ConcurrentLinkedQueue<>();

        model.listResourcesWithProperty(RDF.type, RDFUNITv.ManualTestCase).toList()
                .parallelStream()
                .forEach(resource -> testCases.add(ManualTestCaseReader.create().read(resource)));


        model.listResourcesWithProperty(RDF.type, RDFUNITv.PatternBasedTestCase)
                .toList()
                .parallelStream()
                .forEach(resource -> {
                    try {
                        testCases.add(PatternBasedTestCaseReader.create().read(resource));
                    } catch (IllegalArgumentException ex) {
                        log.warn("Cannot create PatternBasedTestCase {}", resource.toString(), ex);
                    }
                });

        return ImmutableList.copyOf(testCases);

    }

}
