/*function onchange_element_selection(){
	curElementName = element_select.options[0].value;	
}*/


function onchange_element_selection() {
	var elementSelectionId=curDataSource+'_element_selection_canvas';
	var element_selection=document.getElementById(elementSelectionId);
	var curElementIndex=element_selection.selectedIndex;
	//alert(curElementIndex);
	//clear test types
	/*
		if(curDataSource=="EPA") {
			var checkboxId=curDataSource+'_test_type_checkbox';
			var test_type_checkbox=document.getElementById(checkboxId);
			test_type_checkbox.innerHTML = "Test Type: ";

			var limitCheckboxId=curDataSource+'_test_type_limit_checkbox';
			var test_type_limit_checkbox=document.getElementById(limitCheckboxId);
			test_type_limit_checkbox.innerHTML = "Test Type Limit: ";
		}
	*/
	//clear test types
	if(curDataSource=="EPA") {
		var testTypeselectionId=curDataSource+'_test_type_selection_canvas';
		var testTypeSelection=document.getElementById(testTypeselectionId);
		testTypeSelection.innerHTML="";
		//onchange_test_type_selection();
	}

	if(curElementIndex>=0){
		curElementName = element_selection.options[curElementIndex].value;
		if(curDataSource=="EPA")
			sendEPATestTypeQuery(curStateAbbr, curPermit, curElementName);	
	}
}

function genEPATestTypeQuery(stateAbbr, permit, elementName){
  var sparqlEPATestType="";
	if(EPADataset=="foia")
		sparqlEPATestType+="PREFIX water: <http://escience.rpi.edu/ontology/semanteco/2/0/water.owl#>\r\n";
  else//echo
		sparqlEPATestType+="PREFIX e1: <"+datahost+"/source/epa-gov/dataset/echo-measurements-"+stateAbbr+"/vocab/enhancement/1/>\r\n";
	//the common body
	sparqlEPATestType+="PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "SELECT DISTINCT ?testType\r\n"+
        "WHERE {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/epa-gov/dataset/"+EPADataset+"-measurements-"+stateAbbr+"/version/"+EPADataVersion+">\r\n"+
        "{\r\n"+
        "?measure pol:hasPermit <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#FacilityPermit-"+permit+"> .\r\n"+
        "?measure pol:hasCharacteristic <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#" + elementName + "> .\r\n";
	//the final
	if(EPADataset=="foia")
		sparqlEPATestType+="?measure water:hasValueTypeCode ?testType\r\n"+"}}";
  else//echo
		sparqlEPATestType+="?measure e1:test_type ?testType\r\n"+"}}";

 return sparqlEPATestType;
}

function sendEPATestTypeQuery(stateAbbr, permit, elementName){
	var sparqlEPATestType = genEPATestTypeQuery(stateAbbr, permit, elementName);
	
	alert(sparqlEPATestType);

       $.ajax({type: "GET",
          url: thisserviceagent,
          data: "query="+encodeURIComponent(sparqlEPATestType),
          dataType: "xml", 
          success: processTestTypeData,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
}

function processTestTypeData(data) {
	if(EPADataset=="foia")
		processTestTypeDataFOIA(data);
	else
		processTestTypeDataECHO(data);
}

function processTestTypeDataFOIA(data) {
	var selection_id=curDataSource+'_test_type_selection_canvas';
	var test_type_select = document.getElementById(selection_id);
	var testType="";
	var testTypesArr = new Array();

	$(data).find('result').each(function(){
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="testType")
	    testType=($(this).find("literal").text());
		if(testType!="")
			testTypesArr.push(testType);
	});
	});
	testTypesArr.sort();
	//alert(testTypesArr.length);
	for (var i = 0; i < testTypesArr.length; i++) {
		append_selection_element(test_type_select, testTypesArr[i] , testTypesArr[i]);
	}
	if(test_type_select.innerHTML!="")
		test_type_select.selectedIndex = 0;
	else
		test_type_select.selectedIndex = -1;

	onchange_test_type_selection();
}

function processTestTypeDataECHO(data) {
	/*
	//test type check box
	var checkboxId=curDataSource+'_test_type_checkbox';
	var test_type_checkbox=document.getElementById(checkboxId);
	test_type_checkbox.innerHTML = "Test Type: ";
	//limit check box
	var limitCheckboxId=curDataSource+'_test_type_limit_checkbox';
	var test_type_limit_checkbox=document.getElementById(limitCheckboxId);
	test_type_limit_checkbox.innerHTML = "Test Type Limit: ";
	*/
	var selection_id=curDataSource+'_test_type_selection_canvas';
	var test_type_select = document.getElementById(selection_id);
	//test_type_select.innerHTML="";
	var testTypeUri="", testType="", pat="typed/test/";
	var box = "";
	var patLen=pat.length;
	var patIndex;
	var testTypesArr = new Array();

	$(data).find('result').each(function(){
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="testType")
	  {
//http://logd.tw.rpi.edu/source/epa-gov/dataset/enforcement-and-compliance-history-online-echo-measurements/typed/test/C3
	    testTypeUri=($(this).find("uri").text());
			patIndex=testTypeUri.indexOf(pat);
			if(patIndex!=-1){
				testType=testTypeUri.substring(patIndex+patLen);
				//alert(testType);
				testTypesArr.push(testType);
			}
	  }
	});
	});
	testTypesArr.sort();
	//alert(testTypesArr.length);
	for (var i = 0; i < testTypesArr.length; i++) {
		append_selection_element(test_type_select, testTypesArr[i] , testTypesArr[i]);
	}
	if(test_type_select.innerHTML!="")
		test_type_select.selectedIndex = 0;
	else
		test_type_select.selectedIndex = -1;

	onchange_test_type_selection();

	/*
	//create the checkboxes 
	for (var i = 0; i < testTypesArr.length; i++) {
		box="<INPUT TYPE=\"checkbox\" NAME=\""+checkboxId+"\" value=\""+ testTypesArr[i] + "\">"+ testTypesArr[i] + "&nbsp;";
		//alert(box);
 		test_type_checkbox.innerHTML += box;
		limitBox="<INPUT TYPE=\"checkbox\" NAME=\""+limitCheckboxId+"\" value=\""+ testTypesArr[i] + "\">"+ testTypesArr[i] + "&nbsp;";
		//alert(limitBox);
 		test_type_limit_checkbox.innerHTML += limitBox;
	}
	*/
}

/*
function index2NameForTestType(curIndex) {
	return curTestTypes[curIndex];
}*/
