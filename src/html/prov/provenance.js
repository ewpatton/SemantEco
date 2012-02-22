var pmlZipcode;
var pmlState;
var pmlCountyCode;
var pmlCharacteristic;
var pmlValue;
var pmlUnit;
var pmlSiteUri;
var pmlSitePermits;
var pmlDataSrc;
var pmlDataSet;
var pmlDataVersion;
var pmlInfEng;
var pmlDataDownloadTime;
var pmlDataConvertTime;
var pmlAccountName;
var pmlRawDataUrl;


var provAppBase =parseParent(window.location.href); 
var provWin; //refers to the pop out provenance window
var provServiceagent="http://localhost/trend/trendData.php";
var provEchoStates = {"CA": true, "MA": true, "NY": true, "RI": true};
var publishUrl='http://sparql.tw.rpi.edu/';


//Array that contains the name of the regulations
var regNames= new Array();
regNames['EPA'] = "EPA_Drinking_Water_Regulations";
regNames['ri'] = "Rhode_Island_Water_Quality_Regulations";
regNames['ny'] = "New_York_Drinking_Water_Regulations";
regNames['ma'] = "2011_Standards_and_Guidelines_for_Contaminants_in_Massachusetts_Drinking_Water";
regNames['ca'] = "California_Code_of_Regulations";

//--------------------------------------------------------------------
/*This function is called when provenance.html is loaded
It parse the parameters from the given url
then starts query the provenance info from the sparql end point
*/
function showPml(){
  document.getElementById("moreDetailsDiv").style.display = "none";
  var url=location.href;
  //alert(url);
  var params = parseQuery(url);
  //common parameters
  pmlCharacteristic = params["chract"];  
  if(!pmlCharacteristic || pmlCharacteristic.length==0){
    alert("No characteristic! Please check and try again.");
    return;
   }
  pmlValue = params["mValue"];  
  if(!pmlValue || pmlValue.length==0){
    alert("No value! Please check and try again.");
    return;
   }
  pmlUnit = params["unit"];

  //if this is for regulation provenance
  var isReg=params["isReg"]; 
  if(isReg=="true"){
    regId=params["dataSet"];
    pmlDataSet = regNames[regId];
    //alert(pmlDataSet);
	  queryRegulationPml(regId);
	}
  else {
    pmlDataSrc = params["dataSrc"];  
    pmlDataSet = params["dataSet"];  
    pmlZipcode = params["zip"]; 
    pmlState = params["state"];  
    pmlStateCode = params["stateCode"];  
    pmlCountyCode = params["countyCode"];  
    //alert(pmlCharacteristic, pmlValue, pmlUnit, pmlDataSrc, pmlDataSet, pmlState, pmlCountyCode);
    pmlSiteUri = params["siteUri"];  

    if(pmlDataSrc=="usgs-gov"){
      pmlDataVersion="2011-Mar-20";
      queryUSGSMeasurementPml(pmlDataSrc, pmlState, pmlCountyCode);
    }
    else{
      if(pmlDataSet=="foia"){
        pmlDataVersion="2011-Jul-23";
        setDataDownloadTime("2011-07-23");
      }
      else{
        pmlDataVersion="2011-Mar-19";
        setDataDownloadTime("2011-03-19");
      }
      queryEPAMeasurementPml(pmlDataSrc, pmlDataSet, pmlState);
    }
  }
}

/*This function is called in main.js and when the ? is clicked on map.html
It prepares the parameters to send to the pop up window and opens the new window
Params: 
isReg: if it is true then the provenance is for regulation, otherwise, it is for measurement
fromEPA: if it's true then the provenance is for measurement from EPA, otherwise, the measurement is from USGS
*/
function openProvWindow(characteristic, measuredValue, unit, isReg, fromEPA, siteUri){
  //build url parameters string
  var height=450;
  var urlParam = 'chract=' + characteristic + '&mValue=' + measuredValue+'&unit=' + unit+'&isReg=' + isReg;
  if(isReg){
    height=300;
    urlParam += '&dataSet=' + regulation.substring(regulation.lastIndexOf('/')+1, regulation.lastIndexOf('-'));
  }
  else{
   //alert(characteristic+measuredValue+unit+fromEPA+siteUri);
    var dataSrc="usgs-gov";
    var dataSet="nwis"
    if(fromEPA){
      dataSrc="epa-gov";
    if(state in provEchoStates)   
				dataSet="echo";
    else
       dataSet="foia";
    }

	  if(countyCode.length==2)
		  countyCode="0"+countyCode;
	  else if(countyCode.length==1)
		  countyCode="00"+countyCode;

    urlParam += '&dataSrc='+dataSrc + '&dataSet=' + dataSet +'&zip='+zipcode;
    urlParam += '&state='+state.toLowerCase() + '&stateCode='+stateCode +'&countyCode='+countyCode +'&siteUri='+siteUri;
    }
  
    popup(provAppBase+'prov/provenance.html?'+urlParam, 600, height);
}


//--------------------------------------------------------------------
/*After the provenance info for the regulation is retrieved from the end point, 
the function displays the info*/
function showPmlRecord4Regulations(){
	var contents="Limit Value for " + pmlCharacteristic + ": "+ pmlValue + pmlUnit+"<br>";
  contents+="The "+ pmlDataSet.replace(/\_/g,' ')+" were got from <a href=\""+pmlDataSrc+"\">" + pmlDataSrc+"</a> on "+ pmlDataDownloadTime+"<br>";
  contents+="And they were ingested into SemantAQUA by <a href=\"http://tw.rpi.edu/web/person/"+pmlAccountName+"\">"+pmlAccountName+"</a> via " + pmlInfEng + " on "+pmlDataConvertTime+"<br>";
	var pmlDiv=document.getElementById("pmlInfo");
	pmlDiv.innerHTML+=contents;
}


/*After the provenance info for the measurement is retrieved from the end point, 
the function displays the info*/
function showPmlRecord4Measurement(){
	var contents="Measured Value for " + pmlCharacteristic + ": "+ pmlValue + pmlUnit+"<br>";
  contents+="This data was got from " + pmlDataSrc.toUpperCase()+" on "+ pmlDataDownloadTime+"<br>";
  contents+="And it was ingested into SemantAQUA by <a href=\"http://tw.rpi.edu/web/person/"+pmlAccountName+"\">"+pmlAccountName+"</a> via " + "<a href=\"https://github.com/timrdf/csv2rdf4lod-automation\">"+pmlInfEng + "</a> on "+pmlDataConvertTime+"<br>";

  //alert(contents);    
	var pmlDiv=document.getElementById("pmlInfo");
	pmlDiv.innerHTML+=contents;
  document.getElementById("moreDetailsDiv").style.display = "block";
}

/*After more provenance details for the measurement are retrieved from the end point, 
the function displays the details*/
function showPmlDetails4Measurement(){
  var contents=showRawDataSource();
  contents+=showFiles4Measurement();
  contents+="<a href=\"http://tw.rpi.edu/web/doc/SWQP_Tech_Report\">Learn more about the data conversion process.</a><br>";
  var pmlDiv=document.getElementById("pmlDetails");
	pmlDiv.innerHTML+=contents;
}

/*Display the raw data source of the measurement*/
function showRawDataSource(){
  var contents="The orignial source data is available at:<br>";
  if(pmlDataSet=="echo"||pmlDataSet=="foia"){
    pmlRawDataUrl="http://www.epa-echo.gov/cgi-bin/effluentdata.cgi";
  }
  
  contents+="<a href=\""+pmlRawDataUrl+"\">"+pmlRawDataUrl+"</a>";
  
  switch (pmlDataSet){
  case "echo":
    contents+=" (input zipcode "+ pmlZipcode + " and search for facility with id "+pmlSiteUri.substring(pmlSiteUri.lastIndexOf('-')+1)+ ")<br>";
    break;
  case "foia":
    contents+=" (input zipcode "+ pmlZipcode + " and search for facility with id "+pmlSiteUri.substring(pmlSiteUri.lastIndexOf('-')+1)+ ")<br>";
    contents+="We got this data via a <a herf=\'http://www.epa.gov/foia/\'>FOIA request.</a><br>"
    break;
  case "nwis":
    contents+="<br>";
    break;
  default:
    break;
  } 
  return contents;
}

/*Display the data files of the measurement hosted at our web site*/
function showFiles4Measurement(){
  var contents ="<br>The data files are available at our site as follows:<br>";
  //Example source urls:
  //echo: http://sparql.tw.rpi.edu/source/epa-gov/provenance_file/echo-measurements-ri/version/2011-Mar-19/source/RI0000019.csv
  //foia: http://sparql.tw.rpi.edu/source/epa-gov/provenance_file/foia-measurements-ak/version/2011-Jul-23/manual
  //usgs: http://sparql.tw.rpi.edu/source/usgs-gov/provenance_file/nwis-measurements-nh/version/2011-Mar-20/source/US-33-000-result.csv
  contents+="<ul><li>Source file(s):<br>";
  var urlPrefix=" "+publishUrl+"source/"+pmlDataSrc+"/provenance_file/"+pmlDataSet+"-measurements-"+pmlState+"/version/"+pmlDataVersion;
  var curUrl;
  switch (pmlDataSet){
  case "echo":
		for (var i = 0; i < pmlSitePermits.length; i++) {
      curUrl=urlPrefix+"/source/"+pmlSitePermits[i]+".csv";
      contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    }
    break;
  case "foia":
    curUrl=urlPrefix+"/manual/";
    contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    break;
  case "nwis":
    curUrl=urlPrefix+"/source/US-"+pmlStateCode+"-"+pmlCountyCode+"-result.csv";
    contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    break;
  default:
    break;
  } 
  contents+="</li><li>Parameter file(s):<br>";
  var curUrl;
  switch (pmlDataSet){
  case "echo":
		for (var i = 0; i < pmlSitePermits.length; i++) {
      curUrl=urlPrefix+"/manual/"+pmlSitePermits[i]+".csv.e1.params.ttl";
      contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    }
    break;
  case "foia":
    curUrl=urlPrefix+"/manual/";
    contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    break;
  case "nwis":
    curUrl=urlPrefix+"/manual/US-"+pmlStateCode+"-"+pmlCountyCode+"-result.csv.e1.params.ttl";
    contents+="<a href=\""+curUrl+"\">"+curUrl+"</a><br>";
    break;
  default:
    break;
  }
  //Example converted file urls:
  //http://sparql.tw.rpi.edu/source/usgs-gov/file/nwis-measurements-ak/version/2011-Mar-20/conversion/usgs-gov-nwis-measurements-ak-2011-Mar-20.ttl.gz
  contents+="</li><li>Converted file(s) available at:<br>";
  curUrl=" "+publishUrl+"source/"+pmlDataSrc+"/file/"+pmlDataSet+"-measurements-"+pmlState+"/version/"+pmlDataVersion+"/conversion/"+pmlDataSrc+"-"+pmlDataSet+"-measurements-"+pmlState+"-"+pmlDataVersion+".ttl.gz";
  contents+="<a href=\""+curUrl+"\">"+curUrl+"</a></li></ul>";

  return contents;
}

/*This function queries the provenance for measurements from EPA
Provenance queried: the inference engine
*/
function queryEPAMeasurementPml(dataSrc, dataSet, state){    
  var sparqlEPAMeasurementPml="PREFIX pmlj: <http://inference-web.org/2.0/pml-justification.owl#>\r\n"+
        "select distinct ?inf1Engine\r\n"+ 
        "where {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/"+dataSrc+"/dataset/"+dataSet+"-measurements-"+state+"/version/"+pmlDataVersion+"> {\r\n"+
        "?s1 pmlj:hasConclusion <http://sparql.tw.rpi.edu/source/"+dataSrc+"/file/"+dataSet+"-measurements-"+state+"/version/"+pmlDataVersion+"/conversion/"+dataSrc+"-"+dataSet+"-measurements-"+state+"-"+pmlDataVersion+".ttl.gz> .\r\n"+
        "?s1 pmlj:isConsequentOf ?inf1.\r\n"+
        "?inf1 pmlj:hasInferenceEngine ?inf1Engine.\r\n"+
        "}\r\n"+
        "}";

  //alert(sparqlEPAMeasurementPml);

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlEPAMeasurementPml),
          dataType: "xml", 
          success: processMeasurementPml,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

/*This function queries the provenance for measurements from USGS
Provenance queried: the inference engine, raw data source, data download time
*/
function queryUSGSMeasurementPml(dataSrc, state, countyCode){
	//alert(characteristic);
	var sparqlUSGSMeasurementPml="PREFIX pmlp: <http://inference-web.org/2.0/pml-provenance.owl#>\r\n"+
        "PREFIX pmlp1: <http://inference-web.org/2.1exper/pml-provenance.owl#>\r\n"+
        "PREFIX pmlj: <http://inference-web.org/2.0/pml-justification.owl#>\r\n"+
        "PREFIX profile: <http://www.daml.org/services/owl-s/1.1/Profile.owl#>\r\n"+
        "select distinct ?inf1Engine ?inf3source ?inf3dateTime\r\n"+
        "where {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/"+dataSrc+"/dataset/nwis-measurements-"+state+"/version/2011-Mar-20> {\r\n"+
        "?s1 pmlj:hasConclusion <http://sparql.tw.rpi.edu/source/"+dataSrc+"/file/nwis-measurements-"+state+"/version/2011-Mar-20/conversion/"+dataSrc+"-nwis-measurements-"+state+"-2011-Mar-20.ttl.gz> .\r\n"+
        "?s1 pmlj:isConsequentOf ?inf1.\r\n"+
        "?inf1 pmlj:hasInferenceEngine ?inf1Engine.\r\n"+
        "?inf1 pmlp1:hasAntecedentRole ?atRoles1.\r\n"+
        "?atRoles1 pmlp1:hasAntecedent ?at1.\r\n"+
        "?atRoles1 pmlp1:hasRole <http://inference-web.org/registry/ROLE/Input.owl#Input>.\r\n"+
        //"?inf1 pmlp1:hasAntecedentRole ?atRoles2.\r\n"+
        //"?atRoles2 pmlp1:hasAntecedent ?at2.\r\n"+
        //"?atRoles2 pmlp1:hasRole <http://inference-web.org/registry/ROLE/Parameters.owl#Parameters>.\r\n"+
        "?s2 pmlj:hasConclusion ?at1 .\r\n"+
        "?s2 pmlj:isConsequentOf ?inf2.\r\n"+
        "?inf2 pmlj:hasSourceUsage ?inf2sourceUsage  .\r\n"+
        //"?inf2sourceUsage pmlp:hasUsageDateTime ?inf2dateTime .\r\n"+
        "?inf2sourceUsage pmlp:hasSource ?inf2source .\r\n"+
        "?s3 pmlj:hasConclusion ?inf2source .\r\n"+
        "?s3 pmlj:isConsequentOf ?inf3.\r\n"+
        "?inf3 pmlj:hasSourceUsage ?inf3sourceUsage  .\r\n"+
        "?inf3sourceUsage pmlp:hasUsageDateTime ?inf3dateTime .\r\n"+
        "?inf3sourceUsage pmlp:hasSource ?inf3source .\r\n"+
        "}\r\n"+
        "FILTER regex(?at1, \""+countyCode+"\")  \r\n"+
        "}";

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlUSGSMeasurementPml),
          dataType: "xml", 
          success: processMeasurementPml,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processMeasurementPml(data) {
	$(data).find('result').each(function(){
	var infEng="", dataTime="", srcUrl="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="inf1Engine")
	  {
	    infEng=($(this).find("uri").text());     
      if(infEng!=""){
        var sIdx=infEng.lastIndexOf('/')+1;
				infEng=infEng.substring(sIdx);
		    var eIdx=infEng.indexOf('_');
				infEng=infEng.substring(0, eIdx);			  
		    pmlInfEng=infEng;
      }
	  }
	  if($(this).attr("name")=="inf3dateTime")
	  {
	    dataTime=($(this).find("literal").text()); 
      if(dataTime!="")
		    setDataDownloadTime(dataTime);
	  }
	  if($(this).attr("name")=="inf3source")
	  {
	    srcUrl=($(this).find("uri").text()); 
      if(srcUrl!="")
		    pmlRawDataUrl=srcUrl;
	  }
	});
    //alert("infEng: "+infEng+", dataTime: "+dataTime);
	});

  queryConvTime(pmlDataSrc, pmlState)
}


/*This function queries the provenance: data converstion time
*/
function queryConvTime(dataSrc, state){
	var sparqlUSGSConvTime="PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+
        "select distinct ?modTime\r\n"+
        "where {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/"+dataSrc+"/dataset/"+pmlDataSet+"-measurements-"+state+"/version/"+pmlDataVersion+"> {\r\n"+
        " ?dataSet dcterms:modified ?modTime.\r\n"+
        " ?dataSet a void:Dataset.\r\n"+
        "}\r\n"+
        "}";

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlUSGSConvTime),
          dataType: "xml", 
          success: processConvTime,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processConvTime(data) {
	var timeArr = new Array();
  var timeAlert="";

	$(data).find('result').each(function(){
	var modTime="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="modTime")
	  {
	    modTime=($(this).find("literal").text());
			//alert("modTime: "+modTime);
      
      if(modTime!="")
				timeArr.push(modTime);
	  }
	});    
	});
	timeArr.sort();
	for (var i = 0; i < timeArr.length; i++) 
		timeAlert+=timeArr[i]+"\n";
  //alert(timeAlert);
  //alert(timeArr[timeArr.length-1]);
  setDataConvertTime(timeArr[timeArr.length-1]);
  //call the next fun
  queryAccountName(pmlDataSrc, pmlState);
}

/*This function queries the provenance: the account which processes the data
*/
function queryAccountName(dataSrc, state){
	var sparqlAccountName="PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n"+ 
        "PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+ 
        "select distinct ?user\r\n"+
        "where {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/"+dataSrc+"/dataset/"+pmlDataSet+"-measurements-"+state+"/version/"+pmlDataVersion+"> {\r\n"+
        "?conversion dcterms:creator ?act.\r\n"+
        "?user foaf:holdsAccount ?act .\r\n"+
        "}\r\n"+
        "}";
  //alert(sparqlAccountName);

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlAccountName),
          dataType: "xml", 
          success: processAccountName,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}

function processAccountName(data) {
	$(data).find('result').each(function(){
	var actName="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="user")
	  {
	    actName=($(this).find("uri").text());
			//alert("actName: "+actName);      
      if(actName!=""){
        var idx=actName.lastIndexOf('/')+1;
			  actName=actName.substring(idx);
        //alert(actName);
				pmlAccountName=actName;
      }
	  }
	});    
	});


  if(pmlDataSet=="echo")
	  queryFacPermits(pmlSiteUri); 
  else
    //done with query, show the pml info
    showPmlRecord4Measurement();
}

/*This function queries the permits of the selected facility
*/
function queryFacPermits(facUri){
	var sparqlFacilityPermits="PREFIX pol: <http://escience.rpi.edu/ontology/semanteco/2/0/pollution.owl#>\r\n"+
        "SELECT DISTINCT ?permit\r\n"+
        "WHERE {\r\n"+
        "graph <http://sparql.tw.rpi.edu/source/"+pmlDataSrc+"/dataset/"+pmlDataSet+"-facilities-"+pmlState+"/version/"+pmlDataVersion+">{\r\n"+
        "<"+facUri+"> pol:hasPermit ?permit .\r\n"+
        "}}";

	//alert(sparqlFacilityPermits);

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlFacilityPermits),
          dataType: "xml", 
          success: processFacPermits,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}


function processFacPermits(data) {
	pmlSitePermits = new Array();

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
				pmlSitePermits.push(permit);				
			}
	  }
	});
	});
	pmlSitePermits.sort();

  //done with query, show the pml info
  showPmlRecord4Measurement();
}

/*This function queries the provenance for regualations
Provenance queried: the inference engine, data source url, data download time, 
data convert time and the user who processes the data
*/
function queryRegulationPml(regId){
  var regOwl=regNames[regId]+".owl";

 var sparqlRegulationPml="PREFIX foaf: <http://xmlns.com/foaf/0.1/> \r\n"+ 
        "PREFIX oboro:      <http://obofoundry.org/ro/ro.owl#> \r\n"+ 
        "PREFIX pmlp: <http://inference-web.org/2.0/pml-provenance.owl#>\r\n"+ 
        "PREFIX pmlj: <http://inference-web.org/2.0/pml-justification.owl#>\r\n"+ 
        "select ?srcUrl ?downloadTime ?infEng ?convertTime ?user\r\n"+ 
        "where {\r\n"+ 
        "graph <http://sparql.tw.rpi.edu/semantaqua/water-regulations/provenance> {\r\n"+ 
        "<"+regOwl+"> pmlp:hasReferenceSourceUsage ?su1.\r\n"+
        "?su1 pmlp:hasSource ?csvfile.\r\n"+ 
        "?su1 pmlp:hasUsageDateTime ?convertTime.\r\n"+ 
        "?infStep1 pmlj:hasSourceUsage ?su1.\r\n"+ 
        "?infStep1 pmlj:hasInferenceEngine ?infEng.\r\n"+ 
        "?infStep1 oboro:has_agent ?agt .\r\n"+ 
        "?user foaf:holdsAccount ?agt .\r\n"+ 
        "?csvfile pmlp:hasReferenceSourceUsage ?su2.\r\n"+ 
        "?su2 pmlp:hasSource ?srcfile.\r\n"+ 
        "?srcfile pmlp:hasReferenceSourceUsage ?su3.\r\n"+ 
        "?su3 pmlp:hasSource ?srcUrl.\r\n"+ 
        "?su3 pmlp:hasUsageDateTime ?downloadTime.\r\n"+ 
        "}}";
  //alert(sparqlRegulationPml);

       $.ajax({type: "GET",
          url: provServiceagent,
          data: "query="+encodeURIComponent(sparqlRegulationPml),
          dataType: "xml", 
          success: processRegulationPml,
         	error: function (jqXHR, textStatus, errorThrown){
						alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         	}
     });
}


function processRegulationPml(data) {
	$(data).find('result').each(function(){
	var srcUrl="", dTime="", infEng="", cTime="", actName="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="srcUrl")
	  {
	    srcUrl=($(this).find("uri").text());
			//alert("srcUrl: "+srcUrl);      
      if(srcUrl!="")
				pmlDataSrc=srcUrl;
	  }
	  if($(this).attr("name")=="downloadTime")
	  {
	    dTime=($(this).find("literal").text());
			//alert("DownloadTime: "+dTime);      
      if(dTime!="")
				setDataDownloadTime(dTime);
	  }
	  if($(this).attr("name")=="infEng")
	  {
	    infEng=($(this).find("uri").text());     
      if(infEng!=""){
        var sIdx=infEng.lastIndexOf('/')+1;
				infEng=infEng.substring(sIdx);
		    var eIdx=infEng.indexOf('_');
				infEng=infEng.substring(0, eIdx);			  
		    pmlInfEng=infEng;
      }
	  }
	  if($(this).attr("name")=="convertTime")
	  {
	    cTime=($(this).find("literal").text()); 
      if(cTime!="")
		    setDataConvertTime(cTime);
	  }
	  if($(this).attr("name")=="user")
	  {
	    actName=($(this).find("uri").text());
			//alert("actName: "+actName);      
      if(actName!=""){
        var idx=actName.lastIndexOf('/')+1;
			  actName=actName.substring(idx);
        //alert(actName);
				pmlAccountName=actName;
      }
	  }
	});    
	});
  //done with query, show the pml info
  showPmlRecord4Regulations();
}

function setDataDownloadTime(dataTime){
  dataTime=dataTime.substring(0, 10);
  pmlDataDownloadTime=dataTime;
}

function setDataConvertTime(dataTime){
  //alert(dataTime);
  if(dataTime && dataTime.length >= 11)
    dataTime=dataTime.substring(0, 10);
  pmlDataConvertTime=dataTime;
}


/*This function pops up a window in the center of the screen.
It is from http://www.javascript-array.com/scripts/window_open/*/
function popup(url, width, height) 
{
 var left   = (screen.width  - width)/2;
 var top    = (screen.height - height)/2;
 var params = 'width='+width+', height='+height;
 params += ', top='+top+', left='+left;
 params += ', directories=no';
 params += ', location=no';
 params += ', menubar=no';
 params += ', resizable=1';
 params += ', scrollbars=1';
 params += ', status=no';
 params += ', toolbar=no';
 provWin=window.open(url,'', params);
 if (window.focus) {provWin.focus()}
 return false;
}

