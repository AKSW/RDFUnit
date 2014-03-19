package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixService;

/**
 * User: Dimitris Kontokostas
 * Describes a test case status result
 * Created: 9/25/13 9:05 AM
 */
public enum TestCaseResultStatus {

    Success,
    Fail,
    Timeout,
    Error,
    Running;

    public String getUri() {
        // TODO make prefix configurable
        return PrefixService.getPrefix("tddo") + "ResultStatus" + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    public static TestCaseResultStatus resolve(String value) {

        String s = value.replace(PrefixService.getPrefix("tddo") + "ResultStatus", "");
        if (s.equals("Success")) {
            return Success;
        } else if (s.equals("Fail")) {
            return Fail;
        } else if (s.equals("Timeout")) {
            return Timeout;
        } else if (s.equals("Error")) {
            return Error;
        }

        return null;
    }

    public static TestCaseResultStatus resolve(long value) {
        if (value == -2)
            return Error;
        if (value == -1)
            return Timeout;
        if (value == 0)
            return Success;
        if (value > 0)
            return Fail;
        return null;
    }
}
