package edu.rpi.tw.escience.semanteco;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The HierarchicalMethod annotation is used to mark
 * a method on a Module that is exposed as a RESTful
 * interface and will automatically generate a hierarchical
 * facet for the Module, which will call the method
 * when it needs data.
 * 
 * Methods annotated with HiearchicalMethod should have
 * the following signature:
 * 
 * \@HierarchicalMethod
 * public Collection&lt;HierarchyEntry&gt; method_name(Request request, HierarchyVerb verb)
 * 
 * @author ewpatton
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface HierarchicalMethod {

}
