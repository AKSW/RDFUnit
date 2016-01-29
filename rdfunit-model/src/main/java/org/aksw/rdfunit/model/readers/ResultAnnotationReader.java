package org.aksw.rdfunit.model.readers;

import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Reads a Result annotation
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:07 PM
 * @version $Id: $Id
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

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.ResultAnnotationReader} object.
     */
    public static ResultAnnotationReader create() { return createArgumentReaderRut(); }
    /**
     * <p>createArgumentReaderRut.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.ResultAnnotationReader} object.
     */
    public static ResultAnnotationReader createArgumentReaderRut() {
        return new ResultAnnotationReader(RDFUNITv.annotationProperty, RDFUNITv.annotationValue, RDFUNITv.annotationValue);}
    /**
     * <p>createArgumentReaderShacl.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.ResultAnnotationReader} object.
     */
    public static ResultAnnotationReader createArgumentReaderShacl() {
        return new ResultAnnotationReader(SHACL.annotationProperty, SHACL.annotationValue, SHACL.annotationVarName);}


    /** {@inheritDoc} */
    @Override
    public ResultAnnotation read(Resource resource) {
        checkNotNull(resource);

        ResultAnnotationImpl.Builder resultAnBuilder = null;

        // get predicate
        int count = 0;
        for (Statement smt : resource.listProperties(propertyP).toList()) {
            checkArgument(++count == 1, "Cannot have more than one property in ResultAnnotation %s with value %s", resource.getURI(), smt.getObject().toString());
            resultAnBuilder = new ResultAnnotationImpl.Builder(smt.getSubject(), ResourceFactory.createProperty(smt.getObject().asResource().getURI()));
        }

        checkNotNull(resultAnBuilder);

        //value
        count = 0;
        for (Statement smt : resource.listProperties(valueP).toList()) {
            checkArgument(++count == 1, "Cannot have more than one value in ResultAnnotation %s with value %s", resource.getURI(), smt.getObject().toString());
            RDFNode value = smt.getObject();
            if (!valueP.getNameSpace().equals(RDFUNITv.namespace) || !value.toString().startsWith("?")) {
                resultAnBuilder.setValue(value);
            }
        }

        //variable name
        count = 0;
        for (Statement smt : resource.listProperties(varNameP).toList()) {
            checkArgument(++count == 1, "Cannot have more than one variable name in ResultAnnotation %s with value %s", resource.getURI(), smt.getObject().toString());
            String varName = smt.getObject().toString();
            if (!valueP.getNameSpace().equals(RDFUNITv.namespace) || varName.startsWith("?")) {
                resultAnBuilder.setVariableName(varName.startsWith("?") ? varName.substring(1) : varName);
            }

        }

        return resultAnBuilder.build();
    }
}
