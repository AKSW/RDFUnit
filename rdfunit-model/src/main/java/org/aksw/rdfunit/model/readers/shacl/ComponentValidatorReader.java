package org.aksw.rdfunit.model.readers.shacl;

import org.aksw.rdfunit.enums.ComponentValidatorType;
import org.aksw.rdfunit.model.impl.shacl.ComponentValidatorImpl;
import org.aksw.rdfunit.model.interfaces.shacl.ComponentValidator;
import org.aksw.rdfunit.model.readers.ElementReader;
import org.aksw.rdfunit.vocabulary.RDFUNIT_SHACL_EXT;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class ComponentValidatorReader implements ElementReader<ComponentValidator> {

    private final ComponentValidatorType type;

    private ComponentValidatorReader(ComponentValidatorType type){
        this.type = type;
    }

    public static ComponentValidatorReader create(ComponentValidatorType type) { return new ComponentValidatorReader(type);}

    @Override
    public ComponentValidator read(Resource resource) {
        checkNotNull(resource);

        ComponentValidatorImpl.ComponentValidatorImplBuilder validatorBuilder = ComponentValidatorImpl.builder();

        validatorBuilder.element(resource);
        validatorBuilder.type(type);

        // get message
        for (Statement smt : resource.listProperties(SHACL.message).toList()) {
            validatorBuilder.message(smt.getObject().asLiteral());
        }

        // get prefixes
        for (Statement smt : resource.listProperties(SHACL.prefixes).toList()) {
            RDFNode obj = smt.getObject();
            if (obj.isResource()) {
                validatorBuilder.prefixDeclarations(BatchPrefixDeclarationReader.create().getPrefixDeclarations(obj.asResource()));
            }
        }

        //default ask query
        for (Statement smt : resource.listProperties(SHACL.ask).toList()) {
            checkArgument(type.equals(ComponentValidatorType.ASK_VALIDATOR), "SPARQL SELECT-Based Validator contains ASK query: %s", smt.getObject().asLiteral().getLexicalForm());
            validatorBuilder.sparqlQuery(smt.getObject().asLiteral().getLexicalForm());
        }

        //default sparql query
        for (Statement smt : resource.listProperties(SHACL.select).toList()) {
            checkArgument(!type.equals(ComponentValidatorType.ASK_VALIDATOR), "SPARQL ASK-Based Validator contains SELECT query %s", smt.getObject().asLiteral().getLexicalForm());
            validatorBuilder.sparqlQuery(smt.getObject().asLiteral().getLexicalForm());
        }

        // get filter
        for (Statement smt : resource.listProperties(RDFUNIT_SHACL_EXT.filter).toList()) {
            validatorBuilder.filter(smt.getObject().asLiteral().getLexicalForm());
        }

        return validatorBuilder.build();
    }
}
