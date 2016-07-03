RDFUnit CLI docker image
==========

This is the up-to-date docker image for the CLI of RDFUnit.
For information about RDFUnit and the CLI please visit https://github.com/AKSW/RDFUnit

It is meant to execute a rdfunit command and then shutdown the container.
If the output of rdfunit on stdout is not enough or you want to include files in the container, a directory could be mounted via docker in order to create the output/result there or include files.

Here an example of usage:

```
docker run -it --rm yourname/rdfunit -d https://awesome.url/file -r aggregate
```

This creates a temporary docker container which runs the command, prints the results on stdout and stops plus removes itself.
For further usage of CLI visit https://github.com/AKSW/RDFUnit/wiki/CLI
