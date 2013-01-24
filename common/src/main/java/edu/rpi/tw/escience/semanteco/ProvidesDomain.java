package edu.rpi.tw.escience.semanteco;

import java.util.List;

/**
 * Modules that act as data providers MUST implement
 * the ProvidesDomain interface and provide descriptions
 * of data domains via the {@link #getDomains(Request)} method.
 * @author ewpatton
 *
 */
public interface ProvidesDomain {
	/**
	 * Get the domains for a domain provider.
	 * @param request A dummy request object that
	 * can be used for initiating external SPARQL
	 * requests using the QueryExecutor
	 * @return A list of domains declared by the
	 * underlying implementation
	 */
	List<Domain> getDomains(Request request);
}
