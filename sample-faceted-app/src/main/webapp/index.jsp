<%@ taglib uri="/WEB-INF/semanteco-core.tld" prefix="core" %>
<%@ taglib uri="/WEB-INF/semanteco-module.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <title>FacetedApp</title>
    <core:styles />
    <module:styles />

 <link rel="stylesheet" type="text/css" href="js/jstree/themes/default/style.css" />
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
        <link rel="stylesheet" type="text/css" href="css/dropdownchecklist/ui.dropdownchecklist.standalone.css" />
        <link rel="stylesheet" type="text/css" href="css/dropdownchecklist/ui.dropdownchecklist.themeroller.css" />
        <link rel="stylesheet" type="text/css" href="js/jqplot/jquery.jqplot.min.css" />
        <link rel="stylesheet" type="text/css" href="css/annotator/annotator-css-core.css" />
        <link rel="stylesheet" type="text/css" href="//qtip2.com/v/stable/jquery.qtip.min.css" />
<link rel="shortcut icon" href="/flex.gif" type="image/x-icon">
<link rel="icon" href="/flex.gif" type="image/x-icon">
  </head>
  <!-- this was causing the hierarchy create default action for SemantEco Portal
  but not suitable for an app that does not want to load facets immediately on load
  <body onload="SemantEco.initialize()">
  
   -->
   
   
     <body>
   
    <div id="header">
      <div class="header-text">
        <img src="images/header.png" alt="FacetedApp" />
      </div>
    </div>
    <div id="content">
    
    
    <div id="sidebar" class="sidebar">
                <div id="facets">
                
                    <!-- classes view -->
                    <div id="chemicals-module" class="module-facet-container">
                        <h3>Chemicals</h3>
                        <div id="classesFacet" class="facet">
                            <div id="ChemicalTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="ClassesDescription" class="description">
                                <div id="ClassBox"></div>
                            </div>
                        </div>
                    </div>
                    
                    
                         <!-- classes view -->
                    <div id="geospatialFeatures-module" class="module-facet-container">
                        <h3>Geospatial Features</h3>
                        <div id="PropertiesFacet" class="facet">
                            <div id="GeospatialFeatureTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="PropertiesDescription" class="description">
                                <div id="PropertyBox"></div>
                            </div>
                        </div>
                    </div>
                    
                    <div id="organisms-module" class="module-facet-container">
                        <h3>Organisms</h3>
                        <div id="PropertiesFacet" class="facet">
                            <div id="OrganismTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="PropertiesDescription" class="description">
                                <div id="PropertyBox"></div>
                            </div>
                        </div>
                    </div>
                    
                    
                     <div id="topics-module" class="module-facet-container">
                        <h3>Topics</h3>
                        <div id="TopicsFacet" class="facet">
                            <div id="TopicTree" class="hierarchy"></div>
                            <div class="show-annotations-link inactive-text-link">Show Annotations</div>
                            <div id="PropertiesDescription" class="description">
                                <div id="PropertyBox"></div>
                            </div>
                        </div>
                    </div>
                    
                    
                    </div>
                    </div>
    
    
    
    
    </div>

<!-- blah2  -->
    <core:scripts />
    <module:scripts />
        <script type="text/javascript" src="js/test.js"></script>
        <div id="resultsView"></div>
				</div>
  </body>
</html>
