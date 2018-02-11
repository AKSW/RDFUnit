package org.aksw.rdfunit.model.impl.results;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.jena.datatypes.xsd.XSDDateTime;

import java.util.Calendar;

/**
 * Holds the overview results for a dataset
 *
 * @author Dimitris Kontokostas
 * @since 6/11/14 7:48 AM
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

    public DatasetOverviewResults() {
        reset();
    }

    /*
    * reset all variables
    * */
    public final void reset() {
        successfulTests = 0;
        failedTests = 0;
        timeoutTests = 0;
        errorTests = 0;
        individualErrors = 0;

        startTime = null;
        endTime = null;
    }

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


    public long getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    public long getSuccessfulTests() {
        return successfulTests;
    }

    public void increaseSuccessfulTests() {
        this.successfulTests++;
    }

    public long getFailedTests() {
        return failedTests;
    }

    public void increaseFailedTests() {
        this.failedTests++;
    }

    public long getTimeoutTests() {
        return timeoutTests;
    }

    public void increaseTimeoutTests() {
        this.timeoutTests++;
    }

    public long getErrorTests() {
        return errorTests;
    }

    public void increaseErrorTests() {
        this.errorTests++;
    }

    public long getIndividualErrors() {
        return individualErrors;
    }

    public void increaseIndividualErrors(long totalErrors) {
        this.individualErrors = this.individualErrors + totalErrors;
    }

    public XSDDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime() {
        this.startTime = new XSDDateTime(Calendar.getInstance());
    }

    public XSDDateTime getEndTime() {
        return endTime;
    }

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
