var selectedTestType=undefined;

function onchange_test_type_selection(){
	var testTypeselectionId=curDataSource+'_test_type_selection_canvas';
	var testTypeSelection=document.getElementById(testTypeselectionId);
	var curTestTypeIndex=testTypeSelection.selectedIndex;

	if(curTestTypeIndex>=0){
		selectedTestType = testTypeSelection.options[curTestTypeIndex].value;
		//alert('selectedTestType: '+selectedTestType);
	}
}

function showMeasurementTrend(){
	if(curStateAbbr==""){
			alert("State selection is empty");
			return;
	}
	if(curSiteId==""){
			alert("Site/Facility selection is empty");
			return;
	}
	if(curElementName==""){
			alert("Element selection is empty");
			return;
	}

	document.getElementById(curDataSource+"_visualization_outer").style.display = 'none';
	if(curDataSource=="USGS")
		sendUSGSMeasurementQuery(curStateAbbr, curSiteId, curElementName);	
	else 
		sendEPAMeasurementQuery(curStateAbbr, curPermit, curElementName);		
}

function sendUSGSMeasurementQuery(stateAbbr, siteId, elementName){
	//alert(stateAbbr);
	var sparqlWaterMeasurements="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n"+
        "PREFIX repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#>\r\n"+
        "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?date ?value ?unit\r\n"+
        "WHERE {\r\n"+
				//"graph <http://tw2.tw.rpi.edu/water/"+curDataSource+"/"+stateAbbr+">\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/usgs-gov/dataset/nwis-measurements-"+stateAbbr+"/version/2011-Mar-20>\r\n"+
        "{\r\n"+
        "?measure rdf:type water:WaterMeasurement .\r\n"+
        "?measure pol:hasSiteId \"http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#" + siteId + "\" .\r\n"+
        "?measure pol:hasCharacteristic <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#" + elementName + "> .\r\n"+
        "?measure time:inXSDDateTime ?date .\r\n"+
        "?measure pol:hasValue ?value .\r\n"+
        "?measure repr:hasUnit ?unit .\r\n"+
        "}} ORDER BY ?date"

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlWaterMeasurements),
          dataType: "xml", 
          success: drawWaterMeasurementVisualization,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function genEPAMeasurementQuery(stateAbbr, permit, elementName){
	var sparqlEPAMeasurements="";
	if(EPADataset=="foia")
		sparqlEPAMeasurements+="PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n";
  else//echo
		sparqlEPAMeasurements+="PREFIX e1: <"+datahost+"/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/vocab/enhancement/1/>\r\n";
	//the common body
	sparqlEPAMeasurements+="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+        
        "PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+ 
        "PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "PREFIX repr: <http://sweet.jpl.nasa.gov/2.1/repr.owl#>\r\n"+
        "SELECT DISTINCT ?date ?value ?unit ?limit_value ?limit_operator ?limit_type \r\n"+ 
        "WHERE {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/"+EPADataset+"-measurements-"+stateAbbr+"/version/"+EPADataVersion+">\r\n"+
        "{\r\n"+
        "?measure pol:hasPermit <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-"+permit+"> .\r\n"+
        "?measure pol:hasCharacteristic <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#" + elementName + "> .\r\n"+
        "?measure dcterms:date ?date .\r\n"+
        "?measure repr:hasUnit ?unit .\r\n"+
        "?measure pol:hasLimitValue ?limit_value .\r\n"+
        "?measure pol:hasLimitOperator ?limit_operator .\r\n";
	//the final
	if(EPADataset=="foia")
		sparqlEPAMeasurements+="?measure pol:hasStatisticalBaseDesc ?limit_type.\r\n"+
					"OPTIONAL {?measure pol:hasValue ?value .\r\n"+
					"?measure water:hasValueTypeCode \""+selectedTestType+"\" .}}} ORDER BY ?date\r\n";
  else//echo
		sparqlEPAMeasurements+= "?measure pol:hasLimitType ?limit_type .\r\n"+
"?measure rdf:value ?value .\r\n"+
"?measure e1:test_type <"+datahost+"/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/typed/test/"+selectedTestType+"> .}} ORDER BY ?date\r\n";

	return sparqlEPAMeasurements;
}

function sendEPAMeasurementQuery(stateAbbr, permit, elementName){
	//alert(stateAbbr);
	if(selectedTestType===undefined){
		alert("Please select a test type");
		return;
	}
	var sparqlEPAMeasurements=genEPAMeasurementQuery(stateAbbr, permit, elementName);

	alert(sparqlEPAMeasurements);	
	//document.getElementById("test").innerHTML+=sparqlEPAMeasurements;
       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlEPAMeasurements),
          dataType: "xml", 
          success: drawWaterMeasurementVisualization,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}


function drawWaterMeasurementVisualization(data){
	if(curDataSource=="USGS"){
 		var extracted = processUSGSMeasurementData(data);
		drawDotChart(curDataSource, extracted);
	}
	else {	
		var extracted =processEPAMeasurementData(data);
		drawDotChart(curDataSource, extracted);
	}
		//set the title
		document.getElementById(curDataSource+'_visualization_outer').style.display = 'block';
}


function processUSGSMeasurementData(data) {
//alert("In processUSGSMeasurementData");
    var result = {};
		result.dateArr = new Array();
    result.valueArr = new Array();

	var unit="", once=false;
	$(data).find('result').each(function(){
	var dateStr="", value="";
	$(this).find("binding").each(function(){
		var row  = Array();
	  if($(this).attr("name")=="date")
	  {
	    dateStr=($(this).find("literal").text()); 
	  }
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text()); 
	  }
	  if(once==false&&$(this).attr("name")=="unit")
	  {
	    unit=($(this).find("literal").text()); 
			once=true;
	  }
		if(dateStr !=""&&value !=""){
			//alert(dateTime+", "+parseFloat(value));
      var curDate=new myDate(dateStr);
			result.dateArr.push(curDate);
			result.valueArr.push(parseFloat(value));
		}
	});
	});

//provide additional information
	var infoId=curDataSource+'_visualization_info';
	var curInfo=document.getElementById(infoId);

	if(result.dateArr.length==0){
		curInfo.innerHTML = "No Data Available.";
	}
	else if(unit !=""){
		curInfo.innerHTML = "Unit: "+unit;
	}

	return result;
}


function processEPAMeasurementData(data) {
    var result = {};
		result.dateArr = new Array();
    result.valueArr = new Array();
    result.limitValueArr = new Array();

	var unit="", limitOperator="", limitType="";
	$(data).find('result').each(function(){
	var dateStr="", value="", limitValue="";

	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="date")
	  {
	    dateStr=($(this).find("literal").text()); 
	  }
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text()); 
	  }
	  if($(this).attr("name")=="unit")
	  {
	    //unit=($(this).find("literal").text()); 
			unit=($(this).find("uri").text()); 
	  }
	  if($(this).attr("name")=="limit_value")
	  {
	    limitValue=($(this).find("literal").text()); 
	  }
	  if($(this).attr("name")=="limit_operator")
	  {
	    limitOperator=($(this).find("literal").text()); 
	  }
	  if($(this).attr("name")=="limit_type")
	  {
	    limitType=($(this).find("literal").text()); 
	  }

		if(dateStr !=""&&value !=""&&limitValue!=""&&unit !="" && limitOperator!="" && limitType !=""){
			//alert(dateTime+", "+parseFloat(value));
      var curDate=new myDate(dateStr);
			result.dateArr.push(curDate);
			result.valueArr.push(parseFloat(value));
			result.limitValueArr.push(parseFloat(limitValue));
		}
	});
	});

//provide additional information
	var infoId=curDataSource+'_visualization_info';
	var curInfo=document.getElementById(infoId);

	if(result.dateArr.length==0){
		curInfo.innerHTML = "No Data Available.";
	}
	else if(unit !="" && limitOperator!="" && limitType !=""){
		curInfo.innerHTML = "Unit: "+unit+", Limit Operator: "+limitOperator+", Limit Type: "+limitType;
		result.limitOperator=limitOperator;
	}

	return result;
}


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




