package edu.rpi.tw.escience.semanteco.test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleConfiguration;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class MockModule implements Module {

  @Override
  public void visit(Model model, Request request, Domain domain) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visit(OntModel model, Request request, Domain domain) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visit(Query query, Request request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void visit(SemantEcoUI ui, Request request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMajorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getMinorVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getExtraVersion() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setModuleConfiguration(ModuleConfiguration config) {
    throw new UnsupportedOperationException();
  }

}
