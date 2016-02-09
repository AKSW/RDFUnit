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
        } else if (serializationFormat.equals(FormatService.getOutputFormat("xml"))) {
            return createXMLWriter(testExecution, filenameWithoutExtension + "." + serializationFormat.getExtension());
        } else {
            return new RDFFileWriter(filenameWithoutExtension + "." + serializationFormat.getExtension(), serializationFormat.getName());
        }
    }


    public static RDFWriter createWriterFromFormat(OutputStream outputStream, SerializationFormat serializationFormat, TestExecution testExecution) {
        if (serializationFormat.equals(FormatService.getOutputFormat("html"))) {
            return createHTMLWriter(testExecution, outputStream);
        } else if (serializationFormat.equals(FormatService.getOutputFormat("xml"))) {
            return createXMLWriter(testExecution, outputStream);
        } else {
            return new RDFStreamWriter(outputStream, serializationFormat.getName());
        }
    }


    public static RDFHtmlResultsWriter createHTMLWriter(TestExecution testExecution, String filename) {
        return createHTMLWriter(testExecution, RDFStreamWriter.getOutputStreamFromFilename(filename));
    }


    public static RDFHtmlResultsWriter createHTMLWriter(TestExecution testExecution, OutputStream outputStream) {
        switch (testExecution.getTestExecutionType()) {
            case statusTestCaseResult:
                return new RDFHtmlResultsStatusWriter(testExecution, outputStream);
            case aggregatedTestCaseResult:
                return new RDFHtmlResultsAggregateWriter(testExecution, outputStream);
            case shaclSimpleTestCaseResult:
                return new RDFHtmlResultsShaclWriter(testExecution, outputStream);
            case shaclFullTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHtmlResultsShaclWriter(testExecution, outputStream);
            case rlogTestCaseResult:
                return new RDFHtmlResultsRlogWriter(testExecution, outputStream);
            case extendedTestCaseResult:
                // TODO extended not supported ATM, use RLOG instead
                return new RDFHtmlResultsRlogWriter(testExecution, outputStream);
            default:
                return null;
        }
    }
    
    /**
     * <p>createXMLWriter.</p>
     * only support status and aggregated for the time being
     * @param type a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @param filename a {@link String} object.
     * @return a {@link org.aksw.rdfunit.io.writer.RDFHTMLResultsWriter} object.
     */
    public static JunitXMLResultsWriter createXMLWriter(TestExecution testExecution, String filename) {
        switch (testExecution.getTestExecutionType()) {
        	case shaclFullTestCaseResult:
        	case shaclSimpleTestCaseResult:
        	case rlogTestCaseResult:
        	case extendedTestCaseResult:
            case statusTestCaseResult:
                return new JunitXMLResultsStatusWriter(testExecution, filename);
            case aggregatedTestCaseResult:
                return new JunitXMLResultsAggregateWriter(testExecution, filename);
            default:
                return null;
        }
    }
    
    /**
     * <p>createXMLWriter.</p>
     * only support status and aggregated for the time being
     * @param type a {@link org.aksw.rdfunit.enums.TestCaseExecutionType} object.
     * @param outputStream a {@link OutputStream} object.
     * @return a {@link org.aksw.rdfunit.io.writer.RDFHTMLResultsWriter} object.
     */
    public static JunitXMLResultsWriter createXMLWriter(TestExecution testExecution, OutputStream outputStream) {
        switch (testExecution.getTestExecutionType()) {
    	case shaclFullTestCaseResult:
    	case shaclSimpleTestCaseResult:
    	case rlogTestCaseResult:
    	case extendedTestCaseResult:
        case statusTestCaseResult:
            return new JunitXMLResultsStatusWriter(testExecution, outputStream);
        case aggregatedTestCaseResult:
            return new JunitXMLResultsAggregateWriter(testExecution, outputStream);
            default:
                return null;
        }
    }
}
