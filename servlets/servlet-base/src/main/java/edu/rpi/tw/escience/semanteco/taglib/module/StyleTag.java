package edu.rpi.tw.escience.semanteco.taglib.module;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

import edu.rpi.tw.escience.semanteco.ui.SemantEcoUIFactory;
import edu.rpi.tw.escience.semanteco.Resource;
import edu.rpi.tw.escience.semanteco.SemantEcoUI;

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
	
	@Override
	public int doStartTag() throws JspException {
		log.debug("Generating <link> entries");
		final JspWriter out = pageContext.getOut();
		final SemantEcoUI ui = SemantEcoUIFactory.getInstance().getUI();
		final List<Resource> styles = ui.getStylesheets();
		try {
			log.debug("Writing module stylsheets");
			log.debug("Stylesheets: "+styles.size());
			for(Resource i : styles) {
				out.write("<link rel=\"stylesheet\" href=\"");
				out.write(i.getPath());
				out.write("\" type=\"text/css\"></script>\r\n");
			}
		}
		catch(IOException e) {
			log.error("Unable to generate list of scripts", e);
		}
		return SKIP_BODY;
	}
}
