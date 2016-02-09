package org.aksw.rdfunit.io.writer;


import org.aksw.rdfunit.io.format.FormatService;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;

import java.io.OutputStream;


public final class RDFHtmlWriterFactory {
    private RDFHtmlWriterFactory() {
    }

    public static RDFWriter createWriterFromFormat(String filenameWithoutExtension, SerializationFormat serializationFormat, TestExecution testExecution) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(testExecution, filenameWithoutExtension + "." + serializationFormat.getExtension());
        } else {
            return new RDFFileWriter(filenameWithoutExtension + "." + serializationFormat.getExtension(), serializationFormat.getName());
        }
    }


    public static RDFWriter createWriterFromFormat(OutputStream outputStream, SerializationFormat serializationFormat, TestExecution testExecution) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(testExecution, outputStream);
        } else {
            return new RDFStreamWriter(outputStream, serializationFormat.getName());
        }
    }


    public static RDFHTMLResultsWriter createHTMLWriter(TestExecution testExecution,  String filename) {
        return createHTMLWriter(testExecution, RDFStreamWriter.getOutputStreamFromFilename(filename));
    }


    public static RDFHTMLResultsWriter createHTMLWriter(TestExecution testExecution, OutputStream outputStream) {
        switch (testExecution.getTestExecutionType()) {
            case statusTestCaseResult:
                return new RDFHTMLResultsStatusWriter(testExecution, outputStream);
            case aggregatedTestCaseResult:
                return new RDFHTMLResultsAggregateWriter(testExecution, outputStream);
            case shaclSimpleTestCaseResult:
                return new RDFHTMLResultsShaclWriter(testExecution, outputStream);
            case shaclFullTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHTMLResultsShaclWriter(testExecution, outputStream);
            case rlogTestCaseResult:
                return new RDFHTMLResultsRlogWriter(testExecution, outputStream);
            case extendedTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHTMLResultsRlogWriter(testExecution, outputStream);
            default:
                return null;
        }
    }
}
