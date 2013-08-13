package edu.rpi.tw.escience.semanteco.taglib.core;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

/**
 * StyleTag is used to export any CSS resources provided by modules
 * as &lt;link&gt; elements in the final HTML presented by the portal.
 * 
 * @author ewpatton
 *
 */
public class StyleTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8985229537765004099L;
	private final transient Logger log = Logger.getLogger(StyleTag.class);

	public StyleTag() {
		
	}

	protected void writeStylesheet(JspWriter out, String url)
			throws IOException {
		out.write("<link rel=\"stylesheet\" href=\"");
		out.write(url);
		out.write("\" type=\"text/css\"></script>\r\n");
	}

	@Override
	public int doStartTag() throws JspException {
		log.debug("Generating <link> entries");
		final JspWriter out = pageContext.getOut();
		try {
			writeStylesheet(out, "css/reset.css");
			writeStylesheet(out, "css/start/jquery-ui.min.css");
			writeStylesheet(out, "css/start/jquery.ui.theme.css");
			writeStylesheet(out, "css/main.css");
			writeStylesheet(out, "js/jqplot/jquery.jqplot.min.css");
			writeStylesheet(out, "js/jstree/themes/default/style.css");
		}
		catch(IOException e) {
			log.error("Unable to generate list of scripts", e);
		}
		return SKIP_BODY;
	}
}
