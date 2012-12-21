package edu.rpi.tw.escience.waterquality.test;

import java.util.List;

import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;

/**
 * MockUI provides a base implementation of SemantAquaUI
 * that can be used by unit tests to provide functionality
 * for testing module interaction with the UI. All methods
 * throw UnsupportedOperationException by default unless
 * explicitly overridden by a subclass.
 * @author ewpatton
 *
 */
public class MockUI implements SemantAquaUI {

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
