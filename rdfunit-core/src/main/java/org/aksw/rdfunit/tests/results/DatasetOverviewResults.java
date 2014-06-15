package org.aksw.rdfunit.tests.results;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;

import java.util.Calendar;

/**
 * User: Dimitris Kontokostas
 * Holds the overview results for a dataset
 * Created: 6/11/14 7:48 AM
 */
public class DatasetOverviewResults {

    private long totalTests = 0;
    private long successfullTests = 0;
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
    public void reset() {
        successfullTests = 0;
        failedTests = 0;
        timeoutTests = 0;
        errorTests = 0;
        individualErrors = 0;

        startTime = null;
        endTime = null;
    }


    public long getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(long totalTests) {
        this.totalTests = totalTests;
    }

    public long getSuccessfullTests() {
        return successfullTests;
    }

    public void increaseSuccessfullTests() {
        this.successfullTests++;
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
}
