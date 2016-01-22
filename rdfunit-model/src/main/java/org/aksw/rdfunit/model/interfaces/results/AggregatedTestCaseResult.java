package org.aksw.rdfunit.model.interfaces.results;

import java.util.Optional;

public interface AggregatedTestCaseResult extends StatusTestCaseResult {

    long getErrorCount();

    Optional<Long> getPrevalenceCount();
}
