
function onchange_county_selection(){
	//var selectionId=curDataSource+'_county_selection_canvas';
	//var curStateIndex=document.getElementById('state_selection_canvas').selectedIndex;
	//curStateAbbr=stateAbbrs[curStateIndex];
	//var curStateCode = stateCodes[curStateIndex];

	var countySelectionId=curDataSource+'_county_selection_canvas';
	var curCountyIndex=document.getElementById(countySelectionId).selectedIndex;
	
	if(curCountyIndex>=0){
		curCountyCode=countyData.getValue(curCountyIndex, 1);	
		//alert(curStateAbbr+','+curCountyCode);
		if(curDataSource=="USGS")
			sendWaterSiteQuery(curStateAbbr, curStateCode, curCountyCode);
		else 
			sendFacilityQuery(curStateAbbr, curCountyCode);
	}
	else {
		//clear site selection
		var siteSelectionId=curDataSource+'_site_selection_canvas';
		var site_select = document.getElementById(siteSelectionId);
		site_select.innerHTML="";
		onchange_site_selection();
	}
}

function sendWaterSiteQuery(stateAbbr, stateCode, countyCode){
	var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlWaterSites="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?siteId\r\n"+
        "WHERE {\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{\r\n"+
        "?site rdf:type epa:MeasurementSite .\r\n"+
        "?site epa:hasStateCode \"" + stateCode + "\" .\r\n"+
        "?site epa:hasCountyCode \"" + countyCode + "\" .\r\n"+
        "?site epa:hasUSGSSiteId ?siteId .\r\n"+
        "}}"

	//alert(sparqlWaterSites);

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlWaterSites),
          dataType: "xml", 
          success: processWaterSitesData,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processWaterSitesData(data) {
  //var site_select = document.getElementById("site_selection_canvas");
	var siteIdArr = new Array();
	var siteSelectionId=curDataSource+'_site_selection_canvas';
	var site_select = document.getElementById(siteSelectionId);
	site_select.innerHTML="";

	$(data).find('result').each(function(){
	var siteIdUri="", siteId="", pat="#SiteId-";
	//var patLen=pat.length;
	//var patIndex;
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="siteId")
	  {
	    siteIdUri=($(this).find("uri").text());
			siteId=USGSSiteUri2Id(siteIdUri, pat);
			if(siteId!="")
				siteIdArr.push(siteId);
			/*
			patIndex=siteIdUri.indexOf(pat);
			if(patIndex!=-1){
				siteId=siteIdUri.substring(patIndex+patLen)
				//alert(siteId);
				siteIdArr.push(siteId);	
			}*/
	  }
	});
	});

	siteIdArr.sort();
	for (var i = 0; i < siteIdArr.length; i++) {
		append_selection_element(site_select, siteIdArr[i] , siteIdArr[i]);
	}

	if(site_select.innerHTML!="")
		site_select.selectedIndex = 0;
	else
		site_select.selectedIndex = -1;
	onchange_site_selection();
}

