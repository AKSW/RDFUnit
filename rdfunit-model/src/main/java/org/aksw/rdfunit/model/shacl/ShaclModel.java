package org.aksw.rdfunit.model.shacl;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.aksw.rdfunit.model.interfaces.Shape;

import java.util.List;

/**
 * Represents a SHACL Model
 *
 * @author Dimitris Kontokostas
 * @since 29/1/2016 9:28 πμ
 */

@Value
@Builder
public class ShaclModel {
    @NonNull @Singular private final ImmutableList<Shape> shapes;

    public List<Shape> getShapes() { return shapes;}
}
