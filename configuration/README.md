## This folder contains basic RDFUnit configuration

On each build the files of this folder will be copied in the rdfunit-resources module and will be stored and available as java resources.

In order to override these values you can
* edit these files and rebuild the project
* provide files with the same name in the ../data/ folder (or the folder specified with `-f` in CLI) that will be additionally loaded