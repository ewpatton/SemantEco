package edu.rpi.tw.escience.semanteco.test;

import java.net.URI;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import edu.rpi.tw.escience.semanteco.Domain;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.Request;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;
import edu.rpi.tw.escience.semanteco.query.Query;

public class TestModuleManager extends MockModuleManager {

  @Override
  public Module getModuleByName(String name) {
    return null;
  }

  @Override
  public void buildUserInterface(SemantEcoUI ui, Request request) {

  }

  @Override
  public void buildOntologyModel(OntModel model, Request request, Domain domain) {

  }

  @Override
  public void buildDataModel(Model model, Request request, Domain domain) {

  }

  @Override
  public String updateFragmentForFacet(Module module, Request request) {
    return null;
  }

  @Override
  public void augmentQuery(Query query, Request request) {

  }

  @Override
  public void augmentQuery(Query query, Request request, Module originator) {

  }

  @Override
  public List<Module> listModules() {
    return null;
  }

  @Override
  public long getLastModified() {
    return 0;
  }

  @Override
  public Domain getDomain(URI uri) {
    return null;
  }

  @Override
  public void registerDomain(Domain domain) {

  }

  @Override
  public List<Domain> listDomains() {
    return null;
  }

}
