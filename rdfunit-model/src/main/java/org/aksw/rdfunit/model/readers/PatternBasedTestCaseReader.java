package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.PatternBasedTestCaseImpl;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.TestCase;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.services.PatternService;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class PatternBasedTestCaseReader implements ElementReader<TestCase> {

    private PatternBasedTestCaseReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternBasedTestCaseReader} object.
     */
    public static PatternBasedTestCaseReader create() { return new PatternBasedTestCaseReader();}


    /** {@inheritDoc} */
    @Override
    public TestCase read(Resource resource) {
        checkNotNull(resource, "Cannot read a PatternBasedTestCase from a null resource");


        Pattern pattern = null;
        Collection<Binding> bindings = new ArrayList<>();

        int count = 0; // used to count duplicates

        //pattern IRI
        for (Statement smt : resource.listProperties(RDFUNITv.basedOnPattern).toList()) {
            checkArgument(++count == 1, "Cannot have more than one pattern references in PatternBasedTestCase %s", resource.getURI());
            pattern = PatternService.getPatternFromIRI(smt.getObject().asResource().getURI());

        }

        for (Statement smt : resource.listProperties(RDFUNITv.binding).toList()) {
            bindings.add(BindingReader.create(pattern).read(smt.getObject().asResource())) ;

        }

        TestCaseAnnotation annotation = TestCaseAnnotationReader.create().read(resource);

        return new PatternBasedTestCaseImpl(resource, annotation, pattern, bindings);
    }
}
