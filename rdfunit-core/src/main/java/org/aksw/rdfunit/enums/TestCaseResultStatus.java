package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

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

    /**
     * Holds the prefix to resolve this enum
     */
    private static final String schemaPrefix = "rut";

    /**
     * @return a full URI/IRI as a String
     */
    public String getUri() {
        return PrefixNSService.getNSFromPrefix(schemaPrefix) + "ResultStatus" + name();
    }

    @Override
    public String toString() {
        return getUri();
    }

    /**
     * Resolves a full URI/IRI to an enum
     * @param value the URI/IRI we want to resolve
     * @return the equivalent enum type or null if it cannot resolve
     */
    public static TestCaseResultStatus resolve(String value) {

        String qName = value.replace(PrefixNSService.getNSFromPrefix(schemaPrefix) + "ResultStatus", "");
        for (TestCaseResultStatus status: values()) {
            if (qName.equals(status.name())) {
                return status;
            }
        }
        return null;
    }

    public static TestCaseResultStatus resolve(long value) {
        if (value == -2) {
            return Error;
        }
        if (value == -1) {
            return Timeout;
        }
        if (value == 0) {
            return Success;
        }
        if (value > 0) {
            return Fail;
        }
        return null;
    }
}
