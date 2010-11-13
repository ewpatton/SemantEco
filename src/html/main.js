function submit(zip) {
    var xhttp = null;
    if(xhttp==null && window.XMLHttpRequest)
        xhttp = new XMLHttpRequest();
    if(xhttp==null)
        xhttp = new ActiveXObject("Microsoft.XMLHTTP");
    if(xhttp==null) {
        window.alert("Your browser does not support JavaScript XML requests");
        return;
    }
    xhttp.open("GET","http://was.tw.rpi.edu:14490/zip?code="+zip,true);
    xhttp.onreadystatechange = function() {
        if(xhttp.readyState==4) {
            if(xhttp.status==200) {
            }
            else if(xhttp.status==404) {
            }
            window.alert(xhttp.responseText);
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
    var key = e ? e.which : window.event.keyCode;
    var keychar = String.fromCharCode(key);
    if(key==13) {
	submit(document.getElementById("zip").value);
	return false;
    }
    if((key==null) || (key==0) || (key==8) || (key==9) || (key==27)) {
	return true;
    }
    else if((("0123456789").indexOf(keychar) > -1)) {
	return true;
    }
    return false;
}