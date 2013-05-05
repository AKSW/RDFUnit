package transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;  

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
          	List<String> subjects = new ArrayList();
          	List<String> objects = new ArrayList();

      			String disjoint = line.substring(line.lastIndexOf("=")+1,line.length()).trim();
      			String object = "http://dbpedia.org/ontology/" + disjoint;
      			subjects.add(aClass.getURI());
      			objects.add(object);
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
      			//now write all triples
      			for (String subj:subjects) {
      				for (String obj:objects) {
      					triples.append("<"+subj+">"+" <http://www.w3.org/2002/07/owl#disjointWith> "+"<"+obj+ "> .\n");
      				}
      			}
  	    		FileUtils.writeStringToFile(file, triples.toString(), true);
      		}
      	}
      }
    }    
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
