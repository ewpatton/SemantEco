package edu.rpi.tw.escience.semanteco.ui;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;

/**
 * SemantEcoUIImpl provides a container for UI resources and
 * is used by various taglib tags to provide the HTML interface
 * for the SemantEco portal.
 * 
 * @author ewpatton
 *
 */
public class SemantEcoUIImpl implements SemantEcoUI {
	
	private Set<Resource> scripts = new HashSet<Resource>();
	private Set<Resource> stylesheets = new HashSet<Resource>();
	private List<Resource> facets = new LinkedList<Resource>();

	/**
	 * Default constructor
	 */
	public SemantEcoUIImpl() {
		
	}
	
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
