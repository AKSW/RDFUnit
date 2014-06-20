package org.aksw.rdfunit.io;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.services.FormatService;

import java.io.OutputStream;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/19/14 9:20 AM
 */
public final class DataWriterFactory {
    protected DataWriterFactory() {
    }

    public static DataWriter createWriterFromFormat(String filenameWithoutExtension, SerializationFormat serializationFormat, TestCaseExecutionType executionType) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(executionType, filenameWithoutExtension + "." + serializationFormat.getExtension());
        } else {
            return new RDFFileWriter(filenameWithoutExtension + "." + serializationFormat.getExtension(), serializationFormat.getName());
        }
    }

    public static DataWriter createWriterFromFormat(OutputStream outputStream, SerializationFormat serializationFormat, TestCaseExecutionType executionType) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(executionType, outputStream);
        } else {
            return new RDFStreamWriter(outputStream, serializationFormat.getName());
        }
    }


    public static HTMLResultsWriter createHTMLWriter(TestCaseExecutionType type, String filename) {
        switch (type) {
            case statusTestCaseResult:
                return new HTMLResultsStatusWriter(filename);
            case aggregatedTestCaseResult:
                return new HTMLResultsAggregateWriter(filename);
            case rlogTestCaseResult:
                return new HTMLResultsRlogWriter(filename);
            //case extendedTestCaseResult:
            default:
                return null;
        }
    }

    public static HTMLResultsWriter createHTMLWriter(TestCaseExecutionType type, OutputStream outputStream) {
        switch (type) {
            case statusTestCaseResult:
                return new HTMLResultsStatusWriter(outputStream);
            case aggregatedTestCaseResult:
                return new HTMLResultsAggregateWriter(outputStream);
            case rlogTestCaseResult:
                return new HTMLResultsRlogWriter(outputStream);
            //case extendedTestCaseResult:
            default:
                return null;
        }
    }
}
