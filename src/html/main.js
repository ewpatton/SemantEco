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

function queryButton(site, func) {
    /*
	var a = document.createElementNS(XHTML,"a");
	var img = document.createElementNS(XHTML,"img");
	img.setAttribute("src","query.png");
	img.setAttribute("alt","Show underlying query");
	a.appendChild(img);
    */
	var a="<a>"+"<img src='query.png' alt='show underlying query' /></a>";
	/*
	if(a.addEventListener) {
	}
	else {
	}*/
	return a;
}

function queryForWaterPollution(site, justQuery,lat,lng,map) {


    var contents = document.createElementNS(XHTML,"div");
    var contentString="";
    var success = function(data) {
	var nextElem, td;
	contentString+="<h4>"+site.label+"</h4>";
	contentString+="<p>Pollutants:</p>";
	contentString+=queryButton(site,queryForWaterPollution);
	contentString+="<table border='1'>";
	contentString+="<tr>";
	contentString+="<th>Pollutant</th>";
	contentString+="<th>Time Measured</th>";
	contentString+="<th>Value</th>";
	contentString+="<th>Limit</th>";
	contentString+="</tr>";

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
			    contentString+="<tr>";
			    contentString+="<td>"+label+"</td>";
			    contentString+="<td>"+time+"</td>";
			    contentString+="<td>"+value+"</td>";
			    contentString+="<td>"+limit+"</td>";
			    contentString+="</tr>";
			}
		    });
	    });
	contentString+="</table>";
	if(lat!=""&&lng!=""){
	    var blueIcon = new GIcon(G_DEFAULT_ICON,"image/pollutedwater.png");
	    blueIcon.iconSize = new GSize(29,34);
	    var latlng = new GLatLng(lat ,lng);
	    markerOptions = { icon:blueIcon };
	    var marker=new GMarker(latlng, markerOptions);
	    GEvent.addListener(marker, "click",
			       function() {
				   marker.openInfoWindowHtml(contentString);
			       }
			       );
	    map.addOverlay(marker);
	}

    }
    

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
	"}";
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
		"  XXXXX epa:hasMeasurement ?measure .\r\n"+
		"  ?measure time:inXSDDateTime ?lastTime .\r\n"+
		"}\r\n"+
		"ORDER BY DESC(?time)\r\n"+
		"LIMIT 1";
	

	var nextElem = document.createElementNS(XHTML,"h4");
	nextElem.appendChild(document.createTextNode(site.label));
	contents.appendChild(nextElem);
	
	if(site.isPolluted) {
		$.ajax({type: "GET",
			    url: "http://was.tw.rpi.edu/water/service/agent", // SPARQL service URI
			    data: "countyCode="+encodeURIComponent(window.appState.countyCode)+
			    "&stateCode="+window.appState.stateCode+
			    "&state="+window.appState.stateAbbr+
			    "&zip="+window.appState.zipCode+			    
			    "&query="+encodeURIComponent(query), // query parameter
			    beforeSend: function(xhr) {
                            xhr.setRequestHeader("Accept", "application/sparql-results+xml");
                        },
			    dataType: "xml",
			    error: function(xhr, text, err) {
			    if(xhr.status == 200) {
				succcess(xhr.responseXML);
			    }
                        },
			    success: success
			    });

	}
	else {
		nextElem = document.createElementNS(XHTML,"p");
		nextElem.appendChild(document.createTextNode("According to all current regulations, this water supply is not polluted."));
		contents.appendChild(nextElem);
		return contentString;
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
            	else if(data.result!=undefined) {
            		window.appState = data.result;
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