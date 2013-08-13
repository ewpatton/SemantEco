package edu.rpi.tw.escience;

import edu.rpi.tw.escience.semanteco.BaseSemantEcoServlet;

import javax.servlet.annotation.WebServlet;

/**
 * App Description
 */
@WebServlet(name="Annotator",
            urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log",
                    "/provenance"},
            description="",
            displayName="Annotator")
public class Annotator extends BaseSemantEcoServlet {

  /**
   * 
   */
  private static final long serialVersionUID = -6696699562660334893L;

}
