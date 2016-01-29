package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.interfaces.Binding;
import org.aksw.rdfunit.model.interfaces.Pattern;
import org.aksw.rdfunit.model.interfaces.PatternParameter;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads a Result annotation
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
 */
public final class BindingReader implements ElementReader<Binding> {

    private final Pattern pattern;

    private BindingReader(Pattern pattern){
        this.pattern = checkNotNull(pattern, "Pattern must not be bull in BindingReader");
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.BindingReader} object.
     * @param pattern a {@link org.aksw.rdfunit.model.interfaces.Pattern} object.
     */
    public static BindingReader create(Pattern pattern) { return new BindingReader(pattern);}

    /** {@inheritDoc} */
    @Override
    public Binding read(Resource resource) {
        checkNotNull(resource);

        PatternParameter parameter = null;
        RDFNode value = null;

        int count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.bindingValue).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:bindingValue in Binding %s", resource.getURI());
            value = smt.getObject();

        }

        count = 0;
        for (Statement smt : resource.listProperties(RDFUNITv.parameter).toList()) {
            checkArgument(++count == 1, "Cannot have more than one rut:parameter in Binding %s", resource.getURI());
            String parameterURI = smt.getObject().asResource().getURI();
            parameter = pattern.getParameter(parameterURI).orElse(null);
        }

        return new Binding(resource, parameter, value);
    }
}
