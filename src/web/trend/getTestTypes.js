/*function onchange_element_selection(){
	curElementName = element_select.options[0].value;	
}*/


function onchange_element_selection() {
	//var element_selection=document.getElementById('element_selection_canvas');
	var elementSelectionId=curDataSource+'_element_selection_canvas';
	var element_selection=document.getElementById(elementSelectionId);
	var curElementIndex=element_selection.selectedIndex;
	//alert(curElementIndex);
	//clear test types
		if(curDataSource=="EPA") {
			var checkboxId=curDataSource+'_test_type_checkbox';
			var test_type_checkbox=document.getElementById(checkboxId);
			test_type_checkbox.innerHTML = "Test Type: ";

			var limitCheckboxId=curDataSource+'_test_type_limit_checkbox';
			var test_type_limit_checkbox=document.getElementById(limitCheckboxId);
			test_type_limit_checkbox.innerHTML = "Test Type Limit: ";
		}

	if(curElementIndex>=0){
		curElementName = element_selection.options[curElementIndex].value;
		if(curDataSource=="EPA")
			sendEPATestTypeQuery(curStateAbbr, curPermit, curElementName);	
	}
}

function sendEPATestTypeQuery(stateAbbr, permit, elementName){
	var thisserviceagent="http://localhost/demoWater/trendData.php";
	var sparqlEPATestType="PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "PREFIX e1: <http://logd.tw.rpi.edu/source/epa-gov/dataset/enforcement-and-compliance-history-online-echo-measurements/vocab/enhancement/1/>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?testType\r\n"+
        "WHERE {\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/"+stateAbbr+"/"+curDataSource+">\r\n"+
        "{\r\n"+
        "?measure epa:hasPermit <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#FacilityPermit-"+permit+"> .\r\n"+
        "?measure epa:hasElement <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#" + elementName + "> .\r\n"+
        "?measure e1:test_type ?testType\r\n"+
        "}}"
	
	//alert(sparqlEPATestType);

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
	var checkboxId=curDataSource+'_test_type_checkbox';
	var test_type_checkbox=document.getElementById(checkboxId);
	test_type_checkbox.innerHTML = "Test Type: ";
	//limit check box
	var limitCheckboxId=curDataSource+'_test_type_limit_checkbox';
	var test_type_limit_checkbox=document.getElementById(limitCheckboxId);
	test_type_limit_checkbox.innerHTML = "Test Type Limit: ";
	//
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
		box="<INPUT TYPE=\"checkbox\" NAME=\""+checkboxId+"\" value=\""+ testTypesArr[i] + "\">"+ testTypesArr[i] + "&nbsp;";
		//alert(box);
 		test_type_checkbox.innerHTML += box;
		limitBox="<INPUT TYPE=\"checkbox\" NAME=\""+limitCheckboxId+"\" value=\""+ testTypesArr[i] + "\">"+ testTypesArr[i] + "&nbsp;";
		//alert(limitBox);
 		test_type_limit_checkbox.innerHTML += limitBox;
	}
}

/*
function index2NameForTestType(curIndex) {
	return curTestTypes[curIndex];
}*/
