package org.aksw.databugger.enums;

import org.aksw.databugger.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Describes a test case status result
 * Created: 9/25/13 9:05 AM
 */
public enum TestResultStatus {

    Success,
    Fail,
    Timeout,
    Error;

    public String getUri() {
        // TODO make prefix configurable
        return PrefixService.getPrefix("tddo") + name();
    }

    @Override
    public String toString() {
        return getUri();
    }
}
