package edu.rpi.tw.escience.waterquality.zipcode;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.QueryMethod;
import edu.rpi.tw.escience.waterquality.Request;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;
import edu.rpi.tw.escience.waterquality.zipcode.ZipCodeLookup.ServerFailedToRespondException;

public class ZipCodeModule implements Module {

	private ModuleConfiguration config = null;
	
	@Override
	public void visit(Model model, Request request) {

	}

	@Override
	public void visit(OntModel model, Request request) {

	}

	@Override
	public void visit(Query query, Request request) {

	}

	@Override
	public void visit(SemantAquaUI ui, Request request) {

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
		this.config = config;
	}
	
	@QueryMethod
	public String decodeZipCode(Request request) {
		String result = null;
		final Logger log = request.getLogger();
		try {
			log.debug("Decoding zip code");
			ZipCodeLookup zcl = ZipCodeLookup.execute(request.getParam("zip")[0],
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
