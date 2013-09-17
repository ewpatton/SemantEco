<%@ taglib uri="/WEB-INF/semanteco-core.tld" prefix="core" %>
<%@ taglib uri="/WEB-INF/semanteco-module.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <title>SolrTesting</title>
    <core:styles />
    <module:styles />
    <core:scripts />
    <module:scripts />
  </head>
  <body onload="SemantEco.initialize()">
    <div id="header" >
      <div class="header-text">
      <!--   <img src="images/header.png" alt="SolrTesting" /> -->
      </div>
    </div>
    <div id="content">
    <div id="facets">
    <div id="vocab" class="facet">




<br><br>
<table id="master-module" border="1" style="float:left;width:20%">

<div id="masterDiv" class="facet">


<tr> <!--  outer row -->
<td> <!--   -->
<table id="data-source-module" border="1" style="float:left;width:100%">
<th>Domain</th></td></tr>
<tr>
<td>
<div id="DataSourceFacet" class="facet">
<input name="domain" type="checkbox" value="chemical" id="chemical" /><label for="Chemical">Chemical</label><br />
<input name="domain" type="checkbox" value="organism" id= "organism" /><label for="organism">Organism</label><br />
<input name="domain" type="checkbox" value="geo" id="geo" /><label for="geo">Geospatial Feature</label><br />
</form>
</div>
</td>
</tr>

 </table>

<tr>
<td>
<table id="data-source-module" border="1" style="float:left;width:100%">
<tr><th>Expansion Options</th></tr><tr>
<td><div id="DataSourceFacet" class="facet">
<input name="searchType" type="checkbox" value="vocabulary" id="vocabulary" checked="checked" /><label for="Vocabulary expansion" >vocabulary expansion</label><br />
<input name="searchType" type="checkbox" value="topic" id= "topic"  checked="checked" /><label for="Topic expansion">topic expansion</label><br />
</div>
</td>
</tr>
</table>
</td>
</tr>


 <!--      <table id="results"><tr><th>Title</th><th>Abstract</th><th>Keywords</th></tr></table> -->
<tr>
<td>
    <table id="data-source-module" border="1">
<tr><th>Search</th></tr>
<td><div id="DataSourceFacet" class="facet">    
        <h1>Enter Search Term(s):</h1>
    <form onsubmit="return false;"><input name="term" size="15" type="text"/><br><br>
<form onsubmit="return false;"><input name="Expand" type="button" value="Expand" id="Expansion"><br><br>
<input name="Search" type="button" value="Search" id="Search"  >
</td>
</tr>
</div>
    </table>
</td>
</tr>

</td>




    </table>

  </body>
</html>
