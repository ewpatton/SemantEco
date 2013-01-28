/* -*- espresso-indent-level: 2; -*- */
//from http://stackoverflow.com/questions/840781/easiest-way-to-find-duplicate-values-in-a-javascript-array
function eliminateDuplicates(arr) {
  var i,
  len=arr.length,
  out=[],
  obj={};

  for (i=0;i<len;i++) {
    obj[arr[i]]=0;
  }
  for (i in obj) {
    out.push(i);
  }
  return out;
}

//<input type="radio" name="regulation" value="http://escience.rpi.edu/ontology/semanteco/2/0/EPA-regulation.owl" checked="checked" onclick="submitQuery(this.value,this.name)"/>EPA Regulation<br/>
function append_radio_element(radioTable, curName, curValue, curChecked, curHtml){
  var element = document.createElement('input');
  element.setAttribute("type", "radio");
  element.setAttribute("name", curName);
  var curId=curHtml.replace(/ /g, "_");
  element.setAttribute("id", curId);
	element.setAttribute("value", curValue);
  element.setAttribute("checked", curChecked);
  element.setAttribute("onclick", "submitQuery(this.value,this.name)");   
  //element.innerHTML = curHtml+"<br/>"; 
  //alert(element.innerHTML);
  radioTable.appendChild(element);
  //alert(curId);
  radioTable.innerHTML += curHtml+"<br/>";
  //document.getElementById(curId).innerHTML = curHtml+"<br/>"; 
}

function append_selection_element(select, value, html){
  var element = document.createElement("option");
  element.setAttribute("value",value);
  element.innerHTML = html; 
  select.appendChild(element);
}

function PadNumber(number, width) {
  var padded=number.toString();
  var pad="";
  var dist=width-padded.length;
  for(var i=0; i < dist;i++)
    pad = "0"+pad;

  return pad+padded;
}

function parseQuery(url){
  var begin=url.indexOf("?");
  var paramsUrl=decodeURIComponent(url.substring(begin+1));
  //alert(paramsUrl);

  var params=new Array();
  var parts=paramsUrl.split("&");
  for(var i=0;i<parts.length;i++){
    var pieces = parts[i].split("=");
    if(pieces.length!=2) {
      alert("The query is not well formated.");
      return params;
    }
    //alert(pieces[0]+", "+pieces[1]);
    var temp=pieces[0];
    params[temp]=pieces[1];
  }

  //for (param in params)
  //	alert(params[param]);

  return params;
}

//from USGS
function getURLParam(strParamName){
  var strReturn = "";
  var strHref = window.location.href;
  if ( strHref.indexOf("?") > -1 ){
    var strQueryString = strHref.substr(strHref.indexOf("?"));
    var aQueryString = strQueryString.split("&");
    for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
      if (aQueryString[iParam].indexOf(strParamName + "=") > -1 ){
        var aParam = aQueryString[iParam].split("=");
        strReturn = aParam[1];
        break;
      }
    }
  }
  strReturn = strReturn.replace(/\+/g,' ');
  return unescape(strReturn);
}

function createElement(type, name) {
  var element = null;

  try {
    // First try the IE way if this fails then use the standard way
    element = document.createElement('<'+type+' name="'+name+'">');
  } catch (e) {
    //Probably failed because we're not running on IE
  }
  if (!element) {
    element = document.createElement(type);
    element.name = name;
  }
  return element;
}
