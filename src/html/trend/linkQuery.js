curStateAbbr="";

function init_EPA_linked_from_orgpedia() {
        init_status();
        curDataSource="EPA";
        linkedEPAQueryFromOrgpedia();
}

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
//        document.getElementById("EPA_state_selection_div").style.display = 'none';
//        document.getElementById("EPA_county_selection_div").style.display = 'none';
//        document.getElementById("EPA_site_selection_div").style.display = 'none';

			if(curStateAbbr in echoStates){
				EPADataset="echo";
				EPADataVersion="2011-Mar-19";
			}

			sendFacilityLabelQuery(curStateAbbr, facUrl);
}


function linkedEPAQueryFromOrgpedia() {
	var url=location.href;
	//alert(url);
	var params = parseQuery(url);
	var facUIN = params["facility"];

	if(facUIN== 'undefined'|| facUIN ==""){
		alert("The query url is not well formated! No facility UIN.");
		return;
	}

//	document.getElementById("EPA_state_selection_div").style.display = 'none';
//	document.getElementById("EPA_county_selection_div").style.display = 'none';
//	document.getElementById("EPA_site_selection_div").style.display = 'none';
	//
	queryFacilityState(facUIN);
}

function queryFacilityState(facUIN){
    //var facUIN="110000308523";
    var sparqlFacilityState="prefix local_vocab: <http://tw.rpi.edu/orgpedia/source/epa-gov/dataset/facility-registry-system/vocab/>\r\n"+
				"prefix e1: <http://tw.rpi.edu/orgpedia/source/epa-gov/dataset/facility-registry-system/vocab/enhancement/1/>\r\n"+
				"select distinct ?facState \r\n"+
        "where{\r\n"+
        "graph <http://tw.rpi.edu/orgpedia/source/epa-gov/facility-registry-system/version/2011-Jul-05>\r\n"+
        "{ <http://tw.rpi.edu/orgpedia/source/epa-gov/id/facility/"+facUIN+"> e1:state_code ?facState.\r\n"+
        "}}";

   //alert(sparqlFacilityState);

   var success = function(data) {
   	//var facState="";
        $(data).find('result').each(function(){
           $(this).find("binding").each(function(){
           if($(this).attr("name")=="facState")
           {
              curStateAbbr=($(this).find("literal").text());
	      //alert(curStateAbbr);
           }
           });
        });
       if(curStateAbbr==""){
           //alert("Sorry that we don't have enough information about the facility, in particular the state where the facility locates");
		document.getElementById("EPA_facility_data_status").innerHTML = "<p>We don't have water quality data about the facility.<br> Sorry about that.</p>";
	   	document.getElementById("EPA_facility_label").style.display = 'none';
		document.getElementById("EPA_permit_selection_div").style.display = 'none';
		document.getElementById("EPA_element_selection_div").style.display = 'none';
		document.getElementById("EPA_test_type_selection_div").style.display = 'none';
		document.getElementById("EPA_trend__button_div").style.display = 'none';
       }
       else{
         var facUrl="http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#facility-"+facUIN;
				 curStateAbbr=curStateAbbr.toLowerCase();
         sendFacilityLabelQuery(curStateAbbr, facUrl);
       }
   };

	$.ajax({type: "GET",
          url: orgpediaAgent,
          data: "query="+encodeURIComponent(sparqlFacilityState),
          dataType: "xml",
          error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
            success: success
	});
}

function sendFacilityLabelQuery(stateAbbr, facUrl){
        //var facUrl="http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#facility-"+facUIN;
	var sparqlFacilityLabel="PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "SELECT DISTINCT ?facLabel\r\n"+ 
        "WHERE {\r\n"+        
        //"graph <http://tw2.tw.rpi.edu/water/"+curDataSource+"/"+stateAbbr+">\r\n"+
//http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-ri/version/2011-Mar-19
        "graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-"+stateAbbr+"/version/2011-Mar-19>\r\n"+
        "{\r\n"+
        //"\""+facUrl+"\" rdfs:label ?facLabel .\r\n"+
        "<"+facUrl+"> rdfs:label ?facLabel .\r\n"+
        "}}";

	alert(sparqlFacilityLabel);

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
        document.getElementById("EPA_facility_label").innerHTML += facLabel;
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

	curSiteId=USGSSiteUri2Id(curSiteUri, "Site-");
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

