package org.aksw.rdfunit.validate.cli;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.aksw.rdfunit.RDFUnit;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.io.writer.RdfStreamWriter;
import org.aksw.rdfunit.model.interfaces.GenericTestCase;
import org.aksw.rdfunit.model.interfaces.TestGenerator;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.tests.generators.CompositeTestGenerator;
import org.aksw.rdfunit.tests.generators.GenerateAndCacheRdfUnitTestGenerator;
import org.aksw.rdfunit.tests.generators.RdfUnitTestGenerator;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.tests.generators.TagRdfUnitTestGenerator;
import org.aksw.rdfunit.utils.TestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "generate", description = "Generate tests for a vocabulary",
    mixinStandardHelpOptions = true)
public final class GenerateCommand implements Callable {

  Logger log = LoggerFactory.getLogger(CLI.class);

  @Option(names = {"-p",
      "--prefix"}, description = "Overrule prefix, by default uses vann:preferredNamespacePrefix (if defined in schema, else '').")
  private String prefix = "";

  @Option(names = {"-u", "--uri"}, required = true, description = "Schema uri.")
  private String uri = "";

  @Option(names = {"-s",
      "--schema"}, required = true, description = "Path to schema file (owl/rdfs/shacl) to generate tests for.")
  private String schemaPath = "";

  @Option(names = {"-o",
      "--output"}, description = "A file location to write the tests (default: '-' for stdout).", defaultValue = "-")
  private String output;

  @Option(names = {"-f",
      "--data-folder"}, description = "The location of the data folder to write the tests to caches.")
  private String datafolder = null;

  @Option(names = {"-w",
      "--addWhereClause"}, description = "Add the whereClause explicitly to PatternBasedTestCases.")
  private boolean addExplicitWhere;


  @Override
  public Object call() throws Exception {

    RdfReader rdfReader = RdfReaderFactory.createFileOrResourceReader(schemaPath, uri);

    SchemaSource schemaSource = SchemaSourceFactory
        .createSchemaSourceSimple(prefix, uri, rdfReader);

    log.info("Generating tests");

    RDFUnit rdfUnit = RDFUnit
        .createWithAllGenerators()
        .init();

    Collection<TestGenerator> testGenerators = rdfUnit.getAutoGenerators();

    ImmutableList.Builder<RdfUnitTestGenerator> builder = ImmutableList.builder();
    builder.add(new TagRdfUnitTestGenerator(testGenerators));
    builder.add(new ShaclTestGenerator());
    RdfUnitTestGenerator g;

    if (datafolder != null) {
      String testFolder = this.datafolder + "tests/";
      g = new GenerateAndCacheRdfUnitTestGenerator(new CompositeTestGenerator(builder.build()),
          testFolder);
    } else {
      g = new CompositeTestGenerator(builder.build());
    }

    Collection<GenericTestCase> tests = new ArrayList<>(g.generate(schemaSource));

    if (!tests.isEmpty()) {
      if (output.trim().equals("-")) {
        TestUtils.writeTestsToFile(tests, new RdfStreamWriter(System.out), addExplicitWhere);
      } else {
        File outputFile = new File(output);
        outputFile.getParentFile().mkdirs();

        TestUtils.writeTestsToFile(tests, new RdfFileWriter(outputFile.getAbsolutePath()));
      }
    }

    return null;
  }
}
