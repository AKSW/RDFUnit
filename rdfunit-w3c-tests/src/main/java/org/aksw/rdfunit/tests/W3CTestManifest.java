package org.aksw.rdfunit.tests;


import com.google.common.collect.Streams;
import lombok.*;
import org.aksw.rdfunit.utils.JenaUtils;
import org.aksw.rdfunit.vocabulary.DATA_ACCESS_TESTS;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static com.google.common.collect.Streams.stream;
import static java.util.Comparator.comparing;

@RequiredArgsConstructor
public class W3CTestManifest {

    static final Resource PartialResult = ResourceFactory.createResource("http://www.w3.org/ns/shacl-test#partial");
    static final String resultFolder = "tests-results-nonconformant";


    @Getter @NonNull final Path sourceFile;

    @Getter(lazy = true) @NonNull private final Model model = JenaUtils.readModel(sourceFile.toUri());

    @Getter(lazy = true) @NonNull private final Resource manifestResource = findManifestResource();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public Stream<W3CTestManifest> getIncludes() {

        return stream(getManifestResource().listProperties(DATA_ACCESS_TESTS.include))
                .map(stmt -> resolvePathURI(stmt.getResource().getURI()))
                .sorted()
                .map(W3CTestManifest::new);
    }

    public Stream<W3CTestCase> getTestCases() {

        return stream(getManifestResource().listProperties(DATA_ACCESS_TESTS.entries))
                .flatMap(stmt -> Streams.stream(stmt.getObject().as(RDFList.class).iterator()))
                .filter(Resource.class::isInstance).map(Resource.class::cast)
                .sorted(comparing(Resource::getURI))
                .map(res -> new W3CTestCase(res, this));
    }

    public Stream<W3CTestCase> getTestCasesRecursive() {

        return Stream.concat(getTestCases(), getIncludes().flatMap(W3CTestManifest::getTestCasesRecursive));
    }

    private Resource findManifestResource() {


        java.util.List<Statement> manifestTypeStmts =
                getModel().listStatements(null, RDF.type, DATA_ACCESS_TESTS.Manifest).toList();

        if(manifestTypeStmts.size() != 1) {
            throw new RuntimeException("no Manifest resource or ambiguity due to several Manifests");
        }

        return manifestTypeStmts.get(0).getSubject();
    }

    @SneakyThrows(URISyntaxException.class)
    public Path resolvePathURI(String includeURI) {

        val includePath = Paths.get(new URI(includeURI));

        if(includePath.isAbsolute()) {
            return  includePath;
        } else {
            // sht:dataGraph <> => /manifest/path/manifest.ttl
            // sht:dataGraph <shared-data.ttl> => /manifest/path/shared-data.ttl
            return includeURI.isEmpty()? getSourceFile() : getSourceFile().getParent().resolve(includePath);
        }
    }
}