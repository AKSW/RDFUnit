package org.aksw.rdfunit.model.writers.results;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.model.writers.ElementWriterUtils;


public final class TestExecutionWriter implements ElementWriter {

    private final TestExecution testExecution;

    private TestExecutionWriter(TestExecution testExecution) {
        this.testExecution = testExecution;
    }

    public static TestExecutionWriter create(TestExecution testExecution) {return new TestExecutionWriter(testExecution);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriterUtils.copyElementResourceInModel(testExecution, model);

        //TODO

        return resource;
    }
}
