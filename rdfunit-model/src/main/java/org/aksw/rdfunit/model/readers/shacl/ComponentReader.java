package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.model.impl.shacl.ComponentImpl;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import static com.google.common.base.Preconditions.checkNotNull;


public class ComponentReader implements ElementReader<Component> {

    private ComponentReader(){}

    public static ComponentReader create() { return new ComponentReader();}

    @Override
    public Component read(Resource resource) {
        checkNotNull(resource);

        ComponentImpl.ComponentImplBuilder componentBuilder = ComponentImpl.builder();

        componentBuilder.element(resource);

        // get parameters
        for (Statement smt : resource.listProperties(SHACL.parameter).toList()) {
            Resource obj = smt.getObject().asResource();
            ComponentParameter cp = ComponentParameterReader.create().read(obj);
            componentBuilder.parameter(cp);
        }

        // get validators / ASK
        for (Statement smt : resource.listProperties(SHACL.validator).toList()) {
            Resource obj = smt.getObject().asResource();
            ComponentValidator cv = ComponentValidatorReader.create(ComponentValidatorType.ASK_VALIDATOR).read(obj);
            componentBuilder.validator(cv);
        }


        // get validators / node validators
        for (Statement smt : resource.listProperties(SHACL.nodeValidator).toList()) {
            Resource obj = smt.getObject().asResource();
            ComponentValidator cv = ComponentValidatorReader.create(ComponentValidatorType.NODE_VALIDATOR).read(obj);
            componentBuilder.validator(cv);
        }

        // get validators / property validators
        for (Statement smt : resource.listProperties(SHACL.propertyValidator).toList()) {
            Resource obj = smt.getObject().asResource();
            ComponentValidator cv = ComponentValidatorReader.create(ComponentValidatorType.PROPERTY_VALIDATOR).read(obj);
            componentBuilder.validator(cv);
        }

        return componentBuilder.build();
    }
}
