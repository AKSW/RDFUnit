package org.aksw.rdfunit.enums;

import org.aksw.rdfunit.services.PrefixNSService;

/**
 * <p>TestCaseResultStatus class.</p>
 *
 * @author Dimitris Kontokostas
 *         Describes a test case status result
 * @since 9/25/13 9:05 AM
 * @version $Id: $Id
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
     * <p>getUri.</p>
     *
     * @return a full URI/IRI as a String
     */
    public String getUri() {
        return PrefixNSService.getNSFromPrefix(schemaPrefix) + "ResultStatus" + name();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getUri();
    }

    /**
     * Resolves a full URI/IRI to an enum
     *
     * @param value the URI/IRI we want to resolve
     * @return the equivalent enum type or null if it cannot resolve
     */
    public static TestCaseResultStatus resolve(String value) {

        String qName = PrefixNSService.getLocalName(value, schemaPrefix).replace("ResultStatus", "");
        for (TestCaseResultStatus status : values()) {
            if (qName.equals(status.name())) {
                return status;
            }
        }
        return null;
    }

    /**
     * <p>resolve.</p>
     *
     * @param value a long.
     * @return a {@link org.aksw.rdfunit.enums.TestCaseResultStatus} object.
     */
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
