package org.aksw.rdfunit.model.impl.results;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.jena.datatypes.xsd.XSDDateTime;

import java.util.Calendar;

/**
 * <p>DatasetOverviewResults class.</p>
 *
 * @author Dimitris Kontokostas
 *         Holds the overview results for a dataset
 * @since 6/11/14 7:48 AM
 * @version $Id: $Id
 */
@ToString
@EqualsAndHashCode
public class DatasetOverviewResults {

    private long totalTests = 0;
    private long successfulTests = 0;
    private long failedTests = 0;
    private long timeoutTests = 0;
    private long errorTests = 0;
    private long individualErrors = 0;

    private XSDDateTime startTime;
    private XSDDateTime endTime;

    /**
     * <p>Constructor for DatasetOverviewResults.</p>
     */
    public DatasetOverviewResults() {
        reset();
    }

    /*
    * reset all variables
    * */
    /**
     * <p>reset.</p>
     */
    public final void reset() {
        successfulTests = 0;
        failedTests = 0;
        timeoutTests = 0;
        errorTests = 0;
        individualErrors = 0;

        startTime = null;
        endTime = null;
    }

    /**
     * <p>set.</p>
     *
     * @param results a {@link DatasetOverviewResults} object.
     */
    public void set(DatasetOverviewResults results) {
        this.totalTests = results.getTotalTests();
        this.successfulTests = results.getSuccessfulTests();
        this.failedTests = results.getFailedTests();
        this.timeoutTests = results.getTimeoutTests();
        this.errorTests = results.getErrorTests();
        this.individualErrors = results.getIndividualErrors();

        this.startTime = results.getStartTime();
        this.endTime = results.getEndTime();
    }


    /**
     * <p>Getter for the field <code>totalTests</code>.</p>
     *
     * @return a long.
     */
    public long getTotalTests() {
        return totalTests;
    }

    /**
     * <p>Setter for the field <code>totalTests</code>.</p>
     *
     * @param totalTests a long.
     */
    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    /**
     * <p>Getter for the field <code>successfulTests</code>.</p>
     *
     * @return a long.
     */
    public long getSuccessfulTests() {
        return successfulTests;
    }

    /**
     * <p>increaseSuccessfulTests.</p>
     */
    public void increaseSuccessfulTests() {
        this.successfulTests++;
    }

    /**
     * <p>Getter for the field <code>failedTests</code>.</p>
     *
     * @return a long.
     */
    public long getFailedTests() {
        return failedTests;
    }

    /**
     * <p>increaseFailedTests.</p>
     */
    public void increaseFailedTests() {
        this.failedTests++;
    }

    /**
     * <p>Getter for the field <code>timeoutTests</code>.</p>
     *
     * @return a long.
     */
    public long getTimeoutTests() {
        return timeoutTests;
    }

    /**
     * <p>increaseTimeoutTests.</p>
     */
    public void increaseTimeoutTests() {
        this.timeoutTests++;
    }

    /**
     * <p>Getter for the field <code>errorTests</code>.</p>
     *
     * @return a long.
     */
    public long getErrorTests() {
        return errorTests;
    }

    /**
     * <p>increaseErrorTests.</p>
     */
    public void increaseErrorTests() {
        this.errorTests++;
    }

    /**
     * <p>Getter for the field <code>individualErrors</code>.</p>
     *
     * @return a long.
     */
    public long getIndividualErrors() {
        return individualErrors;
    }

    /**
     * <p>increaseIndividualErrors.</p>
     *
     * @param totalErrors a long.
     */
    public void increaseIndividualErrors(long totalErrors) {
        this.individualErrors = this.individualErrors + totalErrors;
    }

    /**
     * <p>Getter for the field <code>startTime</code>.</p>
     *
     * @return a {@link org.apache.jena.datatypes.xsd.XSDDateTime} object.
     */
    public XSDDateTime getStartTime() {
        return startTime;
    }

    /**
     * <p>Setter for the field <code>startTime</code>.</p>
     */
    public void setStartTime() {
        this.startTime = new XSDDateTime(Calendar.getInstance());
    }

    /**
     * <p>Getter for the field <code>endTime</code>.</p>
     *
     * @return a {@link org.apache.jena.datatypes.xsd.XSDDateTime} object.
     */
    public XSDDateTime getEndTime() {
        return endTime;
    }

    /**
     * <p>Setter for the field <code>endTime</code>.</p>
     */
    public void setEndTime() {
        this.endTime = new XSDDateTime(Calendar.getInstance());
    }

    public void setSuccessfulTests(long successfulTests) {
        this.successfulTests = successfulTests;
    }

    public void setFailedTests(long failedTests) {
        this.failedTests = failedTests;
    }

    public void setTimeoutTests(long timeoutTests) {
        this.timeoutTests = timeoutTests;
    }

    public void setErrorTests(long errorTests) {
        this.errorTests = errorTests;
    }

    public void setStartTime(XSDDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(XSDDateTime endTime) {
        this.endTime = endTime;
    }

    public void setIndividualErrors(long individualErrors) {
        this.individualErrors = individualErrors;
    }
}
