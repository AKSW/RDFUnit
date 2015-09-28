package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.impl.ManualTestCaseImpl;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class ManualTestCaseWriter implements ElementWriter {

    private final ManualTestCaseImpl manualTC;

    private ManualTestCaseWriter(ManualTestCaseImpl manualTC) {
        this.manualTC = manualTC;
    }

    public static ManualTestCaseWriter createManualTestCaseWriter(ManualTestCaseImpl manualTC) {return new ManualTestCaseWriter(manualTC);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriterUtils.copyElementResourceInModel(manualTC, model);

        resource
                .addProperty(RDF.type, RDFUNITv.ManualTestCase)
                .addProperty(RDFUNITv.sparqlWhere, manualTC.getSparqlWhere())
                .addProperty(RDFUNITv.sparqlPrevalence, manualTC.getSparqlPrevalence());

        TestAnnotationWriter.createTestCaseAnnotationWriter(manualTC.getTestCaseAnnotation()).write(model);

        return resource;
    }
}
