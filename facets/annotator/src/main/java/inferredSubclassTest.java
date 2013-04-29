import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import edu.rpi.tw.escience.semanteco.ModuleConfiguration;


public class inferredSubclassTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ModuleConfiguration config = null;
		Model model = null;
		model = ModelFactory.createOntologyModel(PelletReasonerFactory.THE_SPEC);
		//FileManager.get().readModel(model, config.getResource("owl-files/oboe-biology-sans-imports.owl").toString()) ;
		model.read("/Users/apseyed/Documents/rpi/dataOne-march-21.owl");
		//String 
		//String constructStringOrganisms = "construct {?a skos:broader ?b. ?a skos:prefLabel ?c. tema:N395 skos:prefLabel ?d. } where { graph <http://dataone.tw.rpi.edu> {?a skos:broader ?b. ?a skos:prefLabel ?c. tema:N395 skos:prefLabel ?d.}}";





	}
	
	

}
