package org.aksw.rdfunit.elements.readers;

import com.hp.hpl.jena.rdf.model.*;
import org.aksw.rdfunit.elements.implementations.ResultAnnotationImpl;
import org.aksw.rdfunit.elements.interfaces.ResultAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads a Result annotation
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 */
public final class ResultAnnotationReader implements ElementReader<ResultAnnotation> {

    private final Property propertyP;
    private final Property valueP;
    private final Property varNameP;

    private ResultAnnotationReader(Property propertyP, Property valueP, Property varNameP){
        this.propertyP = propertyP;
        this.valueP = valueP;
        this.varNameP = varNameP;
    }

    public static ResultAnnotationReader createArgumentReader() { return createArgumentReaderRut(); }
    public static ResultAnnotationReader createArgumentReaderRut() {
        return new ResultAnnotationReader(RDFUNITv.annotationProperty, RDFUNITv.annotationValue, RDFUNITv.annotationValue);}
    public static ResultAnnotationReader createArgumentReaderShacl() {
        return new ResultAnnotationReader(SHACL.annotationProperty, SHACL.annotationValue, SHACL.annotationVarName);}


    @Override
    public ResultAnnotation read(Resource resource) {
        checkNotNull(resource);

        ResultAnnotationImpl.Builder resultAnBuilder = null;

        // get predicate
        for (Statement smt : resource.listProperties(propertyP).toList()) {
            resultAnBuilder = new ResultAnnotationImpl.Builder(ResourceFactory.createProperty(smt.getObject().asResource().getURI()));
        }

        checkNotNull(resultAnBuilder);

        //value
        for (Statement smt : resource.listProperties(valueP).toList()) {
            RDFNode value = smt.getObject();
            if (!valueP.getNameSpace().equals(RDFUNITv.namespace) || !value.toString().startsWith("?")) {
                resultAnBuilder.setValue(value);
            }
        }

        //variable name
        for (Statement smt : resource.listProperties(varNameP).toList()) {
            String varName = smt.getObject().toString();
            if (!valueP.getNameSpace().equals(RDFUNITv.namespace) || varName.startsWith("?")) {
                resultAnBuilder.setVariableName(varName.startsWith("?") ? varName.substring(1) : varName);
            }

        }

        return resultAnBuilder.build();
    }
}
