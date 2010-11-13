function submit(zip) {
    window.alert(zip);
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