function onchange_site_selection(){
	//var site_selection=document.getElementById('site_selection_canvas');
	var siteSelectionId=curDataSource+'_site_selection_canvas';
	var site_selection=document.getElementById(siteSelectionId);
	var curSiteIndex=site_selection.selectedIndex;
	//var site_selection=document.getElementById('site_selection_canvas');
	//var curFacIndex=site_selection.selectedIndex;
	//var curFacLabel = site_selection.options[curFacIndex].value;

	if(curSiteIndex>=0){
		curSiteId = site_selection.options[curSiteIndex].value;
		if(curDataSource=="USGS")
			sendUSGSElementQuery(curStateAbbr, curSiteId);
		else 
			sendFacilityPermitQuery(curStateAbbr, curSiteId);
	}
	else {
		//clear element if usgs
		if(curDataSource=="USGS"){
			var elementSelectionId=curDataSource+'_element_selection_canvas';
			var element_select = document.getElementById(elementSelectionId);
			element_select.innerHTML="";
			onchange_element_selection();
		}
		//clear permit if epa
		else {
			var permitSelectionId=curDataSource+'_permit_selection_canvas';
			var permit_select=document.getElementById(permitSelectionId);
			permit_select.innerHTML="";
			onchange_permit_selection();
		}
	}

}

function onchange_permit_selection(){
	//var permit_selection=document.getElementById('permit_selection_canvas');
	var permitSelectionId=curDataSource+'_permit_selection_canvas';
	var permit_selection=document.getElementById(permitSelectionId);
	var curPermitIndex=permit_selection.selectedIndex;
	if(curPermitIndex>=0){
		curPermit = permit_selection.options[curPermitIndex].value;
		if(curDataSource=="EPA")
			sendEPAElementQuery(curStateAbbr, curPermit);
		else 
			alert("Error: Current Data Source is USGS, so should not call onchange_permit_selection!");
	}
  else{
			//clear element selection
			var elementSelectionId=curDataSource+'_element_selection_canvas';
			var element_select = document.getElementById(elementSelectionId);
			element_select.innerHTML="";
			onchange_element_selection();
	}
}

function sendFacilityPermitQuery(stateAbbr, facLabel){
	//var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlFacilityPermits="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?permit\r\n"+
        "WHERE {\r\n"+
        //"graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+curDataSource+"/"+stateAbbr+">\r\n"+
        "{\r\n"+
        "?fac rdf:type epa:Facility .\r\n"+
        "?fac epa:hasPermit ?permit .\r\n"+
        "?fac rdfs:label \""+facLabel+"\" .\r\n"+
        //"?fac rdfs:label \"BRISTOL WPCF\" .\r\n"+
        "}}";

	//alert(sparqlFacilityPermits);
	//document.getElementById("test").innerHTML+=sparqlFacilityPermits;

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlFacilityPermits),
          dataType: "xml", 
          success: processFacilityPermitData,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processFacilityPermitData(data) {
  //var permit_select = document.getElementById("EPA_permit_selection_canvas");
	var permitSelectionId=curDataSource+'_permit_selection_canvas';
	var permit_select=document.getElementById(permitSelectionId);
	permit_select.innerHTML="";
	var permitArr = new Array();

	$(data).find('result').each(function(){
	var permitUri="", permit="", pat="#FacilityPermit-";
	var patLen=pat.length;
	var patIndex;
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="permit")
	  {
	    permitUri=($(this).find("uri").text());
			patIndex=permitUri.indexOf(pat);
			if(patIndex!=-1){
				permit=permitUri.substring(patIndex+patLen)
				//alert(permit);
				permitArr.push(permit);				
			}
	  }
	});
	});
	permitArr.sort();
	for (var i = 0; i < permitArr.length; i++) {
		append_selection_element(permit_select, permitArr[i] , permitArr[i]);
	}

	if(permit_select.innerHTML!="")
		permit_select.selectedIndex = 0;
	else
		permit_select.selectedIndex = -1;
	onchange_permit_selection();
}
