package org.aksw.rdfunit.dqv;

/**
 * @author Dimitris Kontokostas
 * @since 21/1/2016 9:20 μμ
 */
public class QualityMeasure {
    private final String testExecutionUri;
    private final String DqvMetricUri;
    private final double value;

    public QualityMeasure(String testExecutionUri, String dqvMetricUri, double value) {
        this.testExecutionUri = testExecutionUri;
        DqvMetricUri = dqvMetricUri;
        this.value = value;
    }

    public String getTestExecutionUri() {
        return testExecutionUri;
    }

    public String getDqvMetricUri() {
        return DqvMetricUri;
    }

    public double getValue() {
        return value;
    }
}
