package org.aksw.rdfunit.model.interfaces.results;

import com.google.common.base.Optional;

public interface AggregatedTestCaseResult extends StatusTestCaseResult {

    long getErrorCount();

    Optional<Long> getPrevalenceCount();
}
