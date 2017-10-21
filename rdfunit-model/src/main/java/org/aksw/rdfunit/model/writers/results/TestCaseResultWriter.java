package org.aksw.rdfunit.model.writers.results;

import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.*;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;


public class TestCaseResultWriter implements ElementWriter {

    private final TestCaseResult testCaseResult;
    private final String executionUri;

    private TestCaseResultWriter(TestCaseResult testCaseResult, String executionUri) {
        this.testCaseResult = checkNotNull(testCaseResult);
        this.executionUri = checkNotNull(executionUri);
    }

    public static TestCaseResultWriter create(TestCaseResult testCaseResult, String executionUri) {return new TestCaseResultWriter(testCaseResult, executionUri);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource;
        if (testCaseResult.getElement().isAnon()) {
            resource = model.createResource(JenaUtils.getUniqueIri(executionUri + "/"));
        } else {
            resource = ElementWriter.copyElementResourceInModel(testCaseResult, model);
        }

        // general properties
        resource
                .addProperty(RDF.type, RDFUNITv.TestCaseResult)
                .addProperty(PROV.wasGeneratedBy, model.createResource(executionUri))
                .addProperty(RDFUNITv.testCase, model.createResource(testCaseResult.getTestCaseUri()))
                .addProperty(DCTerms.date, model.createTypedLiteral(testCaseResult.getTimestamp(), XSDDatatype.XSDdateTime));

        if (testCaseResult instanceof StatusTestCaseResult) {
            resource
                    .addProperty(RDF.type, RDFUNITv.StatusTestCaseResult)
                    .addProperty(RDFUNITv.resultStatus, model.createResource(((StatusTestCaseResult) testCaseResult).getStatus().getUri()))
                    .addProperty(DCTerms.description, testCaseResult.getMessage())
                    .addProperty(RDFUNITv.testCaseLogLevel, model.createResource(testCaseResult.getSeverity().getUri()))
                    ;
        }

        if (testCaseResult instanceof AggregatedTestCaseResult) {
            resource
                    .addProperty(RDF.type, RDFUNITv.AggregatedTestResult)
                    .addProperty(RDFUNITv.resultCount,
                            ResourceFactory.createTypedLiteral(Long.toString(((AggregatedTestCaseResult) testCaseResult).getErrorCount()), XSDDatatype.XSDinteger))
                    .addProperty(RDFUNITv.resultPrevalence,
                            ResourceFactory.createTypedLiteral(Long.toString(((AggregatedTestCaseResult) testCaseResult).getPrevalenceCount().orElse(-1L)), XSDDatatype.XSDinteger));
        }

        if (testCaseResult instanceof ShaclLiteTestCaseResult) {
            resource.addProperty(RDF.type, SHACL.ValidationResult)
            ;
        }

        if (testCaseResult instanceof ShaclTestCaseResult) {

            Collection<PropertyValuePair> annotations = ((ShaclTestCaseResult) testCaseResult).getResultAnnotations();
            for (PropertyValuePair annotation : annotations) {
                for (RDFNode rdfNode : annotation.getValues()) {
                    resource.addProperty(annotation.getProperty(), rdfNode);
                    if (rdfNode.isAnon() && annotation.getProperty().equals(SHACL.resultPath)) {
                        resource.getModel().add(rdfNode.getModel());
                    }
                }
            }
            boolean containsMessage = annotations.stream().anyMatch(an -> an.getProperty().equals(SHACL.message));
            if (!containsMessage) {
                resource.addProperty(SHACL.message, testCaseResult.getMessage());
            }
            boolean containsFocusNode = annotations.stream().anyMatch(an -> an.getProperty().equals(SHACL.focusNode));
            if (!containsFocusNode) {
                resource.addProperty(SHACL.focusNode, ResourceFactory.createResource(((ShaclTestCaseResult) testCaseResult).getFailingResource()));
            }
            boolean containsSeverity = annotations.stream().anyMatch(an -> an.getProperty().equals(SHACL.severity));
            if (!containsSeverity) {
                resource.addProperty(SHACL.severity, ResourceFactory.createResource(testCaseResult.getSeverity().getUri()));
            }


        }


        return resource;
    }
}
