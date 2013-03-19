<%@ taglib uri="/WEB-INF/semanteco.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="css/reset.css" type="text/css"/>
    <link rel="stylesheet" href="css/start/jquery-ui.min.css" type="text/css"/>
    <link rel="stylesheet" href="css/start/jquery.ui.theme.css" type="text/css"/>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="js/jqplot/jquery.jqplot.min.css" />
    <link rel="stylesheet" type="text/css" href="js/jstree/themes/default/style.css" />
    <module:styles />
    <title>SemantEco Data Portal</title>
    <script type="text/javascript" src="js/underscore-min.js"></script>
    <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery-ui-1.10.1.custom.min.js"></script>
    <script type="text/javascript" src="js/jquery.ba-bbq-1.2.1.js"></script>
    <script type="text/javascript" src="js/jquery.cookie.js"></script>
    <script type="text/javascript" src="js/jquery.client.js"></script>
    <script type="text/javascript" src="js/modernizr-2.0.js"></script>
    <script type="text/javascript" src="js/d3.js"></script>
    <script type="text/javascript" src="js/d3.csv.js"></script>
    <script type="text/javascript" src="js/d3.time.js"></script>
    <script type="text/javascript" src="js/json.js"></script>
    <script type="text/javascript" src="js/SemantEco.js"></script>
    <script type="text/javascript" src="js/SemantEcoUI.js"></script>
    <script type="text/javascript" src="js/SemantEcoUI.HierarchicalFacet.js"></script>
    <script type="text/javascript" src="js/config.js" ></script>
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAkAvsEZc18GOe01jOVpX48hnWRIgIajec&amp;sensor=false"></script>
    <script type="text/javascript" src="js/jqplot/jquery.jqplot.min.js"></script>
    <script type="text/javascript" src="js/jqplot/plugins/jqplot.highlighter.js"></script>
    <script type="text/javascript" src="js/jqplot/plugins/jqplot.cursor.min.js"></script>
    <script type="text/javascript" src="js/jqplot/plugins/jqplot.dateAxisRenderer.min.js"></script>
    <script type="text/javascript" src="js/jstree/jquery.jstree.js"></script>
    <module:scripts />
  </head>
  <body onload="SemantEco.initialize()">
    <div class="container">
	    <div id="header">
	      <img src="images/header.png" alt="header"/>
	    </div>
	    <div id="content">
    	  <div class="main">
			<form>
		  	  <p>Zip Code:
		        <input id="zip" type="text" size="10" name="zip" value="02809" />
	            <input type="button" value="Go!" onclick="SemantEco.showAddress(); return false;" style="margin-left:20px;" /><br/>
		        Try:
			    <a href="javascript:SemantEco.showAddress('02809');">Bristol, RI: 02809</a>, 
			    <a href="javascript:SemantEco.showAddress('90813');">Los Angeles, CA: 90813</a>, 
			    <a href="javascript:SemantEco.showAddress('94107');">San Francisco, CA: 94107</a>, 
			    <a href="javascript:SemantEco.showAddress('95113');">Santa Clara, CA: 95113</a>, 
			    <a href="javascript:SemantEco.showAddress('98103');">Seattle, WA: 98103</a>, 
			    <a href="javascript:SemantEco.showAddress('94305');">Stanford, CA: 94305</a>,
			    <a href="javascript:SemantEco.showAddress('93103');">Santa Barbara, CA: 93103</a>,
			    <a href="javascript:SemantEco.showAddress('21613');">Cambridge, MD: 21613</a>,
			    <a href="javascript:SemantEco.showAddress('80103');">Welby, CO: 80103</a>
		  	  </p>
			</form>
			<p id="reportSite">&nbsp;</p>
      	  </div>
      	  <div class="sidebar">
			<div id="facets" style="float:right;width:20%">
		  	  <module:facets />
			</div>
      	  </div>
		  <div id="display">
		    <div id="map_canvas"></div>
		    <div id="page">&nbsp;</div>
		  </div>
    	  <div class="clear" style="clear:both;"></div>
      	  <div id="footer"><a href="about.html">About</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="privacy.html">Privacy</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="contact.html">Contact</a></div>
      	  <div id="spinner"><img src="images/spinner.gif" alt="Processing..."/><br/>Processing your request...</div>
      	  <div id="sparql2"><div></div></div>
      	  <div id="sparql"><div id="sparql-content"></div><div class="big"><a onclick="javascript:hideSparql();">Close this window</a></div></div>
    	</div>

	    <div class="lightbox" style="display:none;">
	        <div class="lb_shadow">
	            <div class="positioner">
	                <div class="lb_container" style="display:none;">
	                    <div class="lb_loading"><img src="images/spinner.gif" alt="Processing..."/><br/>Processing your request...</div>
	                    <div class="lb_closebutton"></div>
	                    <div class="clear"></div>
	                    <div class="lb_content" id="id_lb_content"></div>
	                </div>
	            </div>
	        </div>
		</div>
	</div>
</body>
</html>
