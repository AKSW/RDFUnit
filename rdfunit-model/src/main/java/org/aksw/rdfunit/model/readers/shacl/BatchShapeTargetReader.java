package org.aksw.rdfunit.model.readers.shacl;

import com.google.common.collect.ImmutableSet;
import org.aksw.rdfunit.enums.ShapeTargetType;
import org.aksw.rdfunit.model.impl.shacl.ShapeTargetCore;
import org.aksw.rdfunit.model.interfaces.shacl.ShapeTarget;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

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
public final class BatchShapeTargetReader {

    private BatchShapeTargetReader() {
    }

    /**
     * <p>create.</p>
     *
     * @return a {@link org.aksw.rdfunit.model.readers.PatternReader} object.
     */
    public static BatchShapeTargetReader create() {
        return new BatchShapeTargetReader();
    }

    public Set<ShapeTarget> read(Resource resource) {
        checkNotNull(resource);

        ImmutableSet.Builder<ShapeTarget> targetBuilder = ImmutableSet.builder();

        targetBuilder.addAll(collectExplicitTargets(resource));

        //targetBuilder.addAll(collectValueShapeTargets(resource));

        return targetBuilder.build();

    }

    private Set<ShapeTarget> collectExplicitTargets(Resource resource) {
        ImmutableSet.Builder<ShapeTarget> targetBuilder = ImmutableSet.builder();

        if (resource.hasLiteral(SHACL.deactivated, true)) {
            return targetBuilder.build();
        }

        targetBuilder.addAll(collectClassTarget(resource));
        targetBuilder.addAll(collectImplicitClassTarget(resource));
        targetBuilder.addAll(collectNodeTarget(resource));
        targetBuilder.addAll(collectSubjectsOfTarget(resource));
        targetBuilder.addAll(collectObjectsOfTarget(resource));

        return targetBuilder.build();
    }

    private List<ShapeTarget> collectClassTarget(Resource resource) {
        return resource.listProperties(SHACL.targetClass)
                .toList().stream()
                .filter(smt -> smt.getObject().isResource())
                .map(smt -> ShapeTargetCore.create(ShapeTargetType.ClassTarget, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }

    private List<ShapeTarget> collectImplicitClassTarget(Resource resource) {
        if (resource.hasProperty(RDF.type, RDFS.Class) || resource.hasProperty(RDF.type, OWL.Class)) {
            return Collections.singletonList(
                    ShapeTargetCore.create(ShapeTargetType.ClassTarget, resource.getURI()
            ));
        } else {
            return Collections.emptyList();
        }
    }

    private List<ShapeTarget> collectNodeTarget(Resource resource) {
        return collectPropertyHelper(resource, SHACL.targetNode, ShapeTargetType.NodeTarget);
    }

    private List<ShapeTarget> collectSubjectsOfTarget(Resource resource) {
        return collectPropertyHelper(resource, SHACL.targetSubjectsOf, ShapeTargetType.SubjectsOfTarget);
    }

    private List<ShapeTarget> collectObjectsOfTarget(Resource resource) {
        return collectPropertyHelper(resource, SHACL.targetObjectsOf, ShapeTargetType.ObjectsOfTarget);
    }

    private List<ShapeTarget> collectPropertyHelper(Resource resourceShape, Property targetProperty, ShapeTargetType shapeTargetType) {
        return resourceShape.listProperties(targetProperty)
                .toList().stream()
                .filter(smt -> smt.getObject().isResource())
                .map(smt -> ShapeTargetCore.create(shapeTargetType, smt.getObject().asResource().getURI()))
                .collect(Collectors.toList());
    }
}

