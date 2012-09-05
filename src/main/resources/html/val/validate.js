var valServiceagent="http://localhost/trend/trendData.php";
var delta=0.005; //0.005, 0.01
var result;
var pairPt=0;

function getCloseSites() {
  state = $("#state").val();
  county = $("#county").val();
  delta = $("#delta").val();

	//alert(delta);
	$.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"state" : state,
					"county" : county ,
					"delta" : delta,
					"method" : "getCloseSites"
				},
				dataType : "json",
				success : processCloseSites,
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function getCommonElements() {
  state = $("#state").val();
  sitePair = $("#site_selection_canvas").val();

	$.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"state" : state,
					"sitePair" : sitePair ,
					"method" : "getCommonElements"
				},
				dataType : "json",
				success : processCommonElements,
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function getTestTypes() {
  state = $("#state").val();
  sitePair = $("#site_selection_canvas").val();
  element = $("#element_selection_canvas").val();
	$.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"state" : state,
					"sitePair" : sitePair,
					"element" : element,					
					"method" : "getTestTypes"
				},
				dataType : "json",
				success : processTestTypes,
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function getMeasurements() {
  state = $("#state").val();
  sitePair = $("#site_selection_canvas").val();
  element = $("#element_selection_canvas").val();
  testType = $("#testType_selection_canvas").val();

	$.ajax({
				type : "GET",
				url : thisserviceagent,
				data : {
					"state" : state,
					"sitePair" : sitePair,
					"element" : element,			
					"testType" : testType,				
					"method" : "getMeasurements"
				},
				dataType : "json",
				success : processMeasurements,
				error : function(err) {
					window.alert("Unable to retrieve data from the server.");
					$("#spinner").css("display", "none");
				}
			});
}

function processCloseSites(data) {
  var bindings = data.results.bindings;
  var found = {};
  if (bindings.length == 0) {
    window.alert("No data are available for this county at this time.");
  }
  var site_sel = document.getElementById("site_selection_canvas");
	site_sel.innerHTML = "";
	
  for ( var i = 0; i < bindings.length; i++) {
    var result = bindings[i];
    var fac = result["fac"].value;
    var site = result["site"].value;
    //document.getElementById("test").innerHTML +=  fac.replace("<", "").replace(">", "");	
		//document.getElementById("test").innerHTML +=  " , "+ site.replace("<", "").replace(">", "")+" .<br>";	
		append_selection_element(site_sel, fac+", "+site, fac+", "+site);
  }  
}

function processCommonElements(data) {
  var bindings = data.results.bindings;
  if (bindings.length == 0) {
    window.alert("No data are available for this county at this time.");
  }
  var element_sel = document.getElementById("element_selection_canvas");
	element_sel.innerHTML = "";
	
  for ( var i = 0; i < bindings.length; i++) {
    var result = bindings[i];
    var element = result["element"].value;
		//document.getElementById("test").innerHTML +=  element.replace("<", "").replace(">", "")+" .<br>";	
		append_selection_element(element_sel, element, element);
  }  
}

function processTestTypes(data) {
  var bindings = data.results.bindings;
  if (bindings.length == 0) {
    window.alert("No data are available for this county at this time.");
  }
  var testType_sel = document.getElementById("testType_selection_canvas");
	testType_sel.innerHTML = "";
	
  for ( var i = 0; i < bindings.length; i++) {
    var result = bindings[i];
    var testType = result["testType"].value;
		//document.getElementById("test").innerHTML +=  testType.replace("<", "").replace(">", "")+" .<br>";	
		append_selection_element(testType_sel, testType, testType);
  }  
}

function processMeasurements(data){
  var epaRes = {};
	epaRes.dateArr = new Array();
  epaRes.valueArr = new Array();

  var usgsRes = {};
	usgsRes.dateArr = new Array();
  usgsRes.valueArr = new Array();
  
  var bothRes = {};
  bothRes.epa = processMeasurementData(data.epa, epaRes);
  bothRes.usgs= processMeasurementData(data.usgs, usgsRes);
  
  drawDotChart(bothRes);
  //set the title
  document.getElementById('val_visualization_outer').style.display = 'block';
}

function processMeasurementData(data, dataRes) {
//alert("In processUSGSMeasurementData");    
  var bindings = data.results.bindings;
  if (bindings.length == 0) {
    window.alert("No data are available for this county at this time.");
    return dataRes;
  }
	var unit="", once=false;
  for ( var i = 0; i < bindings.length; i++) {
    result = bindings[i];
    dateStr = result["date"].value;
    value = result["value"].value;
    if(once==false){
      unit = result["unit"].value; once=true;
    }
		if(dateStr !=""&&value !=""){
      curDate=new myDate(dateStr);
			dataRes.dateArr.push(curDate);
			dataRes.valueArr.push(parseFloat(value));
		}
  } 
  
  /*
//provide additional information
	var infoId='val_visualization_info';
	var curInfo=document.getElementById(infoId);

	if(result.dateArr.length==0){
		curInfo.innerHTML = "No Data Available.";
	}
	else if(unit !=""){
		curInfo.innerHTML = "Unit: "+unit;
	}
*/
	return dataRes;
}


/*
function val_init_status() {
	echoStateArr = new Array("ca", "ma", "ri", "ny");
	echoStates = oc(echoStateArr);
}

function queryCloseSites(){
var sparqlCloseSites ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n" +
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n" + 
				"prefix wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>\r\n" +
        "SELECT DISTINCT ?fac ?site\r\n"+
        "from <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-facilities-ri/version/2011-Mar-19>\r\n"+
        "from <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-sites-ri/version/2011-Mar-20>\r\n"+
        "WHERE {\r\n"+
        "?fac rdf:type water:WaterFacility . \r\n"+
        "?site rdf:type water:WaterSite . \r\n"+
        "?fac wgs:lat ?facLat.\r\n"+
        "?site wgs:lat ?siteLat.\r\n"+
        "?fac wgs:long ?facLong.\r\n"+
        "?site wgs:long ?siteLong.\r\n"+
        "FILTER ( ?facLat < (?siteLat+"+delta+") && ?facLat > (?siteLat-"+delta+") && ?facLong < (?siteLong+"+delta+") && ?facLong > (?siteLong-"+delta+"))\r\n"+
        "}\r\n";

	alert(sparqlCloseSites);
 
       $.ajax({type: "GET",
          url: valServiceagent,
          data: "query="+encodeURIComponent(sparqlCloseSites),
          dataType: "xml", 
          success: processCloseSites,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
}

function processCloseSites1(data) {
    result = {};
		result.epaFacs = new Array();
    result.usgsSites = new Array();
	$(data).find('result').each(function(){
	var facURI="", siteURI="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="fac")
	  {
	    facURI=($(this).find("uri").text());
			//alert(facURI);
	  }
	  if($(this).attr("name")=="site")
	  {
	    siteURI=($(this).find("uri").text());
			//alert(siteURI);
	  }

	 	if(facURI!=""&&siteURI!=""){
			result.epaFacs.push(facURI);
			result.usgsSites.push(siteURI);
		}
	});
	});

		for (var i = 0; i < result.epaFacs.length; i++){ 
			document.getElementById("test").innerHTML +=  result.epaFacs[i].replace("<", "").replace(">", "");	
			document.getElementById("test").innerHTML +=  " , "+ result.usgsSites[i].replace("<", "").replace(">", "")+" .<br>";	
		}  
//startQueryCommonElements
	document.getElementById("test").innerHTML="";
	//queryCommonElementsForOnePair(result.epaFacs[0], result.usgsSites[0]);
}
*/

//http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#site-USGS-415213071204001
//epa:SiteId-USGS-01109150

function queryCommonElementsForOnePair(epaFacURI, usgsSiteURI){
var sparqlCommonElementsForOnePair ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
				"prefix pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n" +
				"prefix water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n" + 
        "SELECT DISTINCT ?char\r\n"+
        "from <http://sparql.tw.rpi.edu/source/epa-gov/dataset/echo-measurements-ri/version/2011-Mar-19>\r\n"+
        "from <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-ri/version/2011-Mar-20>\r\n"+
        "WHERE {\r\n"+
        //"<http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#facility-110004923058> epa:hasPermit ?permit .\r\n"+
        "<"+epaFacURI+"> epa:hasPermit ?permit .\r\n"+
        "?epaMeasure epa:hasPermit ?permit .\r\n"+
        "?epaMeasure pol:hasCharacteristic ?char .\r\n"+
        //"?usgsMeasure epa:hasUSGSSiteId <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#site-USGS-01116875>.\r\n"+
        "?usgsMeasure epa:hasUSGSSiteId <"+usgsSiteURI.replace("#site", "#SiteId")+">.\r\n"+
        "?usgsMeasure pol:hasCharacteristic ?char .\r\n"+
        "}";

	//alert(sparqlCommonElementsForOnePair);
 
       $.ajax({type: "GET",
          url: valServiceagent,
          data: "query="+encodeURIComponent(sparqlCommonElementsForOnePair),
          dataType: "xml", 
          success: processCommonElementsForOnePair,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
}

function processCommonElementsForOnePair(data){
	var commonElements=new Array();
	$(data).find('result').each(function(){
	var elementURI="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="element")
	  {
	    elementURI=($(this).find("uri").text());
			//alert(elementURI);
			commonElements.push(elementURI);
	  }
	});
	});

	pairPt++;
  
	if(pairPt<result.epaFacs.length){
		if(commonElements.length!=0){
			document.getElementById("test").innerHTML += result.epaFacs[pairPt-1]+", "+result.usgsSites[pairPt-1]+"<br>";
			for(var i=0; i<commonElements.length; i++){
				document.getElementById("test").innerHTML +=  commonElements[i].replace("<", "").replace(">", "")+"<br>";
			}
			document.getElementById("test").innerHTML += "<br>";
		}
		queryCommonElementsForOnePair(result.epaFacs[pairPt], result.usgsSites[pairPt]);
	}
}




