package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
final class ManualTestCaseWriter implements ElementWriter {

    private final ManualTestCaseImpl manualTestCase;

    private ManualTestCaseWriter(ManualTestCaseImpl manualTestCase) {
        this.manualTestCase = manualTestCase;
    }

    /**
     * <p>create.</p>
     *
     * @param manualTC a {@link org.aksw.rdfunit.model.impl.ManualTestCaseImpl} object.
     * @return a {@link org.aksw.rdfunit.model.writers.ManualTestCaseWriter} object.
     */
    public static ManualTestCaseWriter create(ManualTestCaseImpl manualTC) {return new ManualTestCaseWriter(manualTC);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(manualTestCase, model);

        resource
                .addProperty(RDF.type, RDFUNITv.ManualTestCase)
                .addProperty(RDFUNITv.sparqlWhere, manualTestCase.getSparqlWhere())
                .addProperty(RDFUNITv.sparqlPrevalence, manualTestCase.getSparqlPrevalence());

        TestAnnotationWriter.create(manualTestCase.getTestCaseAnnotation()).write(model);

        return resource;
    }
}
