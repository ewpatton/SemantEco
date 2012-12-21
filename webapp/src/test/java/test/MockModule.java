package test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class MockModule implements Module {

	@Override
	public void visit(Model model, Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OntModel model, Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Query query, Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SemantEcoUI ui, Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "MockModule";
	}

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getExtraVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setModuleConfiguration(ModuleConfiguration config) {
		// TODO Auto-generated method stub

	}

}
