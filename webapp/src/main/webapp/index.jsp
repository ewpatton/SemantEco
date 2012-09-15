<<<<<<< HEAD
<%@ taglib uri="/WEB-INF/semantaqua.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" href="css/reset.css" type="text/css"/>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <module:styles />
    <title>Water Quality Portal</title>
    <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery.ba-bbq-1.2.1.js"></script>
    <script type="text/javascript" src="js/modernizr-2.0.js"></script>
=======
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <link rel="stylesheet" href="css/reset.css" type="text/css"/>
    <link rel="stylesheet" href="css/main.css" type="text/css"/>
    <title>Water Quality Portal</title>
    <script type="text/javascript" src="js/jquery-1.7.1.min.js"></script>
>>>>>>> Started work on servlet implementation
    <script type="text/javascript" src="js/d3.js"></script>
    <script type="text/javascript" src="js/d3.csv.js"></script>
    <script type="text/javascript" src="js/d3.time.js"></script>
    <script type="text/javascript" src="js/config.js" ></script>
    <script type="text/javascript" src="js/json.js"></script>
    <script src="main.js" type="text/javascript"></script>
    <script src="util.js" type="text/javascript"></script>
    <script src="contaminants.js" type="text/javascript"></script>
    <script src="prov/provenance.js" type="text/javascript"></script>
    <script src="map.js" type="text/javascript"></script>
<<<<<<< HEAD
    <script src="http://maps.google.com/maps?file=api&amp;v=2.x&amp;key=ABQIAAAAiPn9VO27ogin18TIwvmjjhTblPQlWTwbnJ1lYeL5MGN8z4mldhSbc_2C3LyG68tVmYiYWybgEgOS1A" type="text/javascript"></script>
    <module:scripts />
=======
    
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <script src="http://maps.google.com/maps?file=api&amp;v=2.x&amp;key=ABQIAAAAiPn9VO27ogin18TIwvmjjhTblPQlWTwbnJ1lYeL5MGN8z4mldhSbc_2C3LyG68tVmYiYWybgEgOS1A" type="text/javascript"></script>
>>>>>>> Started work on servlet implementation
  </head>
  <body onload="initialize()" onunload="closeHelpers()" onunload="GUnload()">
    <div class="modal-display">
      <div class="modal-fadeout"></div>
      <div class="modal-window-container">
	<div id="modal-window">
	  <div id="modal-title-bar">
	    <a href="javascript:closeModal();">[X]</a>
	  </div>
	  <div id="modal-content-container">
	    <div id="modal-content">
	    </div>
	  </div>
	</div>
      </div>
    </div>
    <div class="container">
    <div id="header">
      <img src="images/header.png" alt="header"/>
    </div>
    <div id="content">
      <div style="text-align:center">
	<form action="#" onsubmit="showAddress(this.zip.value,0,5000); return false">
	  <p>Zip Code:
	    <input id="zip" type="text" size="10" name="zip" value="02888" />
            <input type="submit" value="Go!" /><br/>
	    Try: 
	    <a href="javascript:submit_zip('02809');">Bristol, RI: 02809</a>, 
	    <a href="javascript:submit_zip('90813');">Los Angeles, CA: 90813</a>, 
	    <a href="javascript:submit_zip('94107');">San Francisco, CA: 94107</a>, 
	    <a href="javascript:submit_zip('95113');">Santa Clara, CA: 95113</a>, 
	    <a href="javascript:submit_zip('98103');">Seattle, WA: 98103</a>, 
	    <a href="javascript:submit_zip('94305');">Stanford, CA: 94305</a>
	  </p>
	</form>
	<p id="reportSite">&nbsp;</p>
      </div>
      <div style="margin-left:auto;margin-right:auto;width:100%">
	<div id="facet" style="float:right;width:20%">
<<<<<<< HEAD
	  <module:facets />
=======
>>>>>>> Started work on servlet implementation
	  <table border="1">
	    <tr><th>Data Source</th></tr>
	    <tr><td>
		<input id="USGS" type="checkbox" name="source" checked="checked" value="http://sparql.tw.rpi.edu/source/usgs-gov" onclick="submitQuery(this.value,this.name)" />USGS<br/>
		<input id="EPA" type="checkbox" name="source" checked="checked" value="http://sparql.tw.rpi.edu/source/epa-gov" onclick="submitQuery(this.value,this.name)" />EPA<br/>
	    </td></tr>
	  </table>
	   <table border="1">
	    <tr><th>Species</th></tr>
	    <tr>
	      <td class="nobr">
		<select name="species" id="species" onchange="onchange_species_selection();">
		  <!--<option value="">All species</option>-->
		  <option value="Human">Human</option>
		  <option value="Aquatic-life">Aquatic life</option>
		  <option value="CanadaGoose">Canada Goose</option>
		</select>
	      </td>
	    </tr>
	  </table>
	  <table border="1" name="regulation" id="regulation">
	    <tr><th>Regulation</th></tr>
      <tr><td>
      <div id="regDiv">
<!--		<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/EPA-regulation.owl" checked="checked" onclick="submitQuery(this.value,this.name)"/>EPA Regulation<br/>
		<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/ca-regulation.owl" onclick="submitQuery(this.value,this.name)"/>CA Regulation<br/>
		<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/ma-regulation.owl" onclick="submitQuery(this.value,this.name)" />MA Regulation<br/>
		<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/ny-regulation.owl" onclick="submitQuery(this.value,this.name)" />NY Regulation<br/>
		<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/ri-regulation.owl" onclick="submitQuery(this.value,this.name)"/>RI Regulation<br/>-->
		  </div>
	    </td></tr>
	  </table>
	  <table border="1">
	    <tr><th>Icon Type</th></tr>
	    <tr>
	      <td>
		<input id="cleanWater" checked="checked" type="checkbox" onchange="showhide('cleanWater')"/><img height="12" src="images/clean-water.png"/>Clean Water <br />
		<input id="facility" checked="checked" type="checkbox" onchange="showhide('facility')"/><img height="12" src="images/facility.png"/>Facility<br />
		<input id="pollutedWater" checked="checked" type="checkbox" onchange="showhide('pollutedWater')"/><img height="12" src="images/polluted-water.png"/>Polluted Water <br />
		<input id="pollutedFacility" checked="checked" type="checkbox" onchange="showhide('pollutedFacility')"/><img height="12" src="images/polluted-facility.png"/>Polluting Facility<br />
	      </td>
	    </tr>
	  </table>
	  <table border="1" name="industry_table" id="industry_table">
	    <tr><th>Industry</th></tr>
      <tr><td>	  
	  <div id="industry_selection_div"><select name= "industry_selection_canvas" id="industry_selection_canvas" width="170" style="width: 170px"> 
		</select>
</div>
	    </td></tr>
	  </table>
	  <table border="1">
	    <tr><th>Characteristic</th></tr>
	    <tr>
	      <td class="nobr">
		<input type="text" name="characteristicName"
		       id="characteristicName"/>
		<a id="characteristicSelect" href="#"
		   onclick="showSelectDialog('characteristicName')">select</a>
	      </td>
	    </tr>
	  </table>
	  <table border="1">
	    <tr><th>Health Concern</th></tr>
	    <tr>
              <td class="nobr">
		<input type="text" name="health" id="health"/>
		<a id="healthSelect" href="#"
                   onclick="showSelectDialog('health')">select</a></td>
	    </tr>
	  </table>
	  <table border="1">
	    <tr><th>Time Frame</th></tr>
	    <tr>
	      <td class="nobr">
		<select name="time" id="time">
		  <option value="">All data</option>
		  <option value="P-1M">Past Month</option>
		  <option value="P-3M">Past Three Months</option>
		  <option value="P-6M">Past Six Months</option>
		  <option value="P-1Y">Past Year</option>
		  <option value="P-2Y">Past Two Years</option>
		  <option value="P-3Y">Past Three Years</option>
		  <option value="P-5Y">Past Five Years</option>
		  <option value="P-10Y">Past Ten Years</option>
		</select>
	      </td>
	    </tr>
	  </table>
	</div>
	<div id="display" styple="width: 100%; height: 720px">
	  <div id="map_canvas" style="width: 78%; height: 720px"></div>
	  <div id="page">&nbsp;</div>
	</div>
      </div>
      
      <div id="footer"><a href="about.html">About</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="privacy.html">Privacy</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="contact.html">Contact</a></div>
      <div id="spinner"><img src="spinner.gif" alt="Processing..."/><br/>Processing your request...</div>
      <div id="sparql2"><div></div></div>
      <div id="sparql"><div id="sparql-content"></div><div class="big"><a onclick="javascript:hideSparql();">Close this window</a></div></div>
    </div>
</div>

</body>
</html>
