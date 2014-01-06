package edu.rpi.tw.esience.facetedapp;

import edu.rpi.tw.escience.semanteco.BaseSemantEcoServlet;

import javax.servlet.annotation.WebServlet;

/**
 * App Description
 */
@WebServlet(name="FacetedApp",
            urlPatterns={"/rest/*","/js/modules/*","/js/config.js","/log",
                    "/provenance"},
            description="",
            displayName="FacetedApp")
public class FacetedApp extends BaseSemantEcoServlet {

}
