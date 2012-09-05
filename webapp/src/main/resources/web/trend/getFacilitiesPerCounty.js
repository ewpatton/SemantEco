function sendFacilityQuery(stateAbbr, countyCode){
	//var thisserviceagent="http://localhost/demoWater/trendData.php";
	var paddedCountyCode=stateAbbr+PadNumber(countyCode, 3);
	//alert(paddedCountyCode);
	var sparqlFacilities="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?lable\r\n"+
        "WHERE {\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{\r\n"+
        "?fac rdf:type epa:Facility .\r\n"+
        "?fac epa:hasStateCode \"" + stateAbbr + "\" .\r\n"+
        "?fac epa:hasCountyCode \"" + paddedCountyCode + "\" .\r\n"+
        "?fac rdfs:label ?lable .\r\n"+
        "}}"

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlFacilities),
          dataType: "xml", 
          success: processFacilityData,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}


function processFacilityData(data) {
	var siteSelectionId=curDataSource+'_site_selection_canvas';
	var site_select = document.getElementById(siteSelectionId);
	site_select.innerHTML="";
	var facLabelArr = new Array();

	$(data).find('result').each(function(){
	var facLabel="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="lable")
	  {
	    facLabel=($(this).find("literal").text());
			if(facLabel!="")
				facLabelArr.push(facLabel);
	  }
	});
	});

	facLabelArr.sort();
	for (var i = 0; i < facLabelArr.length; i++) {
		append_selection_element(site_select, facLabelArr[i] , facLabelArr[i]);
	}

	if(site_select.innerHTML!="")
		site_select.selectedIndex = 0;
	else
		site_select.selectedIndex = -1;
	onchange_site_selection();
}

function PadNumber(number, width) {
	var padded=number.toString();
  var pad="";
	var dist=width-padded.length;
  for(var i=0; i < dist;i++)
       pad = "0"+pad;

  return pad+padded;
}

