package org.aksw.rdfunit.webdemo.presenter;

import org.aksw.rdfunit.RDFUnitConfiguration;
import org.aksw.rdfunit.services.FormatService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SourceFactory;
import org.aksw.rdfunit.webdemo.RDFUnitDemoSession;
import org.aksw.rdfunit.webdemo.utils.SchemaOption;
import org.aksw.rdfunit.webdemo.view.SchemaSelectorView;

import java.util.Arrays;
import java.util.Collection;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 8/30/14 12:24 PM
 */
public class SchemaSelectorPresenter implements SchemaSelectorView.SchemaSelectorViewListener {

    private final SchemaSelectorView schemaSelectorView;

    private static final long fileLimit = 10*1024*1024;

    public SchemaSelectorPresenter(SchemaSelectorView schemaSelectorView) {
        this.schemaSelectorView = schemaSelectorView;

        schemaSelectorView.addListener(this);
    }

    @Override
    public boolean schemaIsSet(SchemaOption schemaOption, Collection<SchemaSource> schemaSources, String text, String format) {
        RDFUnitConfiguration configuration = RDFUnitDemoSession.getRDFUnitConfiguration();

        if (configuration == null) {
            schemaSelectorView.setMessage("Data Selection not configured properly", true);
            return false;
        }

        switch (schemaOption) {
            case AUTO_OWL:
                configuration.setAutoSchemataFromQEF(configuration.getTestSource().getExecutionFactory());
                break;
            case SPECIFIC_URIS:
                configuration.setSchemata(schemaSources);
                break;
            case CUSTOM_TEXT:
                String oficialFormat = FormatService.getInputFormat(format).getName();
                Collection<SchemaSource> customTestSource = Arrays.asList(
                        SourceFactory.createSchemaSourceFromText("http//rdfunit.aksw.org/custom#", text, oficialFormat)
                );

                configuration.setSchemata(customTestSource);
                break;

            default:
                schemaSelectorView.setMessage("Unknown error, try again", true);
                return false;
        }

        schemaSelectorView.setMessage("Constraints loaded successfully: (" + getSchemaDesc(configuration.getAllSchemata()) + ")", false);
        return true;

    }

    private String getSchemaDesc(Collection<SchemaSource> schemaSources) {
        StringBuilder builder = new StringBuilder();
        boolean firstTime = true;
        for (SchemaSource src : schemaSources) {
            if (!firstTime) {
                builder.append(", ");
            }
            else {
                firstTime = false;
            }

            builder.append(src.getPrefix());
        }
        return builder.toString();
    }
}
