package edu.rpi.tw.escience.waterquality.taglib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.waterquality.Resource;
import edu.rpi.tw.escience.waterquality.SemantAquaUI;
import edu.rpi.tw.escience.waterquality.res.JspResource;
import edu.rpi.tw.escience.waterquality.res.OwnedResource;
import edu.rpi.tw.escience.waterquality.ui.SemantAquaUIFactory;
import edu.rpi.tw.escience.waterquality.util.NameUtils;

/**
 * The FacetTag class provides a mechanism for the SemantAqua JSP to present
 * facets from the various modules to the end user.
 * 
 * @author ewpatton
 *
 */
public class FacetTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7656285413071676382L;
	private Logger log = Logger.getLogger(FacetTag.class);
	
	/**
	 * Default constructor used by the JSP processor to instantiate this class
	 */
	public FacetTag() {
		
	}

	@Override
	public int doStartTag() throws JspException {
		log.debug("Generating facets");
		final JspWriter out = pageContext.getOut();
		final SemantAquaUI ui = SemantAquaUIFactory.getInstance().getUI();
		final List<Resource> facets = ui.getFacets();
		try {
			log.debug("Facets: "+facets.size());
			for(Resource i : facets) {
				final String name = ((OwnedResource)i).getOwner().getName();
				log.debug("Outputing facet for '"+name+"'");
				out.write("<table id=\""+NameUtils.cleanName(i.getOwner().getClass().getSimpleName())+"\" border=\"1\">");
				out.write("<tr><th>"+name+"</th></tr>");
				out.write("<tr><td>");
				if(i.isJspResource()) {
					try {
						ModuleJspEvaluator eval = new ModuleJspEvaluator((HttpServletResponse)pageContext.getResponse());
						JspResource jsp = (JspResource)i;
						jsp.dispatch(pageContext.getServletContext(), (HttpServletRequest)pageContext.getRequest(), (HttpServletResponse)eval);
						out.write(eval.toString());
					}
					catch(Exception e) {
						log.warn("Unable to render facet due to exception", e);
					}
				}
				else {
					out.write(i.toString());
				}
				out.write("</td></tr></table>");
			}
		}
		catch (IOException e) {
			log.warn("Unable to send response to client", e);
		}
		return SKIP_BODY;
	}

	private static class ModuleJspEvaluator extends HttpServletResponseWrapper {

		private final WrapperOutputStream output = new WrapperOutputStream();
		private final Logger log = Logger.getLogger(ModuleJspEvaluator.class);
		private final PrintWriter writer = new PrintWriter(output, true);
		
		/**
		 * Provides a response wrapper that can be passed to a JSP processor
		 * to capture the output of the processor before sending it to the
		 * actual response stream.
		 * @param response
		 */
		public ModuleJspEvaluator(HttpServletResponse response) {
			super(response);
		}
		
		@Override
		public PrintWriter getWriter() {
			log.trace("getWriter");
			return writer;
		}
		
		@Override
		public ServletOutputStream getOutputStream() {
			log.trace("getOutputStream");
			return output;
		}
		
		@Override
		public String toString() {
			log.trace("toString");
			writer.close();
			return output.toString();
		}
		
	}
	
	private static class WrapperOutputStream extends ServletOutputStream {
		private ByteArrayOutputStream data = new ByteArrayOutputStream();
		private Logger log = Logger.getLogger(WrapperOutputStream.class);

		@Override
		public void write(int arg0) throws IOException {
			log.trace("write");
			data.write(arg0);
		}
		
		@Override
		public String toString() {
			log.trace("toString");
			try {
				return data.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.warn("Data not in UTF-8 format", e);
			}
			return "";
		}
	}
	
}
