package org.aksw.rdfunit.model.helper;

import com.google.common.collect.ImmutableList;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.Collection;
import java.util.List;

/**
 * Utility functions for rdf:Lists
 *
 * @author Dimitris Kontokostas
 * @since 29/6/2016 10:44 μμ
 */
public final class RdfListUtils {
    private RdfListUtils() {}


    public static boolean isList(RDFNode node) {
        return node.isAnon() && node.canAs(RDFList.class);
    }

    /**
     * Tries to return the items of a list or empty if th elist is empty or not an rdf:List
     */
    public static Collection<RDFNode> getListItemsOrEmpty(RDFNode node) {
        ImmutableList.Builder<RDFNode> items = ImmutableList.builder();

        if (isList(node)) {
            RDFList rdfList = node.as(RDFList.class);
            rdfList.iterator().forEachRemaining(items::add);
        }

        return items.build();
    }

    /**
     * Tries to get the items of an rdf:List and throws an {@link IllegalArgumentException} if it is not a list
     */
    public static Collection<RDFNode> getListItemsOrFail(RDFNode node) {

        if (!isList(node)) {
            throw new IllegalArgumentException("Resource not an rdf:List");
        }

        return getListItemsOrEmpty(node);
    }

    public static Resource createListOfNodes(List<RDFNode> nodes) {
        return ModelFactory
                .createDefaultModel()
                .createList(nodes.iterator());
    }
}
