package edu.rpi.tw.escience.waterquality;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The QueryMethod annotation is used to mark methods on
 * Modules that are exposed as RESTful calls by the core
 * SemantAqua framework.
 * 
 * @author ewpatton
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryMethod {

}
