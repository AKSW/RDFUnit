package org.aksw.rdfunit.model.readers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.enums.ShapeScopeType;
import org.aksw.rdfunit.model.impl.ShapeScopeCore;
import org.aksw.rdfunit.model.impl.ShapeScopeValueShape;
import org.aksw.rdfunit.model.interfaces.ShapeScope;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 10/19/15 7:23 PM
 * @version $Id: $Id
 */
public final class BatchShapeScopeReader {

    private BatchShapeScopeReader() {
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternReader} object.
     */
    public static BatchShapeScopeReader create() {
        return new BatchShapeScopeReader();
    }

    public Set<ShapeScope> read(Resource resource) {
        checkNotNull(resource);

        ImmutableSet.Builder<ShapeScope> scopeBuilder = ImmutableSet.builder();

        scopeBuilder.addAll(collectExplicitScopes(resource));

        scopeBuilder.addAll(collectValueShapeScopes(resource));

        return scopeBuilder.build();

    }

    private Set<ShapeScope> collectExplicitScopes(Resource resource) {
        ImmutableSet.Builder<ShapeScope> scopeBuilder = ImmutableSet.builder();

        scopeBuilder.addAll(collectClassScopes(resource));
        scopeBuilder.addAll(collectNodeScopes(resource));
        scopeBuilder.addAll(collectPropertyScope(resource));
        scopeBuilder.addAll(collectInversePropertyScope(resource));
        scopeBuilder.addAll(collectAllSubjectsScope(resource));
        scopeBuilder.addAll(collectAllObjectsScope(resource));

        return scopeBuilder.build();
    }

    private List<ShapeScope> collectClassScopes(Resource resource) {
        return resource.listProperties(SHACL.scopeClass)
                .toList().stream()
                .filter(smt -> smt.getObject().isResource())
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.ClassScope, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }

    private List<ShapeScope> collectNodeScopes(Resource resource) {
        return resource.listProperties(SHACL.scopeNode)
                .toList().stream()
                .filter(smt -> smt.getObject().isResource())
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.NodeScope, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }

    private List<ShapeScope> collectPropertyScope(Resource resource) {
        return  collectPropertyHelper(resource, SHACL.PropertyScope).stream()
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.PropertyScope, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }

    private List<ShapeScope> collectInversePropertyScope(Resource resource) {
        return  collectPropertyHelper(resource, SHACL.InversePropertyScope).stream()
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.InversePropertyScope, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }

    private List<Statement> collectPropertyHelper(Resource resource, Resource type) {
        return  collectGeneralScopeResources(resource).stream()
                .filter(r -> r.hasProperty(RDF.type, type))
                .flatMap( r -> r.listProperties(SHACL.predicate).toList().stream())
                .collect(Collectors.toList());
    }



    private List<ShapeScope> collectAllSubjectsScope(Resource resource) {
        return  collectAllHelper(resource, SHACL.AllSubjectsScope).stream()
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.AllSubjectsScope))
                .collect(Collectors.toList());
    }

    private List<ShapeScope> collectAllObjectsScope(Resource resource) {
        return  collectAllHelper(resource, SHACL.AllObjectsScope).stream()
                .map(smt -> ShapeScopeCore.create(ShapeScopeType.AllObjectsScope))
                .collect(Collectors.toList());
    }

    private List<Resource> collectAllHelper(Resource resource, Resource type) {
        return  collectGeneralScopeResources(resource).stream()
                .filter(r -> r.hasProperty(RDF.type, type))
                .collect(Collectors.toList());
    }


    private List<Resource> collectGeneralScopeResources(Resource resource) {
        return resource.listProperties(SHACL.scope)
                .toList().stream()
                .filter(smt -> smt.getObject().isResource())
                .map(smt -> smt.getObject().asResource())
                .collect(Collectors.toList());
    }

    private Set<ShapeScope> collectValueShapeScopes(Resource resource){
        return collectValueShapeScopes(resource, Collections.emptyList());
    }

    private Set<ShapeScope> collectValueShapeScopes(Resource resource, List<Resource> propertyChain){
        ImmutableSet.Builder<ShapeScope> scopes = ImmutableSet.builder();

        List<Resource> tmp = resource.getModel().listResourcesWithProperty(SHACL.valueShape, resource).toList();

                tmp.forEach( r -> {
                    Resource property = r.getPropertyResourceValue(SHACL.predicate);
                    getParentShapeResources(r)
                            .forEach(shape -> {
                                ImmutableList<Resource> propChainNew = new ImmutableList.Builder<Resource>().add(property).addAll(propertyChain).build();
                                collectExplicitScopes(shape).forEach(scope ->
                                        scopes.add(ShapeScopeValueShape.create(scope, propChainNew)));

                                scopes.addAll(collectValueShapeScopes(shape, propChainNew));
                            });
                });

        return scopes.build();
    }

    private List<Resource> getParentShapeResources(Resource resource) {
        return resource.getModel().listResourcesWithProperty(SHACL.property, resource).toList();
    }
}

