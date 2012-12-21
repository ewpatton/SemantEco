package edu.rpi.tw.escience.semanteco.test;

import java.util.List;

import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;

/**
 * MockUI provides a base implementation of SemantEcoUI
 * that can be used by unit tests to provide functionality
 * for testing module interaction with the UI. All methods
 * throw UnsupportedOperationException by default unless
 * explicitly overridden by a subclass.
 * @author ewpatton
 *
 */
public class MockUI implements SemantEcoUI {

	@Override
	public void addScript(Resource script) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addStylesheet(Resource stylesheet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addFacet(Resource facet) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Resource> getFacets() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Resource> getScripts() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Resource> getStylesheets() {
		throw new UnsupportedOperationException();
	}

}
