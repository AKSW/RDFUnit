package org.aksw.rdfunit.io.writer;

import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.rdfunit.model.impl.results.DatasetOverviewResults;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.apache.jena.datatypes.xsd.XSDDateTime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 * <p>Abstract JunitXMLResultsWriter class.</p>
 *
 * @author Martin Bruemmer
 *         Writes results in JUnit XML format
 * @since 11/14/13 1:04 PM
 * @version $Id: $Id
 */
public abstract class AbstractJunitXmlResultsWriter implements RdfWriter {
    protected final TestExecution testExecution;
    private final OutputStream outputStream;

    /**
     * <p>Constructor for JunitXMLResultsWriter.</p>
     *
     * @param outputStream a {@link java.io.OutputStream} object.
     */
    public AbstractJunitXmlResultsWriter(TestExecution testExecution, OutputStream outputStream) {
        super();
        this.testExecution = testExecution;
        this.outputStream = outputStream;
    }

    /**
     * <p>Constructor for JunitXMLResultsWriter.</p>
     *
     * @param filename a {@link java.lang.String} object.
     */
    public AbstractJunitXmlResultsWriter(TestExecution testExecution, String filename) {
        this(testExecution, RdfStreamWriter.getOutputStreamFromFilename(filename));
    }

    /** {@inheritDoc} */
    @Override
    public void write(QueryExecutionFactory qef) throws RdfWriterException {
  
        try {
            // TODO not efficient StringBuilder.toString().getBytes()
            final String utf8 = "UTF8";
            outputStream.write(getHeader().toString().getBytes(utf8));
            outputStream.write(getTestExecutionStats().toString().getBytes(utf8));
            outputStream.write(getTestExecutionResults().toString().getBytes(utf8));
            outputStream.write(getFooter().toString().getBytes(utf8));
            outputStream.close();
        } catch (IOException e) {
            throw new RdfWriterException("Cannot write XML", e);
        }
    }


    protected abstract StringBuilder getResultsList() ;

    private StringBuilder getHeader() {
        StringBuilder header = new StringBuilder();
        header.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        return header;
    }

    private StringBuilder getFooter() {
        return new StringBuilder("</testsuite>");
    }

    private StringBuilder getTestExecutionStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("<testsuite name=\"").append(testExecution.getTestExecutionUri()).append("\" ");

        DatasetOverviewResults dor = testExecution.getDatasetOverviewResults();
        stats.append("timestamp=\"").append(dor.getEndTime()).append("\" ");
        String length = testLength(dor.getStartTime(), dor.getEndTime());
        if(length!=null) {
            stats.append("time=\"").append(length).append("\" ");
        }
        stats
                .append("tests=\"").append(dor.getTotalTests()).append("\" ")
                .append("failures=\"").append(dor.getFailedTests()).append("\" ")
                .append("errors=\"").append(dor.getTimeoutTests()+dor.getErrorTests()).append("\" ")
                .append("package=\"").append(testExecution.getTestedDatasetUri()).append("\" ")
                .append(">\n");

        return stats;
    }
    
    private String testLength(XSDDateTime datetimeStart, XSDDateTime datetimeEnd) {
    	long diff = datetimeEnd.asCalendar().getTimeInMillis() - datetimeStart.asCalendar().getTimeInMillis();
    	return String.format("%02d:%02d:%02d", 
    		    TimeUnit.MILLISECONDS.toHours(diff),
    		    TimeUnit.MILLISECONDS.toMinutes(diff) - 
    		    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)),
    		    TimeUnit.MILLISECONDS.toSeconds(diff) - 
    		    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
    }

    private StringBuilder getTestExecutionResults() {
        StringBuilder results = new StringBuilder();
        results.append(getResultsList());
        return results;
    }
}
