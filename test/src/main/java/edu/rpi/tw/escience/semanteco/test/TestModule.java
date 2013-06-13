package edu.rpi.tw.escience.semanteco.test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class TestModule extends MockModule {

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
    return "TestModule";
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
  }

}
