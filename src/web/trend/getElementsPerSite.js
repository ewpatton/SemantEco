
/*
function onchange_site_selection(){
	var site_selection=document.getElementById('site_selection_canvas')
	var curSiteIndex=site_selection.selectedIndex;
	var curSiteId = site_selection.options[curSiteIndex].value;
	//alert(curSiteId);
	if(curDataSource=="USGS")
		sendUSGSElementQuery(curStateAbbr, curSiteId);
	else 
		//sendEPAElementQuery(curStateAbbr, curSiteId);
}
*/

function sendUSGSElementQuery(stateAbbr, siteId){
	var sparqlUSGSElements = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?characteristic\r\n"+
        "WHERE {\r\n"+
//        "graph <http://tw2.tw.rpi.edu/water/"+curDataSource+"/"+stateAbbr+">\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-"+stateAbbr+"/version/2011-Mar-20>\r\n"+
        "{\r\n"+
        "?measure rdf:type water:WaterMeasurement .\r\n"+
        "?measure pol:hasSiteId \"http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#" + siteId + "\" .\r\n"+
        "?measure pol:hasCharacteristic ?characteristic .\r\n"+
        "}}"

	alert(sparqlUSGSElements);
 
       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlUSGSElements),
          dataType: "xml", 
          success: processElementData,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
} 

function sendEPAElementQuery(stateAbbr, permit){
	var sparqlEPAElements="PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?characteristic\r\n"+
        "WHERE {\r\n"+
        //"graph <http://tw2.tw.rpi.edu/water/"+curDataSource+"/"+stateAbbr+">\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/version/2011-Mar-19>\r\n"+
        "{\r\n"+
        "?measure pol:hasPermit <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-"+permit+"> .\r\n"+
        "?measure pol:hasCharacteristic ?characteristic .\r\n"+
        "}}";

	alert(sparqlEPAElements);

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlEPAElements),
          dataType: "xml", 
          success: processElementData,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
} 


function processElementData(data) {
  //var element_select = document.getElementById("element_selection_canvas");
	var elementSelectionId=curDataSource+'_element_selection_canvas';
	var element_select = document.getElementById(elementSelectionId);
	element_select.innerHTML="";
	var elementArr = new Array();

	$(data).find('result').each(function(){
	var elementUri="", elementName="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="characteristic")
	  {
	    elementUri=($(this).find("uri").text());
			elementName=elementUri.substring(elementUri.indexOf("#")+1)
			//alert(elementName);
			elementArr.push(elementName);
	  }
	});
	});
	elementArr.sort();
	for (var i = 0; i < elementArr.length; i++) {
		append_selection_element(element_select, elementArr[i] , elementArr[i]);
	}

	if(element_select.innerHTML!="")
		element_select.selectedIndex = 0;
	else
		element_select.selectedIndex = -1;

	onchange_element_selection();
}
