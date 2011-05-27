

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

	//document.getElementById(curDataSource+"_visualizationTitle").style.display = 'none';
	document.getElementById(curDataSource+"_visualization_outer").style.display = 'none';
	if(curDataSource=="USGS")
		sendUSGSMeasurementQuery(curStateAbbr, curSiteId, curElementName);	
	else 
		sendEPAMeasurementQuery(curStateAbbr, curPermit, curElementName);		
}

function sendUSGSMeasurementQuery(stateAbbr, siteId, elementName){
	//alert(stateAbbr);
	var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlWaterMeasurements="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?date ?value ?unit\r\n"+
        "WHERE {\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{\r\n"+
        "?measure rdf:type epa:WaterMeasurement .\r\n"+
        "?measure epa:hasUSGSSiteId <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#SiteId-" + siteId + "> .\r\n"+
        "?measure epa:hasElement <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#" + elementName + "> .\r\n"+
        "?measure time:inXSDDateTime ?date .\r\n"+
        "?measure epa:hasValue ?value .\r\n"+
        "?measure epa:hasUnit ?unit .\r\n"+
        "}}"

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

function buildEPAMeasurementSparql(stateAbbr, permit, elementName) {
	selectedTestTypes = new Array();
	selectedTestLimits = new Array();
	var test_type_checkbox = document["test_type_form"]["EPA_test_type_checkbox"];
 	var test_type_limit_checkbox = document["test_type_limt_form"]["EPA_test_type_limit_checkbox"];

	//var checkboxId=curDataSource+'_test_type_checkbox';
	//var test_type_checkbox=document.getElementById(checkboxId);
	//var limitCheckboxId=curDataSource+'_test_type_limit_checkbox';
	//var test_type_limit_checkbox=document.getElementById(limitCheckboxId);

	var sparqlPart="", sparqlStat="";
	var sparqlBefore="{\r\n"+
        "?measure epa:hasPermit <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#FacilityPermit-"+permit+"> .\r\n"+
        "?measure epa:hasElement <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#" + elementName + "> .\r\n"+
        "?measure dcterms:date ?date .\r\n"+
        "?measure rdf:value ?value .\r\n"+
        "?measure epa:hasUnit ?unit .\r\n"+
        "?measure epa:hasLimitValue ?limit_value .\r\n"+
        "?measure epa:hasLimitOperator ?limit_operator .\r\n"+
        "?measure epa:hasLimitType ?limit_type .\r\n"+
        "?measure e1:test_type ?test_type .\r\n";

	//alert(test_type_checkbox.length);

	if (typeof test_type_checkbox.length === 'undefined') {
  	/*then there is just one checkbox with no array*/
  	if (test_type_checkbox.checked == true )
			//alert(test_type_checkbox.value);	
			selectedTestTypes.push(test_type_checkbox.value);
	}
	else {
		for(var i=0; i<test_type_checkbox.length; i++){
			if(test_type_checkbox[i].checked == true){
				//alert(test_type_checkbox[i].value);			
				selectedTestTypes.push(test_type_checkbox[i].value);
			}
		}
	}

	if (typeof test_type_limit_checkbox.length === 'undefined') {
  	/*then there is just one checkbox with no array*/
  	if (test_type_limit_checkbox.checked == true )
			//alert(test_type_limit_checkbox.value);	
			selectedTestLimits.push(test_type_limit_checkbox.value);
	}
	else {
		for(var i=0; i<test_type_limit_checkbox.length; i++){
			if(test_type_limit_checkbox[i].checked == true){
				//alert(test_type_limit_checkbox[i].value);			
				selectedTestLimits.push(test_type_limit_checkbox[i].value);
			}
		}
	}
	
 	var allSelected = selectedTestTypes.concat(selectedTestLimits);
	var selected = eliminateDuplicates(allSelected);

	if(selected.length == 0)
		return "";

	for(var i=0; i<selected.length; i++){
			sparqlPart = sparqlBefore+"?measure e1:test_type <http://logd.tw.rpi.edu/source/epa-gov/dataset/enforcement-and-compliance-history-online-echo-measurements/typed/test/"+selected[i]+"> .}\r\n"
		if(i==0)
			sparqlStat = sparqlPart; 
		else
			sparqlStat += " UNION\r\n" + sparqlPart;
	}
	sparqlStat+="}}\r\n";
	return sparqlStat;
}

function sendEPAMeasurementQuery(stateAbbr, permit, elementName){
	//alert(stateAbbr);
	var thisserviceagent="http://localhost/demoWater/trendData.php";
	var epaMeasurementByTestTypes=buildEPAMeasurementSparql(stateAbbr, permit, elementName);
  if(epaMeasurementByTestTypes==""){
		alert("Test Type and Test Type Limit are not selected!");
		return;
	}

	var sparqlEPAMeasurements="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+ 
        "PREFIX e1: <http://logd.tw.rpi.edu/source/epa-gov/dataset/enforcement-and-compliance-history-online-echo-measurements/vocab/enhancement/1/>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?test_type ?date ?value ?unit ?limit_value ?limit_operator ?limit_type \r\n"+ 
        "WHERE {\r\n"+
        //"graph <http://tw2.tw.rpi.edu/water/RI/EPA>\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{" + epaMeasurementByTestTypes;

	//alert(sparqlEPAMeasurements);	
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
	//usgsUnit="";
	var table;

	if(curDataSource=="USGS")
 		table = processUSGSMeasurementData(data);
	else {	
		var processed =processEPAMeasurementData(data);
		table = buildEPAMeasurementTable(processed);
	}

	var chartOptions = {};
  chartOptions['title']= curElementName;// +" in " + usgsUnit;
  chartOptions['legend'] = 'bottom';
  chartOptions['legendFontSize'] = 16;
  chartOptions['legendPosition'] = 'newRow';  

//set the title
	//var titleID=curDataSource+'_visualizationTitle';
	//document.getElementById(titleID).style.display = 'block';
//
	var visualizationId=curDataSource+'_visualization';
	document.getElementById(visualizationId+'_outer').style.display = 'block';
	//visualization = new google.visualization.LineChart(document.getElementById(visualizationId));
  //visualization.draw(table, chartOptions);
	visualization = new google.visualization.AnnotatedTimeLine(document.getElementById(visualizationId));
	visualization.draw(table, chartOptions);

}

function preProcessUSGSMeasurementData(data){
	var usgsUnit="";
	var once=false;
	$(data).find('result').each(function(){
	var dateStr="", value="";
	$(this).find("binding").each(function(){		
	  if(once==false&&$(this).attr("name")=="unit")
	  {
	    usgsUnit=($(this).find("literal").text()); 
			if(usgsUnit!="")
					once=true;
	  }
	});
	});
	return usgsUnit;
}

function processUSGSMeasurementData(data) {
    table = new google.visualization.DataTable();
    //table.addColumn('string', 'Date');
		table.addColumn('datetime', 'Date');
		var columnLable = 'USGS Measurement';
		var usgsUnit=preProcessUSGSMeasurementData(data);
		if(usgsUnit!="")
				columnLable += ', Unit: '+usgsUnit;
		table.addColumn('number', columnLable);
    //table.addColumn('number', 'Measurement Value');

	$(data).find('result').each(function(){
	var dateStr="", value="";
	var once=false;
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
	    usgsUnit=($(this).find("literal").text()); 
			once=true;
	  }
		if(dateStr !=""&&value !=""){
			//alert(dateTime+", "+parseFloat(value));
			//row.push(dateStr);
      var curDate=new myDate(dateStr);
			row.push(curDate);
			//row.push(curDate);
			row.push(parseFloat(value));
			table.addRow(row);
		}
	});
	});

	return table;
}


function processEPAMeasurementData(data) {
    var result = {};
    result.valuesByDate = new Array();
    result.limitValuesByDate = new Array();
    result.testTypes = new Array();
		result.unitArr = new Array();
		result.limitOperatorArr = new Array();
		result.limitTypeArr = new Array();
		var testTypeUri="", testType="", dateStr="", value="", unit="", limitValue="", limitOperator="", limitType="";
		var testTypePat="typed/test/";
		var testTypePatLen=testTypePat.length;
		var testTypePatIndex;
	//var once=false;
	$(data).find('result').each(function(){
	$(this).find("binding").each(function(){
		var row  = Array();
	  if($(this).attr("name")=="test_type")
	  {
	    testTypeUri=($(this).find("uri").text()); 
			testTypePatIndex=testTypeUri.indexOf(testTypePat);
			if(testTypePatIndex!=-1)
				testType=testTypeUri.substring(testTypePatIndex+testTypePatLen);
	  }
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
	    unit=($(this).find("literal").text()); 
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
		
		if(testTypeUri !=""&&dateStr !=""&&value !=""&&unit !=""){
			//alert(testType+','+dateStr+','+parseFloat(value)+','+unit+','+limitType);
			result.unitArr[testType]=unit;
			result.testTypes[testType] = testType;
			//values and limit values
    	if (result.valuesByDate[dateStr] == null) {
        var _values = new Array();
        result.valuesByDate[dateStr] = _values;
        _values[testType] = value;
    	} else {
        _values = result.valuesByDate[dateStr];
        _values[testType] = value;
    	}		
		}//end of if the strings are not empty	
		if(testTypeUri !="" && limitValue!="" && limitOperator!="" && limitType !=""){
			result.limitOperatorArr[testType]=limitOperator;
			result.limitTypeArr[testType]=limitType;
			//limit values
   	 	if (result.limitValuesByDate[dateStr] == null) {
        var _limitValues = new Array();
        result.limitValuesByDate[dateStr] = _limitValues;
        _limitValues[testType] = limitValue;
    	} else {
        _limitValues = result.limitValuesByDate[dateStr];
        _limitValues[testType] = limitValue;
    	}	
		}//end of if the strings are not empty	
	});
	});

   return result; 
 }

function buildEPAMeasurementTable(data) {
    table = new google.visualization.DataTable();
    //table.addColumn('string', 'Date');
		table.addColumn('date', 'Date');
		var columnLable ="";
//selectedTestTypes
    //for (var testType in selectedTestTypes){
		for (var i = 0; i < selectedTestTypes.length; i++) {
				var testType = selectedTestTypes[i];
				columnLable = 'EPA ' + testType;
				if(data.unitArr[testType]!= undefined)
					columnLable += ', Unit: ' +data.unitArr[testType];
				//if(data.limitTypeArr[testType]!= undefined)
				//	columnLable += ', Limit type: ' +data.limitTypeArr[testType];
			table.addColumn('number', columnLable);				
        //table.addColumn('number', testType);
    }

//selectedTestLimitss
   // for (var testLimit in selectedTestLimits){
		for (var i = 0; i < selectedTestLimits.length; i++) {
				var testLimit = selectedTestLimits[i];
				columnLable = 'EPA Limit ' + testLimit;
				if(data.limitOperatorArr[testLimit]!= undefined)
					columnLable += ', Limit Operator: ' +data.limitOperatorArr[testLimit];
				if(data.limitTypeArr[testLimit]!= undefined)
					columnLable += ', Limit type: ' +data.limitTypeArr[testLimit];
			table.addColumn('number', columnLable);				
        //table.addColumn('number', testType);
    }

    var valuesByDate = data.valuesByDate;
		var limitValuesByDate = data.limitValuesByDate;
    for (var dateStr in valuesByDate) {
      var row  = Array();
      var curDate=new myDate(dateStr);
			//row.push(curDate.format("yyyy-mm-dd"));
      row.push(curDate);
      //for (var testType in data.testTypes) {
      for (var i = 0; i < selectedTestTypes.length; i++) {
				var testType = selectedTestTypes[i];
      	var testTypeValues = valuesByDate[dateStr]
       	if (testTypeValues[testType] == null)  
        	row.push(undefined);
        else {
        	row.push(parseFloat(testTypeValues[testType]));
        }
			}
			//limit values
			for (var i = 0; i < selectedTestLimits.length; i++) {
				var testLimit = selectedTestLimits[i];
				var testLimitValues = limitValuesByDate[dateStr]
            if (testLimitValues[testLimit] == null)  
                row.push(undefined);
            else {
                row.push(parseFloat(testLimitValues[testLimit]));
            }
        }
        table.addRow(row);
    }
    //table.sort([{column: 0}, {column: 1}]);
    return table;
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



