package org.aksw.rdfunit.sources;

import org.aksw.rdfunit.enums.TestAppliesTo;

/**
 * <p>ApplicationSource class.</p>
 *
 * @author Dimitris Kontokostas
 *         Description
 * @since 9/16/13 1:57 PM
 * @version $Id: $Id
 */
public class ApplicationSource implements Source {


    private final SourceConfig sourceConfig;

    ApplicationSource(String prefix, String uri) {
        this.sourceConfig = new SourceConfig(prefix, uri);
    }

    /** {@inheritDoc} */
    @Override
    public String getPrefix() {
        return sourceConfig.getPrefix();
    }

    /** {@inheritDoc} */
    @Override
    public String getUri() {
        return sourceConfig.getUri();
    }

    /** {@inheritDoc} */
    @Override
    public TestAppliesTo getSourceType() {
        return TestAppliesTo.Application;
    }


}
