RDFUnit - JUnit Integration
==========

RDFUnit-JUnit Integration provides an interface to JUnit via a dedicated Runner which exposes single RDFUnit Test Cases as Unit-Tests.

### Usage
```java

@RunWith(RdfUnitJunitRunner.class)
@Schema(uri = "ontologies/foaf.rdf")
public static class TestRunner {

    @AdditionalData
    public RDFReader getCVs() {
        return new RDFModelReader(ModelFactory
                .createDefaultModel()
                .read("cv/required-cv.rdf"));
    }

    @TestInput
    public RDFReader getInputData() {
        return new RDFModelReader(ModelFactory
                .createDefaultModel()
                .read("inputmodels/foaf.rdf"));
    }

}
```

### What the RdfUnitJunitRunner will do:

1. Validate the Test-Class: `@Schema` and `@TestInput` must be declared; `@AdditionalData` is optional
2. Run the Test Generators against the Schema (can be local file or dereferencable URI)
3. Create generated TestCases for each `@TestInput`
4. Run tests individually hooked into the JUnit-Runtime

### What is currently not supported
- Manual Tests
- LOV integration
- JUnit: `@Rule`, `@Before`, `@After`
