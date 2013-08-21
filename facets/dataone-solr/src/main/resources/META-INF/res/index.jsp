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
    <div id="header">
      <div class="header-text">
        <img src="images/header.png" alt="SolrTesting" />
      </div>
    </div>
    <div id="content">
    <div id="vocab" class="facet">

<h1>Search</h1>
<form><input name="term" size="15" type="text" /><br><br><input name="Submit" type="submit" value="Submit">
<br><br>
<table id="data-source-module" border="1">
<tr><th>Domain</th></tr><tr>
<td><div id="DataSourceFacet" class="facet">
<input name="domain" type="checkbox" value="chemical" id="chemical" /><label for="Chemical">Chemical</label><br />
<input name="domain" type="checkbox" value="organism" id= "organism" /><label for="organism">Organism</label><br />
<input name="domain" type="checkbox" value="geo" id="geo" /><label for="geo">Geospatial Feature</label><br />


</form>


</div>
    
    
    
      <table id="results"><tr><th>Title</th><th>Abstract</th><th>Keywords</th></tr></table>
    </div>
  </body>
</html>
