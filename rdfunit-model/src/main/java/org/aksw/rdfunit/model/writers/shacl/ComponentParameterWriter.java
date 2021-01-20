package org.aksw.rdfunit.model.writers.shacl;

import org.aksw.rdfunit.model.interfaces.shacl.ComponentParameter;
import org.aksw.rdfunit.model.writers.ElementWriter;
import org.aksw.rdfunit.vocabulary.SHACL;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.RDF;

/**
 * @author Dimitris Kontokostas
 * @since 6/17/15 5:57 PM
 */
public final class ComponentParameterWriter implements ElementWriter {

  private final ComponentParameter componentParameter;

  private ComponentParameterWriter(ComponentParameter componentParameter) {
    this.componentParameter = componentParameter;
  }

  public static ComponentParameterWriter create(ComponentParameter componentParameter) {
    return new ComponentParameterWriter(componentParameter);
  }


  @Override
  public Resource write(Model model) {
    Resource resource = ElementWriter.copyElementResourceInModel(componentParameter, model);

    // rdf:type sh:ComponentParameter
    resource.addProperty(RDF.type, SHACL.ParameterCls);

    // sh:path sh:argX
    resource.addProperty(SHACL.path, componentParameter.getPredicate());

    //Optional
    if (componentParameter.isOptional()) {
      resource.addProperty(SHACL.optional,
          ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean));
    }
    return resource;
  }
}
