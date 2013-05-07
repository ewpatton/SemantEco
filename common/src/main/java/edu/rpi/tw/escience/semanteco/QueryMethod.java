package edu.rpi.tw.escience.semanteco;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The QueryMethod annotation is used to mark methods on
 * Modules that are exposed as RESTful calls by the core
 * SemantEco framework.
 * 
 * @author ewpatton
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryMethod {
	/**
	 * Enum to represent different HTTP methods currently supported by
	 * SemantEco's Java servlet.
	 * @author ewpatton
	 *
	 */
	public enum HTTP {
		GET,
		POST
	}
	/**
	 * The HTTP method to use when the query method is called. Defaults to
	 * HTTP GET.
	 * @return
	 */
	HTTP method() default HTTP.GET;
}
