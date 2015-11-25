package org.aksw.rdfunit.model.impl;

import com.google.common.base.Optional;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.rdfunit.enums.RLOGLevel;
import org.aksw.rdfunit.model.interfaces.Argument;
import org.aksw.rdfunit.model.interfaces.ConstraintTemplate;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;

import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/21/15 4:06 PM
 * @version $Id: $Id
 */
public class ConstraintTemplateImpl implements ConstraintTemplate {



    /** {@inheritDoc} */
    @Override
    public String getDefaultMessage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Property getPrperty() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<ResultAnnotation> getResultAnnotations() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public RLOGLevel getSeverity() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Collection<Argument> getArguments() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getSparql() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getLabelTemplate() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Resource getResource() {
        return null;
    }
}
