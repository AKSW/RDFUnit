package org.aksw.rdfunit.model.readers.results;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.helper.PropertyValuePairSet;
import org.aksw.rdfunit.model.impl.results.ShaclTestCaseResultImpl;
import org.aksw.rdfunit.model.interfaces.results.ShaclTestCaseResult;
import org.aksw.rdfunit.model.interfaces.results.SimpleShaclTestCaseResult;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads an argument
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class ShaclTestCaseResultReader implements ElementReader<ShaclTestCaseResult> {

    private ShaclTestCaseResultReader(){}

    /**
     * <p>create.</p>
     *
     * @return a {@link ShaclTestCaseResultReader} object.
     */
    public static ShaclTestCaseResultReader create() { return new ShaclTestCaseResultReader();}

    /** {@inheritDoc} */
    @Override
    public ShaclTestCaseResult read(final Resource resource) {
        checkNotNull(resource);

        SimpleShaclTestCaseResult test = ShaclSimpleTestCaseResultReader.create().read(resource);

        PropertyValuePairSet.PropertyValuePairSetBuilder annotationSetBuilder = PropertyValuePairSet.builder();

        Set<Property> excludesProperties = ImmutableSet.of(SHACL.severity, SHACL.focusNode, SHACL.message, PROV.wasGeneratedBy, DCTerms.date);
        Set<Resource> excludesTypes = ImmutableSet.of(SHACL.ValidationResult, RDFUNITv.TestCaseResult);

        for (Statement smt: resource.listProperties().toList()) {
            if (excludesProperties.contains(smt.getPredicate())) {
                continue;
            }
            if (RDF.type.equals(smt.getPredicate()) && excludesTypes.contains(smt.getObject().asResource())) {
                continue;
            }
            annotationSetBuilder.annotation(PropertyValuePair.create(smt.getPredicate(), smt.getObject()));
        }

        return new ShaclTestCaseResultImpl(resource, test.getTestCaseUri(), test.getSeverity(), test.getMessage(), test.getTimestamp(), test.getFailingResource(), annotationSetBuilder.build().getAnnotations());
    }
}
