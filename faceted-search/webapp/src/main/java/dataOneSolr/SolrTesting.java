package dataOneSolr;

import edu.rpi.tw.escience.semanteco.BaseSemantEcoServlet;

import javax.servlet.annotation.WebServlet;

/**
 * App Description
 */
@WebServlet(name="SolrTesting",
            urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log",
                    "/provenance"},
            description="",
            displayName="SolrTesting")
public class SolrTesting extends BaseSemantEcoServlet {

}
