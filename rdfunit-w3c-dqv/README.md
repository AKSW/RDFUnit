RDFUnit - W3c Data Quality Vocabulary (DQV)
==========

RDFUnit- W3c DQV provides an API to generate DQV reports from Test Executions

### Usage
```java

    // read test executions from a serialized RDF file
    // Note that there might be multiple ones in a file
    Model testExecutionReader = new RDFStreamReader("{RDFUnit results file}").read();

    for (Resource testExecutionResource: testExecutionReader.listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {
        TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

        Collection<QualityMeasure> report = new DqvReport(testExecution).getQualityMeasures();

        Model model = ModelFactory.createDefaultModel();
        DqvReportWriter.create(report).write(model);

        new RDFFileWriter("te." + testExecution.getTestExecutionUri()).write(model);
    }

```
