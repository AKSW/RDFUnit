package org.aksw.rdfunit.elements.writers;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.elements.interfaces.Function;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.commons.lang.NotImplementedException;

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
     * <p>createArgumentWriter.</p>
     *
     * @param function a {@link org.aksw.rdfunit.elements.interfaces.Function} object.
     * @return a {@link org.aksw.rdfunit.elements.writers.FunctionWriter} object.
     */
    public static FunctionWriter createArgumentWriter(Function function) {return new FunctionWriter(function);}

    /** {@inheritDoc} */
    @Override
    public Resource write() {

        // keep the original resource if exists
        Resource resource = function.getResource().isPresent() ?
                function.getResource().get() :
                ResourceFactory.createResource();

        // rdf:type sh:Argument
        resource.addProperty(RDF.type, SHACL.Function);


        throw new NotImplementedException();
        //return resource;
    }
}
