var XHTML="http://www.w3.org/1999/xhtml";

function parent(x) {
	return x.parentElement ? x.parentElement : x.parentNode;
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
    xhttp.open("GET","http://was.tw.rpi.edu:14490/zip?code="+zip,true);
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
    var key = e ? e.which : window.event.keyCode;
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