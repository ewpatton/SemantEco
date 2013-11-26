package edu.rpi.tw.escience.semanteco;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.rpi.tw.escience.semanteco.query.Query;
import edu.rpi.tw.escience.semanteco.regulation.ThresholdQueryBuilder;
import edu.rpi.tw.escience.semanteco.test.TestModuleConfiguration;
import junit.framework.TestCase;

public class ThresholdQueryBuilderTest extends TestCase {

	@Test
	public void testQuery() throws IOException {
		int results = 0;
		TestModuleConfiguration config = new TestModuleConfiguration();
		ThresholdQueryBuilder builder = new ThresholdQueryBuilder();
		Query query = builder.build(config);
		Model model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		InputStream fs = null;
		try {
			fs = new FileInputStream(new File("src/test/resources/threshold-extraction-test.ttl"));
			model.read( fs, "http://escience.rpi.edu/ontology/semanteco/4/0/test.ttl" );
			QueryExecution qe = QueryExecutionFactory.create(query.toString(), model);
			ResultSet rs = qe.execSelect();
			while(rs.hasNext()) {
				System.out.println(rs.next());
				results++;
			}
		} finally {
			if(fs != null) {
				fs.close();
			}
		}
		assertEquals(1, results);
	}

}
