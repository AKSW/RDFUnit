package org.aksw.rdfunit;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 9/24/15 4:43 PM
 * @version $Id: $Id
 */
public final class Resources {

    /** Constant <code>ONTOLOGY="/org/aksw/rdfunit/ns/core.ttl"</code> */
    public static final String ONTOLOGY = "/org/aksw/rdfunit/ns/core.ttl";

    /** core prefixes used throughout RDFUnit */
    public static final String PREFIXES = "/org/aksw/rdfunit/configuration/prefixes.ttl";

    /** Core RDFUnit patterns */
    public static final String PATTERNS = "/org/aksw/rdfunit/configuration/patterns.ttl";

    /** Auto generators / classic RDFUnit */
    public static final String AUTO_GENERATORS_OWL = "/org/aksw/rdfunit/configuration/autoGeneratorsOWL.ttl";
    /** Constant <code>AUTO_GENERATORS_DSP="/org/aksw/rdfunit/configuration/autoGen"{trunked}</code> */
    public static final String AUTO_GENERATORS_DSP = "/org/aksw/rdfunit/configuration/autoGeneratorsDSP.ttl";
    /** Constant <code>AUTO_GENERATORS_RS="/org/aksw/rdfunit/configuration/autoGen"{trunked}</code> */
    public static final String AUTO_GENERATORS_RS = "/org/aksw/rdfunit/configuration/autoGeneratorsRS.ttl";

    /** SHACL related resources */
    public static final String SHACL_ONTOLOGY = "/org/aksw/rdfunit/configuration/shacl.ttl";
    public static final String SHACL_CORE_CCs = "/org/aksw/rdfunit/configuration/shacl-core.ttl";
    public static final String SHACL_SHACL = "/org/aksw/rdfunit/configuration/shacl-shacl.ttl";

    /** Constant <code>SCHEMAS_LOV="/org/aksw/rdfunit/configuration/schemas"{trunked}</code> */
    public static final String SCHEMAS_LOV = "/org/aksw/rdfunit/configuration/schemas_custom.csv";
    /** Constant <code>SCHEMAS_CUSTOM="/org/aksw/rdfunit/configuration/schemas"{trunked}</code> */
    public static final String SCHEMAS_CUSTOM = "/org/aksw/rdfunit/configuration/schemas_lov.csv";

    private Resources() {}

}
