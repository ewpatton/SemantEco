package edu.rpi.tw.escience.semanteco;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet(name="SemantEco",
			urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log",
					"/provenance"},
			description="SemantEco Portal",
			displayName="SemantEco")
public class SemantEco extends BaseSemantEcoServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5847090729887396066L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

}
