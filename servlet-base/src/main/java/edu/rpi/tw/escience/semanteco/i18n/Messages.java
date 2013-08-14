package edu.rpi.tw.escience.semanteco.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The Messages class provides internationalization support for responses to
 * server-side requests within SemantEco.
 * @author ewpatton
 *
 */
public final class Messages {

	private Messages() {
	}

	public static final ResourceBundle BUNDLE
		= ResourceBundle.getBundle("Messages", Locale.getDefault());

	public static final String MODULE_INVALID
		= BUNDLE.getString("module.invalid");
	public static final String METHOD_ONLYGET
		= BUNDLE.getString("method.onlyget");
	public static final String AUTOGEN
		= BUNDLE.getString("autogen");
	public static final String MODE_NOTVALID
		= BUNDLE.getString("mode.notvalid");
	public static final String PROVENANCE_CONNECTION_LOST
		= BUNDLE.getString("provenance.connection.lost");
	public static final String PROVENANCE_CONNECTION_NOCLOSE
		= BUNDLE.getString("provenance.connection.noclose");

}
