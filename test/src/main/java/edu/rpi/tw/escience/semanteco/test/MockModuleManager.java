package edu.rpi.tw.escience.semanteco.test;

import java.net.URI;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.ModuleManager;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class MockModuleManager implements ModuleManager {

  @Override
  public Module getModuleByName(String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void buildUserInterface(SemantEcoUI ui, Request request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void buildOntologyModel(OntModel model, Request request, Domain domain) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void buildDataModel(Model model, Request request, Domain domain) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String updateFragmentForFacet(Module module, Request request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void augmentQuery(Query query, Request request) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void augmentQuery(Query query, Request request, Module originator) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Module> listModules() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getLastModified() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Domain getDomain(URI uri) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void registerDomain(Domain domain) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Domain> listDomains() {
    throw new UnsupportedOperationException();
  }

}
