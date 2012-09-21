package edu.rpi.tw.escience.waterquality.util;

public final class NameUtils {
	
	private NameUtils() {
		
	}

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
