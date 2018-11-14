#List of default vocabularies

Vocabularies in this dictionary are loaded by default, no need for an entry in the schemaDecl.csv.
Use the vann: <http://purl.org/vocab/vann/> vocabulary to point out namespace and prefix (else the ontology is ignored!).

Example
```
    <> a owl:Ontology ;
    rdfs:label "..."@en ;
    rdfs:comment "..."@en ;
    vann:preferredNamespacePrefix "pfx" ;
    vann:preferredNamespaceUri "http://www.w3.org/ns/pfx/" .
```

Note: make sure to include the trailing separator of the namespace (usually '/' or '#')