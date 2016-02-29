package org.aksw.rdfunit.dqv;

import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.io.writer.RdfFileWriter;
import org.aksw.rdfunit.io.writer.RdfWriter;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.model.readers.results.TestExecutionReader;
import org.aksw.rdfunit.vocabulary.RDFUNITv;
import org.apache.commons.cli.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;

import java.util.Collection;

public class RdfunitDqv {

    private static final Options cliOptions = generateCLIOptions();

    public static void main(String[] args) throws Exception {

        CommandLine commandLine = parseArguments(args);

        if (commandLine.hasOption("h")) {
            displayHelp();
            System.exit(0);
        }

        RdfReader input = getInputReader(commandLine);
        RdfWriter output = getOutputWriter(commandLine);

        Model model = ModelFactory.createDefaultModel();

        for (Resource testExecutionResource: input.read().listResourcesWithProperty(RDF.type, RDFUNITv.TestExecution).toList()) {

            TestExecution testExecution = TestExecutionReader.create().read(testExecutionResource);

            Collection<QualityMeasure> report = new DqvReport(testExecution, MetricMapper.createDefault()).getQualityMeasures();


            DqvReportWriter.create(report).write(model);
        }

        output.write(model);
    }


    private static Options generateCLIOptions() {
        Options cliOptions = new Options();

        cliOptions.addOption("h", "help", false, "show this help message");
        cliOptions.addOption("i", "input", true,
                "the input RDF file (required)");
        cliOptions.addOption("o", "output", true,
                "the output RDF file (uses standard output by default)");

        return cliOptions;
    }

    private static CommandLine parseArguments(String[] args) throws ParseException {
        CommandLineParser cliParser = new DefaultParser();
        return cliParser.parse(cliOptions, args);
    }

    private static RdfReader getInputReader(CommandLine commandLine) {
        if (!commandLine.hasOption("i")) {
            displayHelp();
            throw new IllegalArgumentException("Error: Required argument '-i' is missing.");
        }
        return RdfReaderFactory.createResourceOrFileOrDereferenceReader(commandLine.getOptionValue("i"));
    }

    private static RdfWriter getOutputWriter(CommandLine commandLine) {
        if (!commandLine.hasOption("o")) {
            displayHelp();
            throw new IllegalArgumentException("Error: Required argument '-o' is missing.");
        }
        return new RdfFileWriter(commandLine.getOptionValue("o"));
    }

    private static void displayHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("rdqv", cliOptions);
    }

}
