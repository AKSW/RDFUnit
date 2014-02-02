package org.aksw.databugger;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.aksw.databugger.io.TripleFileWriter;
import org.aksw.databugger.sources.DatasetSource;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * User: Dimitris Kontokostas
 * Description
 * Created: 1/22/14 1:59 PM
 */
public class test {

    public static void main(String[] args) throws Exception {
        File filename = new File("test.resources.list");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filename), "UTF8"));

        String resource;

        Model model = ModelFactory.createDefaultModel();
        DatasetSource dataset = new DatasetSource("tmp","tmp","http://localhost:8890/sparql", "http://live.dbpedia.org",null);
        QueryExecutionFactory qef = dataset.getExecutionFactory();

        while ((resource = in.readLine()) != null) {
            try{
                String query =  "construct {\n" +
                        "<" + resource + "> ?p ?o}\n" +
                        "where {<" + resource + "> ?p ?o}" ;
                System.out.println("Querying for resource: " + resource);
                QueryExecution qe = qef.createQueryExecution(query);
                Model results = qe.execConstruct();
                model.add(results);
                //Thread.sleep(1000);
            } catch (Exception e) {

            }

        }

        TripleFileWriter fw = new TripleFileWriter("results.ttl");
        fw.write(model);
    }
}
