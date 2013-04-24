package edu.rpi.tw.escience.semanteco.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {

	public static final ResourceBundle bundle
		= ResourceBundle.getBundle("Messages", Locale.getDefault());

	public static final String MODULE_INVALID
		= bundle.getString("module.invalid");
	public static final String METHOD_ONLYGET
		= bundle.getString("method.onlyget");
	public static final String AUTOGEN
		= bundle.getString("autogen");
	public static final String MODE_NOTVALID
		= bundle.getString("mode.notvalid");

}
