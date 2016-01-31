package org.aksw.rdfunit.dqv;

import lombok.NonNull;
import lombok.Value;

/**
 * @author Dimitris Kontokostas
 * @since 21/1/2016 9:20 μμ
 */
@Value
public class QualityMeasure {
    @NonNull private final String testExecutionUri;
    @NonNull private final String DqvMetricUri;
    private final double value;

}
