package org.aksw.rdfunit.io.writer;

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;

import java.io.OutputStream;

/**
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/19/14 9:20 AM
 */
public final class RDFWriterFactory {
    private RDFWriterFactory() {
    }

    public static RDFWriter createWriterFromFormat(String filenameWithoutExtension, SerializationFormat serializationFormat, TestCaseExecutionType executionType) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(executionType, filenameWithoutExtension + "." + serializationFormat.getExtension());
        } else {
            return new RDFFileWriter(filenameWithoutExtension + "." + serializationFormat.getExtension(), serializationFormat.getName());
        }
    }

    public static RDFWriter createWriterFromFormat(OutputStream outputStream, SerializationFormat serializationFormat, TestCaseExecutionType executionType) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(executionType, outputStream);
        } else {
            return new RDFStreamWriter(outputStream, serializationFormat.getName());
        }
    }


    public static RDFHTMLResultsWriter createHTMLWriter(TestCaseExecutionType type, String filename) {
        switch (type) {
            case statusTestCaseResult:
                return new RDFHTMLResultsStatusWriter(filename);
            case aggregatedTestCaseResult:
                return new RDFHTMLResultsAggregateWriter(filename);
            case rlogTestCaseResult:
                return new RDFHTMLResultsRlogWriter(filename);
            case extendedTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHTMLResultsRlogWriter(filename);
            default:
                return null;
        }
    }

    public static RDFHTMLResultsWriter createHTMLWriter(TestCaseExecutionType type, OutputStream outputStream) {
        switch (type) {
            case statusTestCaseResult:
                return new RDFHTMLResultsStatusWriter(outputStream);
            case aggregatedTestCaseResult:
                return new RDFHTMLResultsAggregateWriter(outputStream);
            case rlogTestCaseResult:
                return new RDFHTMLResultsRlogWriter(outputStream);
            case extendedTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHTMLResultsRlogWriter(outputStream);
            default:
                return null;
        }
    }
}
