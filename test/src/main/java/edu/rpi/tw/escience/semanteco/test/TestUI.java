package edu.rpi.tw.escience.semanteco.test;

import java.util.ArrayList;
import java.util.List;

import edu.rpi.tw.escience.semanteco.Resource;

public class TestUI extends MockUI {

  @Override
  public void addScript(Resource script) {
  }

  @Override
  public void addStylesheet(Resource stylesheet) {
  }

  @Override
  public void addFacet(Resource facet) {
  }

  @Override
  public List<Resource> getFacets() {
    return new ArrayList<Resource>();
  }

  @Override
  public List<Resource> getScripts() {
    return new ArrayList<Resource>();
  }

  @Override
  public List<Resource> getStylesheets() {
    return new ArrayList<Resource>();
  }

}
