package org.aksw.rdfunit.io.format;

import org.aksw.rdfunit.enums.SerializationFormatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 6/18/14 4:27 PM
 */
public class FormatTypeFactory {
    public static Collection<FormatType> getAllFormats() {
        ArrayList<FormatType> formatTypes = new ArrayList<>();

        formatTypes.add(createTurtle());
        formatTypes.add(createN3());
        formatTypes.add(createNTriples());
        formatTypes.add(createRDFXMLAbbrevOut());
        formatTypes.add(createRDFXMLIn());
        formatTypes.add(createRDFXMLOut());
        formatTypes.add(createHTML());

        return formatTypes;
    }

    public static FormatType createHTML() {
        // HTML -> only output
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("html", "htm"));
        return new FormatType(
                "html", SerializationFormatType.output, "html" , "text/html",  currentSynonyms);

    }

    public static FormatType createTurtle() {
        // Turtle -> input & output
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("turtle", "ttl"));
        return new FormatType(
                "TURTLE", SerializationFormatType.inputAndOutput, "ttl" , "text/turtle",  currentSynonyms);

    }

    public static FormatType createN3() {
        // N3 -> input & output
        HashSet<String> currentSynonyms = new HashSet<>();
        return new FormatType(
                "N3", SerializationFormatType.inputAndOutput, "n3" , "rdf+n3",  currentSynonyms);
    }

    public static FormatType createNTriples() {
        // ntriples -> input & output
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("n-triple", "n-triples", "ntriple", "ntriples"));
        return new FormatType(
                "N-TRIPLE", SerializationFormatType.inputAndOutput, "nt" , "application/n-triples",  currentSynonyms);

    }

    public static FormatType createRDFXMLAbbrevOut() {
        // RDF/XML-ABBREV -> output only
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("RDF/XML-ABBREV", "RDF-XML-ABBREV", "RDFXMLABBREV"));
        return new FormatType(
                "RDF/XML-ABBREV", SerializationFormatType.output, "rdf" , "application/rdf+xml",  currentSynonyms);
    }

    public static FormatType createRDFXMLOut() {
        // RDF/XML -> output (split it to disambiguate abbrev)
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("RDF/XML", "RDF-XML", "RDFXML"));
        return new FormatType(
                "RDF/XML", SerializationFormatType.output, "rdf" , "application/rdf+xml",  currentSynonyms);

    }

    public static FormatType createRDFXMLIn() {
        // RDF/XML -> input
        HashSet<String> currentSynonyms = new HashSet<>();
        currentSynonyms.addAll(Arrays.asList("RDF/XML", "RDF-XML", "RDFXML", "RDF/XML-ABBREV", "RDF-XML-ABBREV", "RDFXMLABBREV"));
        return new FormatType(
                "RDF/XML", SerializationFormatType.input, "rdf" , "application/rdf+xml",  currentSynonyms);

    }
}
