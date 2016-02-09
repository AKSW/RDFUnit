package org.aksw.rdfunit.io.format;

import com.google.common.collect.Sets;

import java.util.*;

/**
 * <p>SerialiazationFormatFactory class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 6/18/14 4:27 PM
 * @version $Id: $Id
 */
public final class SerialiazationFormatFactory {

    private SerialiazationFormatFactory() {
    }

    /**
     * Returns a list with all the defined serialization formats
     *
     * @return a {@link java.util.Collection} object.
     */
    public static Collection<SerializationFormat> getAllFormats() {
        ArrayList<SerializationFormat> serializationFormats = new ArrayList<>();

        // single graph formats
        serializationFormats.add(createTurtle());
        serializationFormats.add(createN3());
        serializationFormats.add(createNTriples());
        serializationFormats.add(createJsonLD());
        serializationFormats.add(createRDFJson());
        serializationFormats.add(createRDFXMLAbbrevOut());
        serializationFormats.add(createRDFXMLIn());
        serializationFormats.add(createRDFXMLOut());
        serializationFormats.add(createRDFa());
        serializationFormats.add(createHTML());
        serializationFormats.add(createJunitXml());
        
        // dataset formats
        serializationFormats.add(createNQads());
        serializationFormats.add(createTriG());
        serializationFormats.add(createTriX());


        return serializationFormats;
    }


    /**
     * <p>createHTML.</p>
     *
     * @return an HTML format as output only
     */
    public static SerializationFormat createHTML() {
        // HTML -> only output
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("html", "htm"));
        return new SerializationFormat(
                "html", SerializationFormatIOType.output, SerializationFormatGraphType.singleGraph, "html", "text/html", currentSynonyms);

    }
    
    /**
     * <p>createJUnit/XML.</p>
     *
     * @return an XML format as output only
     */
    public static SerializationFormat createJunitXml() {
        // XML -> only output
        HashSet<String> currentSynonyms = Sets.newHashSet("junit", "junit-xml", "junitxml");
        return new SerializationFormat(
                "junitxml", SerializationFormatIOType.output, SerializationFormatGraphType.singleGraph, "xml", "application/xml", currentSynonyms);

    }

    /**
     * <p>createTurtle.</p>
     *
     * @return a new TURTLE format for input/output
     */
    public static SerializationFormat createTurtle() {
        // Turtle -> input & output
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("turtle", "ttl"));
        return new SerializationFormat(
                "TURTLE", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.singleGraph, "ttl", "text/turtle", currentSynonyms);

    }

    /**
     * <p>createN3.</p>
     *
     * @return a new N3 format for input/output
     */
    public static SerializationFormat createN3() {
        // N3 -> input & output
        HashSet<String> currentSynonyms = Sets.newHashSet();
        return new SerializationFormat(
                "N3", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.singleGraph, "n3", "rdf+n3", currentSynonyms);
    }

    /**
     * <p>createNTriples.</p>
     *
     * @return a new NTRIPLES format for input/output
     */
    public static SerializationFormat createNTriples() {
        // ntriples -> input & output
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("n-triple", "n-triples", "ntriple", "ntriples", "nt"));
        return new SerializationFormat(
                "N-TRIPLE", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.singleGraph, "nt", "application/n-triples", currentSynonyms);

    }

    /**
     * <p>createJsonLD.</p>
     *
     * @return a new JSON-LD format for input/output
     */
    public static SerializationFormat createJsonLD() {
        // JSON-LD -> input & output
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("JSON-LD", "JSONLD", "JSON/LD"));
        return new SerializationFormat(
                "JSON-LD", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.singleGraph, "jsonld", "application/ld+json", currentSynonyms);
    }

    /**
     * <p>createRDFJson.</p>
     *
     * @return a new RDF/JSON format for input/output
     */
    public static SerializationFormat createRDFJson() {
        // RDF/JSON -> input & output
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("RDF/JSON", "RDF-JSON", "RDFJSON", "RJ"));
        return new SerializationFormat(
                "RDF/JSON", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.singleGraph, "rj", "application/rdf+json", currentSynonyms);
    }

    /**
     * <p>createRDFXMLAbbrevOut.</p>
     *
     * @return a new XML/RDF-ABBREV format for output only
     */
    public static SerializationFormat createRDFXMLAbbrevOut() {
        // RDF/XML-ABBREV -> output only
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("RDF/XML-ABBREV", "RDF-XML-ABBREV", "RDFXMLABBREV"));
        return new SerializationFormat(
                "RDF/XML-ABBREV", SerializationFormatIOType.output, SerializationFormatGraphType.singleGraph, "rdf", "application/rdf+xml", currentSynonyms);
    }

    /**
     * <p>createRDFXMLOut.</p>
     *
     * @return a new XML/RDF format for output only
     */
    public static SerializationFormat createRDFXMLOut() {
        // RDF/XML -> output (split it to disambiguate abbrev)
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("RDF/XML", "RDF-XML", "RDFXML"));
        return new SerializationFormat(
                "RDF/XML", SerializationFormatIOType.output, SerializationFormatGraphType.singleGraph, "rdf", "application/rdf+xml", currentSynonyms);

    }

    /**
     * <p>createRDFXMLIn.</p>
     *
     * @return a new XML/RDF format for input only
     */
    public static SerializationFormat createRDFXMLIn() {
        // RDF/XML -> input

        HashSet<String> currentSynonyms = Sets.newHashSet("RDF/XML", "RDF-XML", "RDFXML", "RDF/XML-ABBREV", "RDF-XML-ABBREV", "RDFXML-ABBREV", "RDFXMLABBREV", "RDF", "XML");
        return new SerializationFormat(
                "RDF/XML", SerializationFormatIOType.input, SerializationFormatGraphType.singleGraph, "rdf", "application/rdf+xml", currentSynonyms);

    }

    /**
     * <p>createRDFa.</p>
     *
     * @return a new RDFa format for input only
     * @since 0.7.2
     */
    public static SerializationFormat createRDFa() {
        // RDF/XML -> input
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("RDFa", "html", "htm"));
        return new SerializationFormat(
                "RDFA", SerializationFormatIOType.input, SerializationFormatGraphType.singleGraph, "html", "text/html", currentSynonyms);

    }

    /**
     * <p>createNQads.</p>
     *
     * @return a new N-Quads format for input only
     * @since 0.7.2
     */
    public static SerializationFormat createNQads() {
        // RDF/XML -> input
        HashSet<String> currentSynonyms = Sets.newHashSet(Arrays.asList("nq", "nquads", "n-quads", "nquad", "n-quad"));
        return new SerializationFormat(
                "NQuads", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.dataset, "nq", "text/n-quads", currentSynonyms);

    }

    /**
     * <p>createTriX.</p>
     *
     * @return a new N-Quads format for input only
     * @since 0.7.2
     */
    public static SerializationFormat createTriX() {
        // RDF/XML -> input
        HashSet<String> currentSynonyms = Sets.newHashSet(Collections.singletonList("trix"));
        return new SerializationFormat(
                "TriX", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.dataset, "trix", "application/trix", currentSynonyms);

    }

    /**
     * <p>createTriX.</p>
     *
     * @return a new N-Quads format for input only
     * @since 0.7.2
     */
    public static SerializationFormat createTriG() {
        // RDF/XML -> input
        HashSet<String> currentSynonyms = Sets.newHashSet(Collections.singletonList("trig"));
        return new SerializationFormat(
                "TriG", SerializationFormatIOType.inputAndOutput, SerializationFormatGraphType.dataset, "trix", "application/x-trig", currentSynonyms);

    }


}
