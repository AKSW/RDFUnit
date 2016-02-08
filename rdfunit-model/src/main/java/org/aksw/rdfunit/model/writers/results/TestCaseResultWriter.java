package org.aksw.rdfunit.model.writers.results;

import org.aksw.rdfunit.model.helper.PropertyValuePair;
import org.aksw.rdfunit.model.interfaces.results.*;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.PROV;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.RLOG;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

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
                            ResourceFactory.createTypedLiteral("" + ((AggregatedTestCaseResult) testCaseResult).getErrorCount(), XSDDatatype.XSDinteger))
                    .addProperty(RDFUNITv.resultPrevalence,
                            ResourceFactory.createTypedLiteral("" +((AggregatedTestCaseResult) testCaseResult).getPrevalenceCount().orElse(-1L), XSDDatatype.XSDinteger));
        }

        if (testCaseResult instanceof RLOGTestCaseResult) {
            resource
                    .addProperty(RDF.type, RDFUNITv.RLOGTestCaseResult)
                    .addProperty(RDF.type, RLOG.Entry)
                    .addProperty(RLOG.resource, model.createResource(((RLOGTestCaseResult) testCaseResult).getFailingResource()))
                    .addProperty(RLOG.message, testCaseResult.getMessage())
                    .addProperty(RLOG.level, model.createResource(testCaseResult.getSeverity().getUri()))
            ;
        }

        if (testCaseResult instanceof ExtendedTestCaseResult) {
            resource
                    .addProperty(RDF.type, RDFUNITv.ExtendedTestCaseResult);

            for (PropertyValuePair annotation : ((ExtendedTestCaseResult) testCaseResult).getResultAnnotations()) {
                for (RDFNode rdfNode : annotation.getValues()) {
                    resource.addProperty(annotation.getProperty(), rdfNode);
                }
            }
        }

        if (testCaseResult instanceof SimpleShaclTestCaseResult) {
            resource
                    .addProperty(RDF.type, SHACL.ValidationResult)
                    .addProperty(SHACL.focusNode, model.createResource(((SimpleShaclTestCaseResult) testCaseResult).getFailingResource()))    //TODO double check later, might not always be the current resource
                    .addProperty(SHACL.message, testCaseResult.getMessage())
                    .addProperty(SHACL.severity, model.createResource(testCaseResult.getSeverity().getUri()))
            ;
        }

        if (testCaseResult instanceof ShaclTestCaseResult) {
            resource
                    .addProperty(SHACL.subject,model.createResource(((SimpleShaclTestCaseResult) testCaseResult).getFailingResource()));


            for (PropertyValuePair annotation : ((ShaclTestCaseResult) testCaseResult).getResultAnnotations()) {
                for (RDFNode rdfNode : annotation.getValues()) {
                    resource.addProperty(annotation.getProperty(), rdfNode);
                }
            }
        }


        return resource;
    }
}
