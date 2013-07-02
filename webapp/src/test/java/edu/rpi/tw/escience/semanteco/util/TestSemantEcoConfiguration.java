package edu.rpi.tw.escience.semanteco.util;

public class TestSemantEcoConfiguration extends SemantEcoConfiguration {
	private static final long serialVersionUID = -4432336232102848371L;

	public TestSemantEcoConfiguration() {
		super(null);
		SemantEcoConfiguration.install(this);
	}

	@Override
	public void setDebug(boolean debug) {
		super.setDebug(debug);
	}
}
