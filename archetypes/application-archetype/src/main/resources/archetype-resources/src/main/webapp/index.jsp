#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ taglib uri="/WEB-INF/semanteco-core.tld" prefix="core" %>
<%@ taglib uri="/WEB-INF/semanteco-module.tld" prefix="module" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml">
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    <title>${appName}</title>
    <core:styles />
    <module:styles />
    <core:scripts />
    <module:scripts />
  </head>
  <body onload="SemantEco.initialize()">
    <div id="header">
      <div class="header-text">
        <img src="images/header.png" alt="${appName}" />
      </div>
    </div>
    <div id="content">
    </div>
  </body>
</html>
