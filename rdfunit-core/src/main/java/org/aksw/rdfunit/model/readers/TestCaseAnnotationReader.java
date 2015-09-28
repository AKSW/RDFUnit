package org.aksw.rdfunit.model.readers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.enums.TestAppliesTo;
import org.aksw.rdfunit.enums.TestGenerationType;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.TestCaseAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

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
public final class TestCaseAnnotationReader implements ElementReader<TestCaseAnnotation> {

    private TestCaseAnnotationReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link TestCaseAnnotationReader} object.
     */
    public static TestCaseAnnotationReader create() { return new TestCaseAnnotationReader();}


    /** {@inheritDoc} */
    @Override
    public TestCaseAnnotation read(Resource resource) {
        checkNotNull(resource, "Cannot read a TestCaseAnnotation from a null resource");

        String description = null;
        TestAppliesTo appliesTo = null;
        TestGenerationType generated = null;
        String source = null;
        RLOGLevel testCaseLogLevel = null;//RLOGLevel.resolve(qs.get("testCaseLogLevel").toString());
        Collection<String> referencesLst = new ArrayList<>();
        Collection<ResultAnnotation> testAnnotations = new ArrayList<>();
        String testGenerator = null;

        int count = 0; // used to count duplicates

        //description
        for (Statement smt : resource.listProperties(DCTerms.description).toList()) {
            checkArgument(++count == 1, "Cannot have more than one descriptions in TestCaseAnnotation %s", resource.getURI());
            description = smt.getObject().asLiteral().getLexicalForm();
        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.appliesTo).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:appliesTo TestCaseAnnotation %s", resource.getURI());
            appliesTo = TestAppliesTo.resolve( smt.getObject().asResource().getURI());
        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.source).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:source TestCaseAnnotation %s", resource.getURI());
            source = smt.getObject().asResource().getURI();
        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.generated).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:generated TestCaseAnnotation %s", resource.getURI());
            generated = TestGenerationType.resolve( smt.getObject().asResource().getURI());
        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.testCaseLogLevel).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:testCaseLogLevel TestCaseAnnotation %s", resource.getURI());
            testCaseLogLevel = RLOGLevel.resolve( smt.getObject().asResource().getURI());
        }

        //references
        for (Statement smt : resource.listProperties(RDFUNITv.references).toList()) {
            referencesLst.add(smt.getObject().asResource().getURI());
        }

        //annotations
        for (Statement smt : resource.listProperties(RDFUNITv.resultAnnotation).toList()) {
            testAnnotations.add(ResultAnnotationReader.create().read(smt.getResource()));
        }

        return new TestCaseAnnotation(
                resource, generated,
                testGenerator,
                appliesTo,
                source,
                referencesLst,
                description,
                testCaseLogLevel,
                testAnnotations
        ) ;

        //return TestGeneratorImpl.createTAG(resource, description, query, pattern, generatorAnnotations);
    }
}
