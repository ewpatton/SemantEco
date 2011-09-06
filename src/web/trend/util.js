var host="http://localhost";
var host="http://was.tw.rpi.edu";
var thisserviceagent=host+"/swqp/trend/trendData.php";
var orgpediaAgent=host+"/swqp/orgpediaData.php";
var trendAppBase =host+"/swqp/trend/";
var graphNamePrefix="http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-";
var EPAGraphNamePostfix="/version/2011-Mar-19";
var datahost="http://sparql.tw.rpi.edu";


var generalPopupWindow;


//ref: http://qwwebservices.usgs.gov/portal.html#
		function openPopup(URL, windowName){
			if (generalPopupWindow) generalPopupWindow.close();
			generalPopupWindow = window.open(trendAppBase + URL, windowName, "status=1,menubar=1,resizable=1,width=650,height=400,location=1,scrollbars=1");
			return false;
		}


//ref: http://stackoverflow.com/questions/163563/javascript-date-constructor-doesnt-work
//the input str is like: 1970-03-03T11:45:00
function myDate(str){
	//var str="1970-03-03";
	var strLen=str.length;
	if(strLen<10){
		alert("Invalid date string: "+str);
		return undefined;
	}
  //var str='1970-03-03T11:45:01';
	var dateStr=str.substring(0, 10);
  //var curDate=new Date(dateStr);
	//date
	var dateArray = dateStr.split("-");
	var theDate = new Date(dateArray[0],dateArray[1],dateArray[2]); 

	//hour, min, second
	if(str.length>=19){
		var  hourStr=str.substring(11, 13);
		theDate.setHours(hourStr);
		var  minStr=str.substring(14, 16);
		theDate.setMinutes(minStr);
		var  secondStr=str.substring(17);
		theDate.setSeconds(secondStr);
	}
	//alert(str.length);
	//alert(dateStr+", "+hourStr+", "+minStr+", "+secondStr);
	//alert(theDate);
	return theDate;
}

function append_selection_element(select, value, html){
  var element = document.createElement("option");
  element.setAttribute("value",value);
  element.innerHTML = html; 
  select.appendChild(element);
}


function USGSSiteUri2Id(siteIdUri, pat){
	//var pat="#SiteId-"; for uri in getting result from sparql query
	//var pat="site-"; for url in getting result from the linked query
	var patLen=pat.length;

	var patIndex=siteIdUri.indexOf(pat);
	if(patIndex==-1){
		alert("siteUri can't find the pattern #SiteId-");
		return "";
	}

	siteId=siteIdUri.substring(patIndex+patLen);
	//alert(siteId);
	return siteId;
}

function init_USGS_selection() {
	init_status();
	curDataSource="USGS";
	init_state_selection();
}

function init_EPA_selection() {
	init_status();
	curDataSource="EPA";
	init_state_selection();
}

function init_status() {
	curDataSource="";
	curStateName="";
	curStateAbbr="";
	curStateCode="";
	curCountyCode="";
	curSiteId="";
	curPermit="";
	curElementName="";
}

function init_state_selection() {
	/*
	//data source
  data_source_select = document.getElementById("data_source_selection_canvas");
	dataSources = new Array("EPA", "USGS");
  for (var i = 0; i < dataSources.length; i++ ){
		append_selection_element(data_source_select, dataSources[i] , dataSources[i] );
  }
  data_source_select.selectedIndex = 0;
	curDataSource=data_source_select.options[0].value;
	*/

  // state
	var selectionId=curDataSource+'_state_selection_canvas';
  var state_select = document.getElementById(selectionId);
  //state_select = document.getElementById("usgs_state_selection_canvas");
  {
		state_s_index = new Array();
		stateNames = new Array("CALIFORNIA", "MASSACHUSETTS", "RHODE ISLAND");
		stateAbbrs = new Array("ca", "ma", "ri");
		stateCodes = new Array("6", "25", "44");

    for (var i = 0; i < stateNames.length; i++ ){
		append_selection_element(state_select, stateNames[i] , stateNames[i] );
        state_s_index[stateNames[i] ] = i;
   }
    state_select.selectedIndex = 0;
  }
	onchange_state_selection();
}
