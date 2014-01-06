package edu.rpi.tw.escience.semanteco.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import edu.rpi.tw.escience.semanteco.HierarchicalMethod;
import edu.rpi.tw.escience.semanteco.Module;
import edu.rpi.tw.escience.semanteco.QueryMethod;
import edu.rpi.tw.escience.semanteco.QueryMethod.HTTP;

/**
 * JavaScriptGenerator provides some basic utilities for creating
 * JavaScript used by the client to interact with the server.
 * @author ewpatton
 *
 */
public final class JavaScriptGenerator {

	private JavaScriptGenerator() {
	
	}

	/**
	 * Generates JavaScript to provide an AJAX interface for talking
	 * to the specified Module. Any method annotated with \@QueryMethod
	 * will be provided an implementation on the client.
	 * @param mod
	 * @return
	 */
	public static String ajaxForModule(Module mod) {
		final Class<?> cls = mod.getClass();
		final String lineReturn = SemantEcoConfiguration.get().isDebug() ? "\n" : "";
		final StringBuilder result = new StringBuilder("var "+cls.getSimpleName()+" = {");
		result.append(lineReturn);
		int methodCount = 0;
		
		final Method[] methods = cls.getMethods();
		final Collection<Method> hms = new ArrayList<Method>();
		boolean first = true;
		for(int i=0;i<methods.length;i++) {
			Method m = methods[i];
			if(!m.isAnnotationPresent(QueryMethod.class) &&
					!m.isAnnotationPresent(HierarchicalMethod.class)) {
				continue;
			}
			methodCount++;
			if(first) {
				first = false;
			} else {
				result.append(",");
				result.append(lineReturn);
			}
			if(m.isAnnotationPresent(QueryMethod.class)) {
				result.append(processQueryMethod(cls, m));
			} else {
				hms.add(m);
				result.append(processHierarchicalMethod(cls, m));
			}
		}
		result.append(lineReturn);
		result.append("};");
		result.append("$(window).bind(\"initialize\",function(){");
		for(Method m : hms) {
			result.append("SemantEcoUI.HierarchicalFacet.create(\"#");
			result.append(NameUtils.cleanName(cls.getSimpleName()));
			result.append(" div.facet div.hierarchy\", ");
			result.append(cls.getSimpleName());
			result.append(", \"");
			result.append(m.getName());
			result.append("\", \"");
			final HierarchicalMethod hm = m.getAnnotation(HierarchicalMethod.class);
			result.append(hm.parameter());
			result.append("\");");
		}
		result.append("});");
		
		if(methodCount == 0) {
			return "";
		} else {
			return result.toString();
		}
	}

	private static String processMethod(final Class<?> cls, final Method m,
			final HTTP verb, final boolean hasMode) {
		final StringBuilder result = new StringBuilder();

		// extra line endings for debugging
		final boolean debug = SemantEcoConfiguration.get().isDebug();
		final String ending = (debug ? "\n  " : "");
		final String spacing = (debug ? "  " : "");
		result.append("\"");
		result.append(m.getName());
		result.append("\": ");

		// start function definition
		result.append("function(");
		if(hasMode) {
			result.append("mode,");
		}
		result.append("args,success,error){");
		result.append(ending);
		result.append(spacing);

		// extend arguments
		result.append("var a=$.extend(");
		if(hasMode) {
			result.append("{'mode':mode},");
		}
		result.append("SemantEcoUI.getState(),args);");
		result.append(ending);
		result.append(spacing);

		// ajax request
		result.append("var b=$.ajax(SemantEco.restBaseUrl+\"");
		result.append(cls.getSimpleName());
		result.append("/");
		result.append(m.getName());
		result.append("\",{\"data\":SemantEco.prepareArgs(a)");
		if(verb == HTTP.POST) {
			result.append(",\"type\":\"POST\"");
		}
		result.append("});");
		result.append(ending);
		result.append(spacing);

		// configure success handler
		result.append("if(success)");
		result.append(ending);
		result.append(spacing);
		result.append(spacing);
		result.append("b.done(success);");
		result.append(ending);
		result.append(spacing);

		// configure error handler
		result.append("if(error)");
		result.append(ending);
		result.append(spacing);
		result.append(spacing);
		result.append("b.fail(error);");
		result.append(ending);
		result.append(spacing);

		// end function definition
		result.append("}");
		return result.toString();
	}

	private static String processQueryMethod(final Class<?> cls, final Method m) {
		QueryMethod attrs = m.getAnnotation(QueryMethod.class);
		return processMethod(cls, m, attrs.method(), false);
	}

	private static String processHierarchicalMethod(final Class<?> cls, final Method m) {
		return processMethod(cls, m, HTTP.GET, true);
	}

}
