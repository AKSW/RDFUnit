package org.aksw.rdfunit.model.writers;

import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM

 */
public final class BindingWriter implements ElementWriter {

    private final Binding binding;

    private BindingWriter(Binding binding) {
        this.binding = binding;
    }

    public static BindingWriter create(Binding binding) {return new BindingWriter(binding);}


    @Override
    public Resource write(Model model) {
        Resource resource = ElementWriter.copyElementResourceInModel(binding, model);

        resource.addProperty(RDF.type, RDFUNITv.Binding);
        resource.addProperty(RDFUNITv.parameter, ElementWriter.copyElementResourceInModel(binding.getParameter(), model));
        resource.addProperty(RDFUNITv.bindingValue, binding.getValue());

        return resource;
    }
}
