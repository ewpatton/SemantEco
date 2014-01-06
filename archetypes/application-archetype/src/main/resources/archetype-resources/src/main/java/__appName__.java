#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import edu.rpi.tw.escience.semanteco.BaseSemantEcoServlet;

import javax.servlet.annotation.WebServlet;

/**
 * App Description
 */
@WebServlet(name="${appName}",
            urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log",
                    "/provenance"},
            description="",
            displayName="${appName}")
public class ${appName} extends BaseSemantEcoServlet {

}
