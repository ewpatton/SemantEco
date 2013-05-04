package edu.rpi.tw.escience.semanteco.zipcode;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.zipcode.ZipCodeLookup.ServerFailedToRespondException;
import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

/**
 * The ZipCodeModule is responsible for finding the state and county
 * associated with a specific ZIP code. It returns a lat,long pair
 * representing the center of the ZIP code so that the map interface
 * can focus on the relevant area.
 *  
 * @author ewpatton
 *
 */
public class ZipCodeModule implements Module {
	
	@Override
	public void visit(Model model, Request request, Domain domain) {

	}

	@Override
	public void visit(OntModel model, Request request, Domain domain) {

	}

	@Override
	public void visit(Query query, Request request) {

	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {

	}

	@Override
	public String getName() {
		return "Zip Code";
	}

	@Override
	public int getMajorVersion() {
		return 1;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public String getExtraVersion() {
		return null;
	}

	@Override
	public void setModuleConfiguration(ModuleConfiguration config) {
		// not needed
	}

	/**
	 * Decodes the ZIP code sent by the client and sends back the
	 * state, county, lat, and long of the ZIP code
	 * @param request
	 * @return
	 */
	@QueryMethod
	public String decodeZipCode(Request request) {
		String result = null;
		final Logger log = request.getLogger();
		try {
			log.debug("Decoding zip code");
			ZipCodeLookup zcl = ZipCodeLookup.execute((String)request.getParam("zip"),
					log);
			result = zcl.toString();
		}
		catch(ServerFailedToRespondException e) {
			result = "{\"error\":\"Geonames server not responding. Please try again later.\"}";
		}
		catch(Exception e) {
			result = "{\"error\":\"Unknown zip code\"}";
		}
		return result;
	}

}
