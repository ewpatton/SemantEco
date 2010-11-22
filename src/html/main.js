/* -*- espresso-indent-level: 2; tab-width: 8; -*- */
var XHTML="http://www.w3.org/1999/xhtml";

if(document.createElementNS===undefined) {
  document.createElementNS = function(a,b) {
    return document.createElement(b);
  };
}

function parent(x) {
  return x.parentElement ? x.parentElement : x.parentNode;
}

function spinner() {
  var x = document.createElementNS(XHTML,"img");
  x.setAttribute("src","spinner.gif");
  x.setAttribute("alt","loading...");
  return x;
}

function showSparqlQuery(text) {
  var dom = document.getElementById("sparql-content");
  dom.innerHTML = text.replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\r\n/g,"<br/>");
  parent(dom).style.display = "block";
  dom = document.getElementById("sparql2").style.display = "block";
}

function hideSparql() {
  var dom = document.getElementById("sparql-content");
  parent(dom).style.display = "none";
  dom = document.getElementById("sparql2").style.display = "none";
}

function queryButton(site, func) {
  var a = document.createElementNS(XHTML,"a");
  var img = document.createElementNS(XHTML,"img");
  img.setAttribute("src","query.png");
  img.setAttribute("alt","Show underlying query");
  img.setAttribute("title","Show underlying query");
  a.setAttribute("title","Show underlying query");
  a.appendChild(img);

  if(a.addEventListener) {
    a.addEventListener("click",function() {
      showSparqlQuery(func.call(window,site,true,null));
    },false);
  }
  else {
    a.attachEvent("onclick",function() {
      showSparqlQuery(func.call(window,site,true,null));
    });
  }
  return a;
}

function td(body, text) {
  var td = document.createElementNS(XHTML,"td");
  if(body) {
    td.appendChild(body);
  }
  else {
    td.appendChild(document.createTextNode(text));
  }
  return td;
}

function queryForWaterPollution(site, justQuery, icon) {
  var query =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "SELECT DISTINCT ?site ?measure ?element ?label ?value ?unit ?limit ?time WHERE {\r\n"+
    (justQuery ? "# Find measurements that exceed threshold\r\n" : "")+
    "<"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "?measure rdf:type epa:ExceededThreshold .\r\n"+
    "?measure epa:hasElement ?element .\r\n"+
    (justQuery ? "# Get element label for pretty-printing\r\n" : "")+
    "OPTIONAL { ?element rdfs:label ?label . }\r\n"+
    "?measure epa:hasValue ?value .\r\n"+
    "?measure epa:hasUnit ?unit .\r\n"+
    (justQuery ? "# Retrieve threshold information from regulation ontology\r\n" : "")+
    "?measure rdf:type ?threshold .\r\n"+
    "?threshold owl:intersectionOf ?desc .\r\n"+
    "?desc list:member ?restriction .\r\n"+
    "?restriction owl:onProperty epa:hasValue .\r\n"+
    "?restriction owl:someValuesFrom ?datatype .\r\n"+
    "?datatype owl:withRestrictions ?desc2 .\r\n"+
    "?desc2 list:member ?limiter .\r\n"+
    "?limiter xsd:minInclusive ?limit .\r\n"+
    "?measure time:inXSDDateTime ?time .\r\n"+
    "} ORDER BY DESC(?time)";
  if(justQuery) return query;
  var timeQuery =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "PREFIX pmlp: <http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#>\r\n"+
    "\r\n"+
    "SELECT DISTINCT ?lastTime WHERE {\r\n"+
    "  <"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "  ?measure time:inXSDDateTime ?lastTime .\r\n"+
    "}\r\n"+
    "ORDER BY DESC(?lastTime)\r\n"+
    "LIMIT 1";

  var contents = document.createElementNS(XHTML,"div");
  contents.className = "map-popup";
  var nextElem = document.createElementNS(XHTML,"h4");
  nextElem.appendChild(document.createTextNode(site.label));
  contents.appendChild(nextElem);
  
  if(site.isPolluted) {
    var mostRecent = nextElem = document.createElementNS(XHTML,"p");
    var mostRecentSpinner;
    nextElem.appendChild(document.createTextNode("Last test occurred on "));
    nextElem.appendChild(mostRecentSpinner=spinner());
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"p");
    nextElem.appendChild(document.createTextNode("Known Pollutants:"));
    nextElem.appendChild(queryButton(site, queryForWaterPollution));
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"table");
    var tbody = document.createElementNS(XHTML,"tbody");
    nextElem.appendChild(tbody);
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"tr");
    var td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Pollutant"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Time Measured"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Value"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Limit"));
    nextElem.appendChild(td);
    tbody.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"tr");
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    tbody.appendChild(nextElem);
    
    var success = function(data) {
      tbody.removeChild(nextElem);
      $(data).find('result').each(function(){
	var time="",unit="",limit="",label="",value="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="label")
	  {
	    label=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="unit")
	  {
	    unit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="limit")
	  {
	    limit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="time")
	  {
	    time=($(this).find("literal").text());
	  }
	  
	  if(label!=""&&value!=""&&unit!=""&&limit!=""&&time!=""){
	    nextElem = document.createElementNS(XHTML,"tr");
	    nextElem.appendChild(window.td(null,label));
	    nextElem.appendChild(window.td(null,time));
	    nextElem.appendChild(window.td(null,value+" "+unit));
	    nextElem.appendChild(window.td(null,limit+" "+unit));
	    tbody.appendChild(nextElem);
	  }
	});
      });
      icon.openInfoWindow(contents);
    };
    var success2 = function(data) {
      $(data).find('result').each(function() {
	$(this).find("binding").each(function() {
	  if($(this).attr("name")=="lastTime") {
	    mostRecent.removeChild(mostRecentSpinner);
	    mostRecent.appendChild(document.createTextNode($(this).find("literal").text()));
	  }
	});
      });
    };
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
	    data: "session="+window.sessionID+			    
	    "&query="+encodeURIComponent(query), // query parameter
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent",
	    data: "session="+window.sessionID+
	    "&query="+encodeURIComponent(timeQuery),
	    beforeSend: function(xhr) {
	      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
	    dataType: "xml",
	    error: function(xhr, text, err) {
	      if(xhr.status == 200) {
		success2(xhr.responseXML);
	      }
	    },
	    success: success2
	   });
    return contents;
  }
  else {
    nextElem = document.createElementNS(XHTML,"p");
    nextElem.appendChild(document.createTextNode("According to all current regulations, this water supply is not polluted."));
    contents.appendChild(nextElem);
    return contents;
  }
}

function queryForFacilityPollution(site, justQuery, icon) {
  var query =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "SELECT DISTINCT ?site ?measure ?element ?label ?value ?unit ?limit ?time WHERE {\r\n"+
    (justQuery ? "# Find measurements that are Clean Water Act violations\r\n" : "")+
    "<"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "?measure rdf:type epa:Violation .\r\n"+
    "?measure epa:hasElement ?element .\r\n"+
    (justQuery ? "# Get element label for pretty-printing\r\n" : "")+
    "OPTIONAL { ?element rdfs:label ?label . }\r\n"+
    "?measure epa:hasValue ?value .\r\n"+
    "?measure epa:hasUnit ?unit .\r\n"+
    (justQuery ? "# Retrieve threshold information from regulation ontology\r\n" : "")+
    "?measure rdf:type ?threshold .\r\n"+
    "?threshold owl:intersectionOf ?desc .\r\n"+
    "?desc list:member ?restriction .\r\n"+
    "?restriction owl:onProperty epa:hasValue .\r\n"+
    "?restriction owl:someValuesFrom ?datatype .\r\n"+
    "?datatype owl:withRestrictions ?desc2 .\r\n"+
    "?desc2 list:member ?limiter .\r\n"+
    "?limiter ?exclusion ?limit .\r\n"+
    "?measure time:inXSDDateTime ?time .\r\n"+
    "} ORDER BY DESC(?time)";
  if(justQuery) return query;
  var timeQuery =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "PREFIX pmlp: <http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#>\r\n"+
    "\r\n"+
    "SELECT DISTINCT ?lastTime WHERE {\r\n"+
    "  <"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "  ?measure time:inXSDDateTime ?lastTime .\r\n"+
    "}\r\n"+
    "ORDER BY DESC(?lastTime)\r\n"+
    "LIMIT 1";

  var contents = document.createElementNS(XHTML,"div");
  contents.className = "map-popup";
  var nextElem = document.createElementNS(XHTML,"h4");
  nextElem.appendChild(document.createTextNode(site.label));
  contents.appendChild(nextElem);
  
  if(site.isPolluted) {
    var mostRecent = nextElem = document.createElementNS(XHTML,"p");
    var mostRecentSpinner;
    nextElem.appendChild(document.createTextNode("Last test occurred on "));
    nextElem.appendChild(mostRecentSpinner=spinner());
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"p");
    nextElem.appendChild(document.createTextNode("Known Pollutants:"));
    nextElem.appendChild(queryButton(site, queryForWaterPollution));
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"table");
    var tbody = document.createElementNS(XHTML,"tbody");
    nextElem.appendChild(tbody);
    contents.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"tr");
    var td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Pollutant"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Time Measured"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Value"));
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"th");
    td.appendChild(document.createTextNode("Limit"));
    nextElem.appendChild(td);
    tbody.appendChild(nextElem);
    nextElem = document.createElementNS(XHTML,"tr");
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    td = document.createElementNS(XHTML,"td");
    td.appendChild(spinner());
    nextElem.appendChild(td);
    tbody.appendChild(nextElem);
    
    var success = function(data) {
      tbody.removeChild(nextElem);
      $(data).find('result').each(function(){
	var time="",unit="",limit="",label="",value="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="label")
	  {
	    label=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="unit")
	  {
	    unit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="limit")
	  {
	    limit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="time")
	  {
	    time=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="element"&&label=="") {
	    label = $(this).find("uri").text();
	    label = label.substr(label.indexOf("#")+1);
	    label = label.replace(/_/g," ");
	  }
	  
	  if(label!=""&&value!=""&&unit!=""&&limit!=""&&time!=""){
	    nextElem = document.createElementNS(XHTML,"tr");
	    nextElem.appendChild(window.td(null,label));
	    nextElem.appendChild(window.td(null,time));
	    nextElem.appendChild(window.td(null,value+" "+unit));
	    nextElem.appendChild(window.td(null,limit+" "+unit));
	    tbody.appendChild(nextElem);
	  }
	});
      });
      icon.openInfoWindow(contents);
    };
    var success2 = function(data) {
      $(data).find('result').each(function() {
	$(this).find("binding").each(function() {
	  if($(this).attr("name")=="lastTime") {
	    mostRecent.removeChild(mostRecentSpinner);
	    mostRecent.appendChild(document.createTextNode($(this).find("literal").text()));
	  }
	});
      });
    };
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
	    data: "session="+window.sessionID+			    
	    "&query="+encodeURIComponent(query), // query parameter
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent",
	    data: "session="+window.sessionID+
	    "&query="+encodeURIComponent(timeQuery),
	    beforeSend: function(xhr) {
	      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
	    dataType: "xml",
	    error: function(xhr, text, err) {
	      if(xhr.status == 200) {
		success2(xhr.responseXML);
	      }
	    },
	    success: success2
	   });
    return contents;
  }
  else {
    nextElem = document.createElementNS(XHTML,"p");
    nextElem.appendChild(document.createTextNode("This facility has not violated the Clean Water Act.."));
    contents.appendChild(nextElem);
    return contents;
  }
}

function queryForWaterDataProvider(site, justQuery) {
  
}

function queryForWaterRegulationProvider(site, justQuery) {
  
}

function createMapPopup(site) {
  if(site.isWaterSource) {
    var elem = document.createElementNS(XHTML,"div");
    
  }
}

function submitZip(zip) {
  var xhttp = null;
  if(xhttp==null && window.XMLHttpRequest)
    xhttp = new XMLHttpRequest();
  if(xhttp==null)
    xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  if(xhttp==null) {
    window.alert("Your browser does not support JavaScript XML requests");
    return;
  }
  var elem = document.getElementById("zip");
  var p = parent(elem);
  var spinner = document.getElementById("spinner");
  spinner.style.display = "block";
  xhttp.open("GET","http://was.tw.rpi.edu/water/service/zip?code="+zip,true);
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState==4) {
      spinner.style.display = "none";
      if(xhttp.status==200) {
        var data = JSON.parse(xhttp.responseText);
        if(data.error!=undefined) {
        }
        else if(data.session!=undefined) {
          window.sessionID = data.session;
          spinner.style.display = "block";
          p = document.getElementById("display");
          var mapContainer = document.createElementNS(XHTML,"div");
          var mapContent = document.createElementNS(XHTML,"div");
          var mapLegend = document.createElementNS(XHTML,"div");
          var tbl = document.createElementNS(XHTML,"table");
          var tbody = document.createElementNS(XHTML,"tbody");
          tbl.appendChild(tbody);
          tbl.style.width="100%";
          mapLegend.appendChild(tbl);
          var tr = document.createElementNS(XHTML,"tr");
          tbody.appendChild(tr);
          var td = document.createElementNS(XHTML,"td");
          td.appendChild(document.createTextNode("Legend: "));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          var img = document.createElementNS(XHTML,"img");
          mapContent.style.width="600px";
          mapContent.style.height="400px";
          mapContent.style.marginLeft="auto";
          mapContent.style.marginRight="auto";
          mapContainer.appendChild(mapContent);
          mapLegend.style.width="600px";
          mapLegend.style.marginLeft="auto";
          mapLegend.style.marginRight="auto";
          mapLegend.style.backgroundColor="white";
          mapLegend.style.fontSize="9pt";
          mapContainer.appendChild(mapLegend);
          img.setAttribute("src","image/cleanwater2.png");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Water"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/pollutedwater.png");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Polluted Water"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/facility.jpg");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Facility"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/facilitypollute.jpg");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Polluting Facility"));
          tr.appendChild(td);
          parent(p).replaceChild(mapContainer,p);
          mapContainer.setAttribute("id","display");
          window.map = new GMap2(mapContent);
          window.map.setUI(new GMapUIOptions(new GSize(600,400)));
          mapContent.style.borderStyle = "double";
          mapContent.style.borderWidth = "1px";
          mapContent.style.borderColor = "black";
          window.map.setCenter(new GLatLng(data.result.lat, data.result.lng), 12);
          window.map.enableScrollWheelZoom();
          window.geocoder = new GClientGeocoder();
          showPollutedWater();
	  //showViolatedFacility();
        }
      }
      else {
        
      }
    }
  };
  xhttp.send(null);
}

function colorize() {
  var zip = document.getElementById("zip");
  var span = document.getElementById("instruct");
  if(zip.value.length==5) {
    zip.style.borderColor = "#229922";
    zip.style.backgroundColor = "#ddffdd";
    span.style.visibility = "visible";
  }
  else {
    zip.style.borderColor = "#999922";
    zip.style.backgroundColor = "#ffffdd";
    span.style.visibility = "hidden";
  }
}

function verify(e) {
  e = e ? e : window.event;
  var key = e.which ? e.which : e.keyCode;
  var keychar = String.fromCharCode(key);
  if(key==13) {
    submitZip(document.getElementById("zip").value);
    return false;
  }
  if(e.ctrlKey || e.altKey || e.metaKey) return true;
  if((key==null) || (key==0) || (key==8) || (key==9) || (key==27)) {
    return true;
  }
  else if((("0123456789").indexOf(keychar) > -1)) {
    return true;
  }
  return false;
}