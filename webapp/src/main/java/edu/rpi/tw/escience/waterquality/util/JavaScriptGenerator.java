package edu.rpi.tw.escience.waterquality.util;

import java.lang.reflect.Method;

import edu.rpi.tw.escience.waterquality.Module;
import edu.rpi.tw.escience.waterquality.QueryMethod;

public final class JavaScriptGenerator {

	private JavaScriptGenerator() {
	
	}
	
	public static String ajaxForModule(Module mod) {
		final Class<?> cls = mod.getClass();
		String result = "var "+cls.getSimpleName()+" = {";
		int methodCount = 0;
		
		final Method[] methods = cls.getMethods();
		boolean first = true;
		for(int i=0;i<methods.length;i++) {
			Method m = methods[i];
			if(m.isAnnotationPresent(QueryMethod.class)) {
				methodCount++;
				if(first) {
					first = false;
				}
				else {
					result += ",";
				}
				result += processMethod(cls, m);
			}
		}
		result += "};";
		
		if(methodCount == 0) {
			return "";
		}
		else {
			return result;
		}
	}
	
	private static String processMethod(final Class<?> cls, final Method m) {
		String result = "\""+m.getName()+"\": ";
		result += "function(args,success,error){" + 
				"var a=$.extend({},SemantAquaUI.getState(),args);" +
				"var b=$.ajax(SemantAqua.restBaseUrl+\""+cls.getSimpleName()+"/"+m.getName()+"\");" +
				"if(success)b.done(success);" +
				"if(error)b.fail(error);" +
				"}";
		return result;
	}
	
	public static String cleanName(String name) {
		return name.replaceAll(" ", "-");
	}
	
}
