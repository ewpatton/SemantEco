package edu.rpi.tw.escience.waterquality.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;

public class SemantAquaUIImpl implements SemantAquaUI {
	
	private Set<Resource> scripts = new HashSet<Resource>();
	private Set<Resource> stylesheets = new HashSet<Resource>();
	private List<Resource> facets = new LinkedList<Resource>();

	@Override
	public void addScript(Resource script) {
		scripts.add(script);
	}

	@Override
	public void addStylesheet(Resource stylesheet) {
		stylesheets.add(stylesheet);
	}

	@Override
	public void addFacet(Resource facet) {
		facets.add(facet);
	}

	@Override
	public List<Resource> getFacets() {
		return Collections.unmodifiableList(facets);
	}

	@Override
	public List<Resource> getScripts() {
		return new LinkedList<Resource>(scripts);
	}

	@Override
	public List<Resource> getStylesheets() {
		return new LinkedList<Resource>(stylesheets);
	}

}
