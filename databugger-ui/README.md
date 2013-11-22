Databugger -UI
==========

In order to deploy the ui in java server just run the following command from the root folder

$mvn clean install

then copy the `war` file from the `databugger-ui/target/` folder in your web root

To run / test the UI locally you can run

$mvn jetty:run

(note that while the `war` file contains the `../data` folder for a local run you need to copy the folder in `databugger-ui/src/main/webapp/data/`
