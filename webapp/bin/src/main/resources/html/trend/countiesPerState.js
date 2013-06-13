function onchange_state_selection(){
	var selectionId=curDataSource+'_state_selection_canvas';
	var curIndex=document.getElementById(selectionId).selectedIndex;
	if(curIndex>=0){
		curStateName = stateNames[curIndex];
		curStateAbbr = stateAbbrs[curIndex];
		curStateCode = stateCodes[curIndex];
		sendCountyQuery(curStateName);
	}
}

function sendCountyQuery(stateName){
 var sparqlproxy = "http://logd.tw.rpi.edu/ws/sparqlproxy.php";
 var service = "http://tw2.tw.rpi.edu:2035/sparql";
 var sparqlCounties = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
		"PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
		"\r\n"+
		"SELECT DISTINCT ?ctyName ?countyCode\r\n"+
		"WHERE {\r\n"+
		" ?cty rdf:type epa:County .\r\n"+
		" ?cty epa:hasStateName \"" + stateName + "\" .\r\n"+
		" ?cty epa:hasCountyName ?ctyName . \r\n"+
		" ?cty epa:hasCountyCode ?countyCode . \r\n"+
		"}";

 var queryurl = sparqlproxy
                + "?" + "output=gvds"
                + "&service-uri=" + encodeURIComponent(service)
		+ "&query=" + encodeURIComponent(sparqlCounties);

 var query = new google.visualization.Query(queryurl); // Send the query.
 query.send(handleCountyQueryResponse);
} 

function handleCountyQueryResponse(response) {
  if (response.isError()) {
    alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
    return;
  }

	countyData = response.getDataTable();
	var sorted = processCountyData(countyData);
	genCountySelection(sorted);

	//var table = new google.visualization.Table(document.getElementById('ptTable'));
  //table.draw(countyData, {showRowNumber: true});
}

function processCountyData(data) {
	var countyNames = new Array();
	for (var i = 0; i < data.getNumberOfRows(); i++) {
		var curName = data.getValue(i, 0);	
		countyNames.push(curName);		
	}
	countyNames.sort();
	return countyNames;
}

function genCountySelection(data) {
	var selectionId=curDataSource+'_county_selection_canvas';

	county_select = document.getElementById(selectionId);
	county_select.innerHTML="";

	for (var i = 0; i < data.length; i++) {
		append_selection_element(county_select, data[i] , data[i]);
	}

	if(county_select.innerHTML!="")
		county_select.selectedIndex = 0;
	else
		county_select.selectedIndex = -1;
  onchange_county_selection();
}
