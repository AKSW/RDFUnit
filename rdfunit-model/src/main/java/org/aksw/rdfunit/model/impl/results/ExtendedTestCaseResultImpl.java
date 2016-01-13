package org.aksw.rdfunit.model.impl.results;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.helper.SimpleAnnotation;
import org.aksw.rdfunit.model.interfaces.results.RLOGTestCaseResult;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * The type Extended test case result.
 *
 * @author Dimitris Kontokostas
 * @since 2 /2/14 3:57 PM
 * @version $Id: $Id
 */
public class ExtendedTestCaseResultImpl extends RLOGTestCaseResultImpl {

    private final ImmutableSet<SimpleAnnotation> resultAnnotations;

    public ExtendedTestCaseResultImpl(String testCaseUri, RLOGLevel severity, String message, String resource, Set<SimpleAnnotation> resultAnnotations) {
        super(testCaseUri, severity, message, resource);
        this.resultAnnotations = ImmutableSet.copyOf(checkNotNull(resultAnnotations));
    }

    private ExtendedTestCaseResultImpl(Builder builder) {
        this(builder.rlogTestCaseResult.getTestCaseUri(),
                builder.rlogTestCaseResult.getSeverity(),
                builder.rlogTestCaseResult.getMessage(),
                builder.rlogTestCaseResult.getFailingResource(),
                builder.resultAnnotations);
    }

    /** {@inheritDoc} */
    @Override
    public Resource serialize(Model model, String testExecutionURI) {
        Resource resource = super.serialize(model, testExecutionURI)
                .addProperty(RDF.type, RDFUNITv.ExtendedTestCaseResult)
                .addProperty(RDF.type, SHACL.ValidationResult)
                .addProperty(SHACL.focusNode, model.createResource(getFailingResource()));

        for (SimpleAnnotation annotation : resultAnnotations) {
            for (RDFNode rdfNode : annotation.getValues()) {
                resource.addProperty(annotation.getProperty(), rdfNode);
            }
        }

        return resource;
    }

    public Set<SimpleAnnotation> getResultAnnotations() {
        return resultAnnotations;
    }

    public static class Builder {
        private RLOGTestCaseResult rlogTestCaseResult;
        private Set<SimpleAnnotation> resultAnnotations;

        public Builder(String testCaseUri, RLOGLevel severity, String message, String resource) {
            rlogTestCaseResult = new RLOGTestCaseResultImpl(testCaseUri, severity, message, resource);
        }

        public Builder setResultAnnotations(Set<SimpleAnnotation> annotations) {
            this.resultAnnotations = annotations;
            return this;
        }

        public ExtendedTestCaseResultImpl build() {
            return new ExtendedTestCaseResultImpl(this);
        }
    }
}
