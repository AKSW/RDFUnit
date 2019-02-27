package org.aksw.rdfunit.model.impl.shacl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.aksw.rdfunit.model.impl.ResultAnnotationImpl;
import org.aksw.rdfunit.model.interfaces.ResultAnnotation;
import org.aksw.rdfunit.model.interfaces.shacl.Component;
import org.aksw.rdfunit.model.interfaces.shacl.Shape;
import org.aksw.rdfunit.model.interfaces.shacl.Validator;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.core.VarExprList;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dimitris Kontokostas
 * @since 6/10/18
 */
@Builder
@Value
@EqualsAndHashCode
class ResultAnnotationParser {

    @NonNull private final Query query;
    @NonNull private final Shape shape;
    @NonNull private final Validator validator;
    private final boolean canBindValueVariable;
    private final Component component;

    private Optional<ResultAnnotation> getPathAnnotation() {
        if (query.getResultVars().contains("path")) {
            List<Var> vars = query.getProjectVars();
            VarExprList vel = query.getProject();
            return Optional.of(
                createVariableAnnotation(SHACL.resultPath, "path"));
        } else {
            if (shape.getPath().isPresent()) {
                return Optional.of(
                    createValueAnnotation(SHACL.resultPath, shape.getPath().get().getPathAsRdf()));
            }
        }
        return Optional.empty();
    }

    private Optional<ResultAnnotation> getFocusNodeAnnotation() {
        if (query.getResultVars().contains("focusNode")) {
            return Optional.of(
                    createVariableAnnotation(SHACL.focusNode, "focusNode"));
        } else {
            return Optional.of(
                    createVariableAnnotation(SHACL.focusNode, "this"));
        }
    }

    private Optional<ResultAnnotation> getMessageAnnotation() {

        if (query.getResultVars().contains("message")) {
            return Optional.of(
                    createVariableAnnotation(SHACL.resultMessage, "message"));
        } else {

            return ImmutableList.of(shape.getMessage(),validator.getDefaultMessage()).stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .map(message -> createValueAnnotation(SHACL.resultMessage, message));

        }

    }

    private Optional<ResultAnnotation> getValueAnnotation() {
        boolean componentThatAllowsValueBinding = canBindValueVariable && (validator.getSparqlQuery().contains("$value") || validator.getSparqlQuery().contains("?value") );
        boolean isNodeShapeAndHasValueVar = shape.isNodeShape() && (validator.getSparqlQuery().contains("$value") || validator.getSparqlQuery().contains("?value") );
        if (isNodeShapeAndHasValueVar || componentThatAllowsValueBinding ) {
            return Optional.of(
                    createVariableAnnotation(SHACL.value, "value"));
        }

        // when ?value is not defined in Node Shapes we use ?this
        if (shape.isNodeShape()) {
            return Optional.of(
                    createVariableAnnotation(SHACL.value, "this"));
        }



        return Optional.empty();
    }

    private Optional<ResultAnnotation> getSeverityAnnotation() {
        return Optional.of(
                createValueAnnotation(SHACL.resultSeverity, shape.getSeverity()));

    }
    private Optional<ResultAnnotation> getSourceShapeAnnotation() {
        return Optional.of(
                createValueAnnotation(SHACL.sourceShape, shape.getElement()));

    }
    private Optional<ResultAnnotation> getSourceConstraintComponentAnnotation() {
        return Optional
                .ofNullable(component)
                .map(c -> new ResultAnnotationImpl.Builder(c.getElement(), SHACL.sourceConstraintComponent)
                        .setValue(component.getElement()).build());

    }
    private Optional<ResultAnnotation> getSourceConstraintComponentSparqlAnnotation() {
        if (component == null) {
            return Optional.of(
                    createValueAnnotation(SHACL.sourceConstraint, validator.getElement()));
        } else {return Optional.empty();}

    }

    private Optional<ResultAnnotation> getSourceConstraintAnnotation() {
        if (component == null) {
            return Optional.of(
                    createValueAnnotation(SHACL.sourceConstraintComponent, SHACL.SPARQLConstraintComponent));
        } else {return Optional.empty();}

    }



    public Set<ResultAnnotation> getResultAnnotations() {

        return ImmutableSet.of(
                getFocusNodeAnnotation(),
                getPathAnnotation(),
                getMessageAnnotation(),
                getSeverityAnnotation(),
                getSourceConstraintAnnotation(),
                getSourceConstraintComponentAnnotation(),
                getSourceConstraintComponentSparqlAnnotation(),
                getSourceShapeAnnotation(),
                getValueAnnotation()
        )
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());


    }


    private ResultAnnotation createValueAnnotation(Property property, RDFNode value) {
        return new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), property)
                .setValue(value).build();
    }

    private ResultAnnotation createVariableAnnotation(Property property, String variable) {
        return new ResultAnnotationImpl.Builder(ResourceFactory.createResource(), property)
                .setVariableName(variable).build();
    }
}
