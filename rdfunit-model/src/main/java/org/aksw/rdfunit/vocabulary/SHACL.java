package org.aksw.rdfunit.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Core SHACL Vocabulary
 *
 * @author Dimitris Kontokostas
 * @since 6/17/15 6:43 PM
 * @version $Id: $Id
 */
@SuppressWarnings("ClassWithTooManyFields")
public final class SHACL {

    //namespace
    /** Constant <code>namespace="http://www.w3.org/ns/shacl#"</code> */
    public static final String namespace = "http://www.w3.org/ns/shacl#";

    //Classes

    public static final Resource Shape = resource("Shape");
    public static final Resource PropertyShape = resource("PropertyShape");
    public static final Resource NodeShape = resource("NodeShape");
    public static final Resource ParameterCls = resource("Parameter");
    public static final Resource ResultAnnotation = resource("ResultAnnotation");
    public static final Resource ValidationResult = resource("ValidationResult");
    public static final Resource ValidationReport = resource("ValidationReport");
    public static final Resource SPARQLAskValidator  = resource("SPARQLAskValidator");
    public static final Resource ConstraintComponent = resource("ConstraintComponent");


    public static final Property targetClass = property("targetClass");
    public static final Property targetNode = property("targetNode");
    public static final Property targetSubjectsOf = property("targetSubjectsOf");
    public static final Property targetObjectsOf = property("targetObjectsOf");


    public static final Resource Info = resource("Info");
    public static final Resource Warning = resource("Warning");
    public static final Resource Violation = resource("Violation");

    //properties
    public static final Property parameter = property("parameter");
    public static final Property datatype = property("datatype");
    public static final Property datatypeIn = property("datatypeIn");
    public static final Property defaultValue = property("defaultValue");
    public static final Property optional = property("optional");
    public static final Property sparql = property("sparql");
    public static final Property validator = property("validator");
    public static final Property nodeValidator = property("nodeValidator");
    public static final Property propertyValidator = property("propertyValidator");
    public static final Property ask = property("ask");
    public static final Property select = property("select");

    public static final Property clazz = property("class");
    public static final Property clazzIn = property("classIn");
    public static final Property directType = property("directType");

    public static final Property severity = property("severity");
    public static final Property resultSeverity = property("resultSeverity");
    public static final Property focusNode = property("focusNode");
    public static final Property path = property("path");
    public static final Property value = property("value");
    public static final Property message = property("message");
    public static final Property resultMessage = property("resultMessage");
    public static final Property resultPath = property("resultPath");
    public static final Property sourceConstraint = property("sourceConstraint");
    public static final Property sourceShape = property("sourceShape");
    public static final Property sourceConstraintComponent = property("sourceConstraintComponent");
    public static final Property conforms = property("conforms");
    public static final Property result = property("result");

    public static final Property annotationProperty = property("annotationProperty");
    public static final Property annotationVarName = property("annotationVarName");
    public static final Property annotationValue = property("annotationValue");
    public static final Property property = property("property");

    public static final Property inversePath = property("inversePath");
    public static final Property zeroOrMorePath = property("zeroOrMorePath");
    public static final Property oneOrMorePath = property("oneOrMorePath");
    public static final Property zeroOrOnePath = property("zeroOrOnePath");
    public static final Property alternativePath = property("alternativePath");

    public static final Property minExclusive = property("minExclusive");
    public static final Property minInclusive = property("minInclusive");
    public static final Property maxExclusive = property("maxExclusive");
    public static final Property maxInclusive = property("maxInclusive");
    public static final Property minLength = property("minLength");
    public static final Property maxLength = property("maxLength");
    public static final Property nodeKind = property("nodeKind");
    public static final Property notEquals = property("notEquals");
    public static final Property equalz = property("equals");
    public static final Property hasValue = property("hasValue");
    public static final Property in = property("in");
    public static final Property lessThan = property("lessThan");
    public static final Property lessThanOrEquals = property("lessThanOrEquals");
    public static final Property minCount = property("minCount");
    public static final Property maxCount = property("maxCount");
    public static final Property pattern = property("pattern");
    public static final Property flags = property("flags");
    public static final Property uniqueLang = property("uniqueLang");

    public static final Property node = property("node");
    public static final Property and = property("and");
    public static final Property or = property("or");
    public static final Property not = property("not");

    public static final Property declare = property("declare");
    public static final Property prefixes = property("prefixes");
    public static final Property prefix = property("prefix");
    public static final Property prefixNamespace = property("namespace");

    public static final Property deactivated = property("deactivated");


    private SHACL() {
    }


    private static Resource resource(String local) {
        return ResourceFactory.createResource(namespace + local);
    }

    private static Property property(String local) {
        return ResourceFactory.createProperty(namespace, local);
    }

}
