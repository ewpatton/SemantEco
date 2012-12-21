package edu.rpi.tw.escience.semanteco;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CacheControlFilter disables the caching
 * mechanisms in the servlet container for
 * use in debugging resources during development.
 * @author ewpatton
 *
 */
@WebFilter(
		filterName = "CacheControlFilter",
		urlPatterns = {"/*"}
		)
public class CacheControlFilter implements Filter {

	private final Logger log = Logger.getLogger(CacheControlFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		log.debug("doFilter");
		HttpServletResponse resp = (HttpServletResponse)response;
		resp.setHeader("Expires", "Wed, 31 Dec 1969 23:59:59 GMT");
		resp.setHeader("Last-Modified", new Date().toString());
		resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
        resp.setHeader("Pragma", "no-cache");

        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
