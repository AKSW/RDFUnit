package org.aksw.rdfunit.io;

import com.hp.hpl.jena.rdf.model.Model;
import org.aksw.rdfunit.exceptions.TripleReaderException;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 11/14/13 8:48 AM
 */
public class RDFDereferenceReader extends DataReader {

    private final String uri;

    public RDFDereferenceReader(String uri) {
        this.uri = uri;
    }

    @Override
    public void read(Model model) throws TripleReaderException {
        try {
            //TODO check for relative file names and convert to absolute paths
            model.read(uri);
        //if not found this error is thrown:
        //}catch(com.hp.hpl.jena.shared.NotFoundException NFE){
		
		//org.apache.jena.riot.RiotException -> if wrong format, i.e. turtle instead of RDF/XML
        
        } catch (Exception e) {
            throw new TripleReaderException(e);
        }
    }
}
