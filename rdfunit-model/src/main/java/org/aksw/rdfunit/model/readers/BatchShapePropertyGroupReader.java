package org.aksw.rdfunit.model.readers;

import lombok.NonNull;
import org.aksw.rdfunit.model.interfaces.PropertyConstraintGroup;
import org.apache.jena.rdf.model.Resource;

import java.util.Collections;
import java.util.Set;

public final class BatchShapePropertyGroupReader {

    private BatchShapePropertyGroupReader() {
    }

    public static BatchShapePropertyGroupReader create() {
        return new BatchShapePropertyGroupReader();
    }

    public Set<PropertyConstraintGroup> read(@NonNull Resource resource) {


        return Collections.EMPTY_SET;

    }

}

