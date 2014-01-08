<%@ taglib uri="/WEB-INF/semanteco-core.tld" prefix="core" %>
<%@ taglib uri="/WEB-INF/semanteco-module.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <title>SemantEco Data Portal</title>
    <core:styles />
    <module:styles />
    <core:scripts />
    <script src="http://maps.googleapis.com/maps/api/js?key=AIzaSyAkAvsEZc18GOe01jOVpX48hnWRIgIajec&amp;sensor=false"></script>
    <module:scripts />
  </head>
  <body onload="SemantEco.initialize()">
	    <div id="header">
	      <div class="header-text">
	        <img src="images/header.png" alt="SemantEco" />
	      </div>
	    </div>
	    <div id="content">
      	  <div id="sidebar" class="sidebar">
      	    <div class="search button float">Search</div>
			<div id="facets" style="float:right;width:20%">
		  	  <module:facets />
			</div>
			<div class="search button">Search</div>
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
</body>
</html>
