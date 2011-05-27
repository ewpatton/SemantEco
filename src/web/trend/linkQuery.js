function init_EPA_linked_query() {
	init_status();
	curDataSource="EPA";
	linkedEPAQuery();
}

function init_USGS_linked_query() {
	init_status();
	curDataSource="USGS";
	linkedUSGSQuery();
}

function linkedEPAQuery() {
	var url=location.href;
	//alert(url);
	var params = parseQuery(url);
	curStateAbbr = params["state"];
	var facUrl = params["site"];

	if(curStateAbbr === 'undefined' || curStateAbbr ==""){
		alert("The query url is not well formated! No state.");
		return;
	}
	if(facUrl=== 'undefined'|| facUrl ==""){
		alert("The query url is not well formated! No facility uri.");
		return;
	}

	//alert(curStateAbbr+", "+facUrl);
	document.getElementById("EPA_state_selection_div").style.display = 'none';
	document.getElementById("EPA_county_selection_div").style.display = 'none';
	document.getElementById("EPA_site_selection_div").style.display = 'none';
	//
	sendFacilityLabelQuery(curStateAbbr, facUrl);
}

function sendFacilityLabelQuery(stateAbbr, facUrl){
	var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlFacilityLabel="PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "SELECT DISTINCT ?facLabel\r\n"+ 
        "WHERE {\r\n"+        
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{\r\n"+
        "\""+facUrl+"\" rdfs:label ?facLabel .\r\n"+
        "}}";

	//alert(sparqlFacilityLabel);

	       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlFacilityLabel),
          dataType: "xml", 
          success: processFacilityLabelData,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processFacilityLabelData(data){
	var facLabel="";
	$(data).find('result').each(function(){
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="facLabel")
	  {
	    facLabel=($(this).find("literal").text());
	  }
	});
	});

	if(facLabel==""){
		alert("processFacilityLabelData can't find facility label");
	}
	curSiteId=facLabel;
	//alert(facLabel);
	sendFacilityPermitQuery(curStateAbbr, curSiteId);
}

function linkedUSGSQuery() {
	var url=location.href;
	//alert(url);
	var params = parseQuery(url);
	curStateAbbr = params["state"];
	var curSiteUri = params["site"];
	if(curStateAbbr === 'undefined' || curStateAbbr ==""){
		alert("The query url is not well formated! No state.");
		return;
	}
	if(curSiteUri=== 'undefined'|| curSiteUri ==""){
		alert("The query url is not well formated! No site uri.");
		return;
	}

	curSiteId=USGSSiteUri2Id(curSiteUri, "site-");
	//alert(curStateAbbr+", "+curSiteId);
	document.getElementById("USGS_state_selection_div").style.display = 'none';
	document.getElementById("USGS_county_selection_div").style.display = 'none';
	document.getElementById("USGS_site_selection_div").style.display = 'none';
	//
	sendUSGSElementQuery(curStateAbbr, curSiteId);
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

