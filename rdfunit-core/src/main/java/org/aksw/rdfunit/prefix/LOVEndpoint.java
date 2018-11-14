package org.aksw.rdfunit.prefix;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.aksw.rdfunit.io.format.SerializationFormatFactory;
import org.aksw.rdfunit.io.format.SerializationFormatGraphType;
import org.aksw.rdfunit.io.format.SerializationFormatIOType;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.function.library.leviathan.log;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LOV (lov.okfn.org) is a vocabulary repository that we re-use to do prefix dereferencing
 * and locate the exact definition IRI in case TCN is not properly configured
 *
 * @author Dimitris Kontokostas
 * @since 3 /31/15 4:15 PM

 */
@Slf4j
public final class LOVEndpoint {

    private static final SerializationFormat RDFXML = SerializationFormatFactory.createRDFXMLOut();
    private static final SerializationFormat TURTLE = SerializationFormatFactory.createTurtle();
    private static final SerializationFormat NTRIPLES = SerializationFormatFactory.createNTriples();
    private static final SerializationFormat N3SER = SerializationFormatFactory.createN3();
    private static final SerializationFormat OWLRDFXML = new SerializationFormat(
            "RDF/XML",
            SerializationFormatIOType.output,
            SerializationFormatGraphType.singleGraph,
            "owl",
            "application/rdf+xml",
            Sets.newHashSet()
    );
    private static final List<SerializationFormat> allRdfFormats = Arrays.asList(OWLRDFXML, RDFXML, TURTLE, N3SER, NTRIPLES);

    // representing a general request header for all accepted types
    private static final SerializationFormat GENERALFORMAT = new SerializationFormat(
            "TEXT/HTML",
            SerializationFormatIOType.output,
            SerializationFormatGraphType.dataset,
            "xxx",
            allRdfFormats.stream().map(SerializationFormat::getMimeType).collect(Collectors.joining(",")) +
                    ";q=1.0,text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            Sets.newHashSet()
    );

    //in some cases we have to follow redirects via the html page
    private static final SerializationFormat TEXTHTML = new SerializationFormat(
            "TEXT/HTML",
            SerializationFormatIOType.output,
            SerializationFormatGraphType.dataset,
            "html",
            "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8",
            Sets.newHashSet()
    );

    private static final String LOV_ENDPOINT_URI = "https://lov.linkeddata.es/dataset/lov/sparql";
    private static final String LOV_GRAPH = "https://lov.linkeddata.es/dataset/lov";
    private static final String LOV_SPARQL_QUERY =
            "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX vann:<http://purl.org/vocab/vann/>\n" +
            "PREFIX voaf:<http://purl.org/vocommons/voaf#>\n" +
            "SELECT ?vocabURI ?vocabPrefix ?vocabNamespace ?definedBy\n" +
            "WHERE{\n" +
            "\t?vocabURI a voaf:Vocabulary.\n" +
            "\t?vocabURI vann:preferredNamespacePrefix ?vocabPrefix.\n" +
            "\t?vocabURI vann:preferredNamespaceUri ?vocabNamespace.\n" +
            "\tOPTIONAL {?vocabURI rdfs:isDefinedBy ?definedBy.}\n" +
            "} \n" +
            "ORDER BY ?vocabPrefix ";


    public List<SchemaEntry> getAllLOVEntries() {

        List<SchemaEntry> lovEntries = new LinkedList<>();
        QueryExecutionFactory qef = new QueryExecutionFactoryHttp(LOV_ENDPOINT_URI, Collections.singletonList(LOV_GRAPH));


        try (QueryExecution qe = qef.createQueryExecution(LOV_SPARQL_QUERY)) {

            ResultSet selection = qe.execSelect();
            selection.forEachRemaining( row -> {

                String prefix = row.get("vocabPrefix").asLiteral().getLexicalForm();
                String vocab = row.get("vocabURI").asResource().getURI();
                String ns = row.get("vocabNamespace").asLiteral().getLexicalForm();
                String definedBy = ns; // default
                if (ns == null || ns.isEmpty()) {
                    ns = vocab;
                }

                if (row.get("definedBy") != null) {
                    definedBy = row.get("definedBy").asResource().getURI();
                }
                lovEntries.add(new SchemaEntry(prefix, vocab, ns, definedBy));
            });
        } catch (Exception e) {
            log.error("Encountered error when reading schema information from LOV, schema prefixes & auto schema discovery might not work as expected", e);
        }

        return lovEntries;
    }

    public void writeAllLOVEntriesToFile(String filename)  {

        List<SchemaEntry> lovEntries = getAllLOVEntries();
        Collections.sort(lovEntries);
        String header =
                "#This file is auto-generated from the (amazing) LOV service\n" +
                "# if you don't want to load this use the available CLI / code options \n" +
                "# To override some of it's entries use the schemaDecl.csv";


        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8)) ){

            out.write(header);
            out.write("\n");

            int count = 0;

            for (SchemaEntry entry: lovEntries) {
                count ++;
                SchemaEntry updatedEntry = extractResourceLocation(entry);
                out.write(updatedEntry.getPrefix());
                out.write(',');
                out.write(updatedEntry.getVocabularyNamespace());
                out.write(',');
                out.write(updatedEntry.getVocabularyDefinedBy());
                out.write('\n');

                if(count % 100 == 0)
                    log.info("Checked " + count + " of " + lovEntries.size() + " vocabularies.");
            }
        } catch (IOException e) {
            log.info("Cannot write to file", e);
        }
    }

    /**
     * Will try to get a responsive content location trying
     * 1. VocabularyDefinedBy location
     * 2. VocabularyURI location
     * @param entry - the origin URL which is used as basis
     * @return - the optional content location
     */
    public SchemaEntry extractResourceLocation(SchemaEntry entry) {
        Optional<String> actualResourceUri = Optional.empty();
        if (! entry.getVocabularyDefinedBy().equals(entry.getVocabularyNamespace()))
            actualResourceUri = getContentLocation(entry.getVocabularyDefinedBy(), GENERALFORMAT, Lists.newArrayList());

        if(! actualResourceUri.isPresent())
            actualResourceUri = getContentLocation(entry.getVocabularyURI(), GENERALFORMAT, Lists.newArrayList());

        // if still nothing was found, we try to pretend to be a browser and do it over again, since some resources have really bad redirecting
        if (! actualResourceUri.isPresent() && ! entry.getVocabularyDefinedBy().equals(entry.getVocabularyNamespace()))
            actualResourceUri = getContentLocation(entry.getVocabularyDefinedBy(), TEXTHTML, Lists.newArrayList());

        if(! actualResourceUri.isPresent())
            actualResourceUri = getContentLocation(entry.getVocabularyURI(), TEXTHTML, Lists.newArrayList());

        if(! actualResourceUri.isPresent()) {
            log.info("Could not find resource for " + entry.getVocabularyURI());
            return entry;
        }
        return new SchemaEntry(entry.getPrefix(), entry.getVocabularyURI(), entry.getVocabularyNamespace(), actualResourceUri.get());
    }

    private Set<SerializationFormat> findFormatByMimeType(String mimeType){
        if(mimeType == null || mimeType.isEmpty())
            return Collections.emptySet();
        return allRdfFormats.stream().filter(x -> x.getMimeType().equals(mimeType.trim().toLowerCase())).collect(Collectors.toSet());
    }

    /**
     * Will try to get a responsive content location by adding suffixes based on the given SerializationFormats
     * @param currentUrl - the base url onto which to concatenate the suffixes
     * @param formats - the list of SerializationFormats to try out
     * @param redirects - list of established redirects which led to this point
     * @return - the optional content location
     */
    private Optional<String> getContentVersions(String currentUrl, Collection<SerializationFormat> formats, ArrayList<String> redirects){
        Optional<SerializationFormat> test = formats.stream().filter(x -> currentUrl.trim().endsWith(x.getExtension())).findFirst();
        if(currentUrl != null && ! test.isPresent()){
            for(SerializationFormat format : formats) {
                String tryOwl = StringUtils.replacePattern(currentUrl, "#$", "");
                tryOwl = StringUtils.replacePattern(tryOwl, "/$", "");
                tryOwl = StringUtils.replacePattern(tryOwl, "\\.html$", "");
                tryOwl = StringUtils.replacePattern(tryOwl, "\\.htm$", "");
                Optional<String> res = getContentLocation(tryOwl + "." + format.getExtension(), format, redirects);
                if (res.isPresent())
                    return res;
                res = getContentLocation(tryOwl + "/ontology." + format.getExtension(), format, redirects);
                if (res.isPresent())
                    return res;
                res = getContentLocation(tryOwl + "/schema." + format.getExtension(), format, redirects);
                if (res.isPresent())
                    return res;
            }
        }
        return Optional.empty();
    }

    /**
     * Will try to determine a possible location of the ontology document (in any RDF serialization), following redirects and alternatives
     * @param urlStr - the origin url
     * @param format - the initial serialization format to try
     * @param redirects - list of established redirects which led to this point
     * @return - the optional content location
     */
    private Optional<String> getContentLocation(String urlStr, SerializationFormat format, ArrayList<String> redirects){
        if(urlStr == null || urlStr.isEmpty() || redirects.contains(urlStr.trim()))
            return Optional.empty();

        redirects.add(urlStr.trim());
        try {
            URL url = new URL(urlStr);
            // get the response
            HttpResponse conn = TrustingUrlConnection.executeHeadRequest(url.toURI(), format);
            // extract ContentType
            Header cth = conn.getFirstHeader("Content-Type");
            // only use the most prevalent
            String ct = cth != null ? cth.getValue().split("([,;])")[0] : null;
            // translate ContentType into MimeType
            Set<SerializationFormat> ctf = findFormatByMimeType(ct);

            StatusLine status = conn.getStatusLine();
            //fill in all intermediate redirects
            Stream.of(conn.getHeaders(TrustingUrlConnection.HEADERKEY)).forEach(x -> redirects.add(x.getValue().trim()));
            String lastRedirect = redirects.get(redirects.size()-1);

            // deal with errors
            if(status.getStatusCode() >= 400){
                String resp = status.getReasonPhrase();
                if(redirects.size() <= 1)
                    log.error("Error while retrieving " + urlStr + " :" + status.getStatusCode() + " " + resp);
                return Optional.empty();
            }

            // if we have a ContentType
            if(ct != null){
                // if the ContentType == HTML
                if(ct.contains("html")) {
                    return tryToFindHtmlAlternative(format, redirects, url, conn, lastRedirect);
                }
                // if the ContentType is a well known RDF serialization
                else if(! ctf.isEmpty()){
                    Optional<String> zw = getContentVersions(lastRedirect, ctf, redirects);
                    if (zw.isPresent())
                        return zw;
                }
            }
            //default if nothing was found we return the origin and hope for the best :)
            return Optional.of(lastRedirect);
        } catch (IOException | URISyntaxException e) {
            log.debug("Error while retrieving " + urlStr, e);
            return Optional.empty();
        }
    }

    private Optional<String> tryToFindHtmlAlternative(SerializationFormat format, ArrayList<String> redirects, URL url, HttpResponse conn, String lastRedirect) throws URISyntaxException {
        Header clh = conn.getFirstHeader("Content-Location");
        String cl = clh != null ? clh.getValue() : null;

        //if we have no specific ContentLocation
        if (cl == null || cl.isEmpty()) {
            return getContentVersions(lastRedirect, allRdfFormats, redirects);
        } else {
            String cleaned = StringUtils.stripEnd(url.toString(), "#?/");
            URI u = null;
            try {
                u = new URI(cleaned);
            } catch (URISyntaxException ignored) {
            }

            //if we found no absolute URI
            if (u == null) {
                //we try to concatenate the relative URI to the origin (looking for an overlap)
                int ind = Math.max(Math.max(cleaned.lastIndexOf("."), cleaned.lastIndexOf("/")), cleaned.length());
                String resetToLastPathSection = cleaned.substring(0, ind);
                String longestOverlap = org.aksw.rdfunit.utils.StringUtils.findLongestOverlap(resetToLastPathSection, cl);

                String urlPrefix = resetToLastPathSection.substring(0, resetToLastPathSection.length() - longestOverlap.length());
                u = new URI(urlPrefix + (cl.startsWith("/") ? "" : "/") + cl);
            }
            //if we already checked this address we try different rdf extensions on it
            if (redirects.contains(u.toString())) {
                Optional<String> zw = getContentVersions(lastRedirect, allRdfFormats, redirects);
                if (zw.isPresent())
                    return zw;
            }

            return getContentLocation(u.toString(), format, redirects);
        }
    }

    /**
     * Will start the crawling of the LOV endpoint
     * @param args - if provided, the first argument is the file location of the results, else the default schemaLOV.csv is used
     */
    public static void main(String[] args) {
        LOVEndpoint lov = new LOVEndpoint();
        String file = args.length > 0 ? args[1].trim() : "rdfunit-model/src/main/resources/org/aksw/rdfunit/configuration/schemaLOV.csv";
        lov.writeAllLOVEntriesToFile(file);
    }
}
