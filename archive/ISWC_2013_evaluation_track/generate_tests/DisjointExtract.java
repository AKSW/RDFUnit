package transform;

import org.apache.commons.io.FileUtils;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DisjointExtract {

	public static String dbpediaOwl = "http://mappings.dbpedia.org/server/ontology/dbpedia.owl" ;
  public static File file = new File("disjoint_classes.nt");	
  
  public static void main (String args[]) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
  	
  	// get classes from dbpedia ontology
    OntModel onto = ModelFactory.createOntologyModel();      
    InputStream in = FileManager.get().open( dbpediaOwl );
    if (in == null) {
        throw new IllegalArgumentException(
                                     "File: " + dbpediaOwl + " not found");
    }
    onto.read(in, null);
    
    StringBuilder triples = new StringBuilder();
  	Set<String> subjects = new HashSet();
  	Set<String> objects = new HashSet();
  	Set<String> tripleSet = new HashSet();

    ExtendedIterator classes = onto.listClasses();
    while (classes.hasNext()) {
    	
      OntClass aClass = (OntClass) classes.next();
      String label = aClass.getLocalName().toString();
      
    	// get value from xml and export from mappings api
      String xmlFile = "http://mappings.dbpedia.org/index.php/Special:Export/OntologyClass:"+label;
      String text = xpath(xmlFile,"mediawiki/page/revision/text");
      // only some are worth processing
      if (text.contains("disjoint")) {

      	String [] lines = text.split("\n");
      	for (String line : lines) {
      		if (line.contains("owl:disjointWith")){

      			String disjoint = line.substring(line.lastIndexOf("=")+1,line.length()).trim();
      			String object = "http://dbpedia.org/ontology/" + disjoint;
      			subjects.add(aClass.getURI().toString());
      			objects.add(object.toString());
      			// get all sub-classes for found disjoint subjects
      			if (aClass.hasSubClass()){      
      						
	      			ExtendedIterator subclasses = aClass.listSubClasses();
	      			while (subclasses.hasNext()) {
	      				OntClass sClass = (OntClass) subclasses.next();
	      				subjects.add(sClass.getURI());
	      			}
      			}
      			// get all sub-classes for found disjoint objects
      			OntClass subjectClass = onto.getOntClass(object);
      			if (subjectClass != null && subjectClass.hasSubClass()){
	      			ExtendedIterator sub2classes = subjectClass.listSubClasses();
	      			while (sub2classes.hasNext()) {
	      				OntClass sClass = (OntClass) sub2classes.next();
	      				objects.add(sClass.getURI());
	      			}
      			} 
      		}
      		// finally write all triples
          if (!subjects.isEmpty()&& !objects.isEmpty()) {
      			Iterator itSubj = subjects.iterator();
      			while (itSubj.hasNext()) {
      				String strSubj = (String) itSubj.next(); 
      				Iterator itObj = objects.iterator();
      				while (itObj.hasNext()) {
        				String strObj = (String) itObj.next(); 
      	  			tripleSet.add("<"+strSubj +">"+" <http://www.w3.org/2002/07/owl#disjointWith> "+"<"+strObj+ "> .");
      				}
      			}
      			// cleanup sets for next class 
      			objects.clear();
      			subjects.clear();
          }
      	}
      }
    }
    // finally write triples to file
		Iterator itTriple = tripleSet.iterator();
		while (itTriple.hasNext()) {
			String strTriple = (String) itTriple.next(); 
			triples.append(strTriple +"\n");
		}
		FileUtils.writeStringToFile(file, triples.toString(), true);
  }
	
  // get value from xml file
  public static String xpath (String xmlFile, String path) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
  	
    InputStream is = FileManager.get().open(xmlFile);
    DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = xmlFactory.newDocumentBuilder();
    Document xmlDoc = docBuilder.parse(is);
    XPathFactory xpathFact = XPathFactory.newInstance();
    XPath xpath = xpathFact.newXPath();
    
    String text = (String) xpath.evaluate(path , xmlDoc, XPathConstants.STRING);  	
  	
  	return text;
  }	
}
