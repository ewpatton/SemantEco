package edu.rpi.tw.escience.waterquality.util;

/**
 * NameUtils can be used by modules providing JavaScript to take a string
 * and turn it into one that is compatible with variable names in the
 * JavaScript language.
 * 
 * @author ewpatton
 *
 */
public final class NameUtils {
	
	private NameUtils() {
		
	}

	/**
	 * Cleans up the provided name so that it can be used in
	 * HTML styles and as a name in HTML forms.
	 * @param name
	 * @return
	 */
	public static String cleanName(final String name) {
		final StringBuilder sb = new StringBuilder();
		for(int i=0;i<name.length();i++) {
			char c = name.charAt(i);
			if(Character.isUpperCase(c)) {
				if(i>0) {
					sb.append("-");
				}
				sb.append(Character.toLowerCase(c));
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
