package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.Function;
import org.aksw.rdfunit.vocabulary.SHACL;
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
public final class FunctionWriter implements ElementWriter {

    private final Function function;

    private FunctionWriter(Function function) {
        this.function = function;
    }

    /**
     * <p>create.</p>
     *
     * @param function a {@link org.aksw.rdfunit.model.interfaces.Function} object.
     * @return a {@link org.aksw.rdfunit.model.writers.FunctionWriter} object.
     */
    public static FunctionWriter createArgumentWriter(Function function) {return new FunctionWriter(function);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(function, model);

        // rdf:type sh:Argument
        resource.addProperty(RDF.type, SHACL.Function);


        throw new UnsupportedOperationException("FunctionWriter not implemented");
        //return resource;
    }
}
