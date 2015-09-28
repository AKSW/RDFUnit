package org.aksw.rdfunit.model.writers;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.vocabulary.RDFUNITv;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 * @version $Id: $Id
 */
public final class BindingWriter implements ElementWriter {

    private final Binding binding;

    private BindingWriter(Binding binding) {
        this.binding = binding;
    }

    /**
     * <p>createBindingWriter.</p>
     *
     * @param binding a {@link org.aksw.rdfunit.model.interfaces.Binding} object.
     * @return a {@link org.aksw.rdfunit.model.writers.BindingWriter} object.
     */
    public static BindingWriter createBindingWriter(Binding binding) {return new BindingWriter(binding);}

    /** {@inheritDoc} */
    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriterUtils.copyElementResourceInModel(binding, model);

        resource.addProperty(RDF.type, RDFUNITv.Binding);
        resource.addProperty(RDFUNITv.parameter, ElementWriterUtils.copyElementResourceInModel(binding.getParameter(), model));
        resource.addProperty(RDFUNITv.bindingValue, binding.getValue());

        return resource;
    }
}
