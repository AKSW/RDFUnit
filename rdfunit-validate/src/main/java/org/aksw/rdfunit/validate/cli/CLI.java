package org.aksw.rdfunit.validate.cli;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "rdfunit", description = "...", mixinStandardHelpOptions = true, subcommands = {GenerateCommand.class})
public class CLI implements Callable {

  public static void main(String[] args) {
    //new CommandLine(new CLI()).usage(System.out);
    System.exit(new CommandLine(new CLI()).execute(args));
  }

  private void run() {

  }

  @Override
  public Object call() throws Exception {
    return null;
  }
}
