package org.aksw.rdfunit.validate.cli;

import java.util.Collection;
import org.aksw.rdfunit.RDFUnitConfiguration;
import picocli.CommandLine.Option;

public class Validate {

  @Option(names = { "-d", "--dataset-uri" }, description = "the dataset URI (required)", required = true)
  String datasetURI;

  @Option(names = { "-e", "--endpoint" }, description = "the endpoint URI to run the tests on (if no endpoint is provided RDFUnit will try to dereference the dataset-uri)")
  String endpointURI;

  @Option(names = { "-eu", "--endpoint-username" }, description = "the username for endpoint basic authentication")
  String username;

  @Option(names = { "-ep", "--endpoint-password" }, description = "the password for endpoint basic authentication")
  String password;

  @Option(names = { "-g", "--graph" }, description = "the graphs to use (separate multiple graphs with ',' (no whitespaces) (defaults to '')", split = ",")
  Collection<String> endpointGraphs;

  @Option(names = { "-u", "--uri" }, description = "the URI to use for dereferencing a dump if not the same with `dataset`")
      String dumpURI;

  @Option(names = { "-s", "--schemas" }, description = "the schemas used in the chosen graph "
      + "(comma separated prefixes without whitespaces according to http://lov.okfn.org/). If this option is missing RDFUnit will try to guess them automatically", split = ",")
  Collection<String> schemaUriPrefixes;

  @Option(names = { "-v", "--no-LOV" }, description = "Do not use the LOV service")
  boolean noLOV;


/*    cliOptions.addOption("i", "imports", false,
        "if set, in addition to the schemata provided or discovered, all transitively discovered import schemata (owl:imports) are included into the schema set");
    cliOptions.addOption("x", "excluded schemata", true,
        "the schemas excluded from test generation by default " +
        "(comma separated prefixes without whitespaces according to http://lov.okfn.org/)."
        );
    cliOptions.addOption("o", "output-format", true,
        "the output format of the validation results: html (default), turtle, n3, ntriples, json-ld, rdf-json, rdfxml, rdfxml-abbrev, junitxml");
    cliOptions.addOption("p", "enriched-prefix", true,
        "the prefix of this dataset used for caching the schema enrichment, e.g. dbo");
    cliOptions.addOption("C", "no-test-cache", false,
        "Do not load cached automatically generated test cases, regenerate them (Cached test cases are loaded by default)");
    cliOptions.addOption("M", "no-manual-tests", false,
        "Do not load any manually defined test cases (Manual test cases are loaded by default)");
    cliOptions.addOption("A", "no-auto-tests", false,
        "Do not load any schema / automatically generated test cases (Automatically generated test cases are loaded by default)");
    cliOptions.addOption("r", "result-level", true,
        "Specify the result level for the error reporting. One of status, aggregate, rlog, extended (default is aggregate).");
    cliOptions.addOption("l", "logging-level", true,
        "Not supported at the moment! will filter test cases based on logging level (notice, warn, error, etc).");
    cliOptions.addOption("T", "query-cache-ttl", true,
        "Specify the SPARQL Endpoint cache Time-To-Live (TTL) of the cache (in minutes) or '0' to disable it.");
    cliOptions.addOption("D", "query-delay", true,
        "Specify the delay between consecutive queries cache against an Endpoint or '0' to disable delay.");
    cliOptions.addOption("L", "query-limit", true,
        "Specify the maximum results from a SPARQL test query or '0' to disable limits.");
    cliOptions.addOption("P", "query-pagination", true,
        "Specify a pagination option for retrieving big results or '0' to disable it.");
    cliOptions.addOption("c", "test-coverage", false, "Calculate test-coverage scores");
    cliOptions.addOption("f", "data-folder", true,
        "the location of the data folder (defaults to '../data/' or '~/.rdfunit). " +
        "This is where the results and the caches are stored. " +
        "If none exists, bundled versions will be loaded.'");
*/


//  RDFUnitConfiguration configuration = readDatasetUriAndInitConfiguration(commandLine);

}
