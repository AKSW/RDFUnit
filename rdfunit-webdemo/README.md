## RDFUnit Web Demo

This module contains the code used to run the RDFUnit WebDemo at http://rdfunit.aksw.org/demo

In order to deploy the ui in java server just run the following command from the root folder

```
$mvn -pl rdfunit-webdemo -am clean install
```

then copy the `war` file from the `rdfunit-webdemo/target/` folder in your web root

To run / test the UI locally you can run

```
$mvn jetty:run
```

