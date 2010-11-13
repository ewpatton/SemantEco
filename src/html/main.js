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
    var spin = document.createElementNS(XHTML,"img");
    spin.setAttribute("src","spinner.gif");
    spin.setAttribute("alt","Loading...");
    p.insertBefore(spin, elem.nextSibling);
    xhttp.open("GET","http://was.tw.rpi.edu:14490/zip?code="+zip,true);
    xhttp.onreadystatechange = function() {
        if(xhttp.readyState==4) {
            if(xhttp.status==200) {
            	var data = JSON.parse(xhttp.responseText);
            	if(data.error!=undefined) {
            	}
            	else if(data.result!=undefined) {
            		var mapContainer = document.createElementNS(XHTML,"div");
            		var mapContent = document.createElementNS(XHTML,"div");
            		mapContent.style.width="600px";
            		mapContent.style.height="400px";
            		mapContainer.appendChild(mapContent);
            		parent(p).replaceChild(p,mapContent);
            		window.map = new GMap2(mapContent);
                    window.map.setCenter(new GLatLng(data.result.lat, data.result.lng), 13);
            		window.geocoder = new GClientGeocoder();
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