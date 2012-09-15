package test;

import java.util.Map;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.ModuleConfiguration;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.query.Query;

public class MockModule implements Module {

	@Override
	public void visit(Model model, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(OntModel model, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(Query query, Map<String, String> params) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(SemantAquaUI ui, Map<String, String> params) {
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
