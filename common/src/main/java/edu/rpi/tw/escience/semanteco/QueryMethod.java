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
	public enum HTTP {
		GET,
		POST
	}
	HTTP method() default HTTP.GET;
}
