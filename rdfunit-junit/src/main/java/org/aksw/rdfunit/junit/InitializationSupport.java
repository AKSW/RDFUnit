package org.aksw.rdfunit.junit;

import org.junit.runners.model.InitializationError;

/**
 * Simplify initialization checks and error message formatting.
 *
 * @see <a href="https://code.google.com/p/guava-libraries/wiki/PreconditionsExplained">Guava Preconditions</a>
 */
class InitializationSupport {

    static <T> T checkNotNull(
            T t,
            String stringFormatMessagePattern,
            Object... patternReplacements
    ) throws InitializationError {
        checkState(
                t != null,
                stringFormatMessagePattern,
                patternReplacements
        );
        return t;
    }

    static void checkState(
            boolean expressionResult,
            String stringFormatMessagePattern,
            Object... patternReplacements
    ) throws InitializationError {
        if (expressionResult) {
            return;
        }
        throw new InitializationError(String.format(stringFormatMessagePattern, patternReplacements));
    }

}
