/* -*- espresso-indent-level: 2; tab-width: 8; -*- */
var XHTML="http://www.w3.org/1999/xhtml";
var data_source=new Array();
var regulation="EPA-regulation";
var data_type=new Array();
data_source["EPA"]=1;
data_source["USGS"]=1;
//var visualizeBaseUrl="http://localhost/visualize.php";
var zipagent="http://was.tw.rpi.edu/water/service/zip";
var orgpediaAgent="http://was.tw.rpi.edu/swqp/orgpediaData.php";
var visualizeEPABaseUrl="http://was.tw.rpi.edu/swqp/trend/epaTrend.html";
var visualizeUSGSBaseUrl="http://was.tw.rpi.edu/swqp/trend/usgsTrend.html";

if(document.createElementNS===undefined) {
  document.createElementNS = function(a,b) {
    return document.createElement(b);
  };
}

function submitQuery(value,name){

if(name=="data_source"){ 
	if(document.getElementById(value).checked==true){
	  data_source[value]="1";
	}
    else{
	   data_source[value]="0";
    }	
}
else if(name=="regulation"){
     regulation=value;
}
else if(name=="data_type"){
	if(document.getElementById(value).checked==true){
	  data_type[value]="1";
	}
    else{
	   data_type[value]="0";
    }	
}
}



function parent(x) {
  return x.parentElement ? x.parentElement : x.parentNode;
}

function spinner() {
  var x = document.createElementNS(XHTML,"img");
  x.setAttribute("src","spinner.gif");
  x.setAttribute("alt","loading...");
  return x;
}

function showSparqlQuery(text) {
  var dom = document.getElementById("sparql-content");
  dom.innerHTML = text.replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/\r\n/g,"<br/>");
  parent(dom).style.display = "block";
  dom = document.getElementById("sparql2").style.display = "block";
}

function hideSparql() {
  var dom = document.getElementById("sparql-content");
  parent(dom).style.display = "none";
  dom = document.getElementById("sparql2").style.display = "none";
}

function queryButton(site, func) {
  var a = document.createElementNS(XHTML,"a");
  var img = document.createElementNS(XHTML,"img");
  img.setAttribute("src","query.png");
  img.setAttribute("alt","Show underlying query");
  img.setAttribute("title","Show underlying query");
  a.setAttribute("title","Show underlying query");
  a.appendChild(img);

  if(a.addEventListener) {
    a.addEventListener("click",function() {
      showSparqlQuery(func.call(window,site,true,null));
    },false);
  }
  else {
    a.attachEvent("onclick",function() {
      showSparqlQuery(func.call(window,site,true,null));
    });
  }
  return a;
}

function td(body, text) {
  var td = document.createElementNS(XHTML,"td");
  if(body) {
    td.appendChild(body);
  }
  else {
    td.appendChild(document.createTextNode(text));
  }
  return td;
}
function showwaterregulationpml(threshold,element,value){
var query = 
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
	"PREFIX pmlp: <http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#>\r\n"+
	"SELECT DISTINCT ?csv_document ?original_document ?threshold WHERE {\r\n"+
	//"?information pmlp:hasURL \""+threshold+"\". \r\n"+
	"?information pmlp:hasURL ?threshold. \r\n"+
	"?information pmlp:hasReferenceSourceUsage ?csv_source. \r\n"+
	"?csv_source pmlp:hasSource ?csv_document.\r\n"+
	"<http://tw2.tw.rpi.edu/zhengj3/owl/"+regulation+".pml#ORIGINAL-WQR> pmlp:hasSource ?original_document.\r\n"+
	"}";
var contents="";
var original="",csv="";
	   var success = function(data) {
	contents += "Water Regulation Provenance: http://tw2.tw.rpi.edu/zhengj3/owl/"+regulation+".pml\n";
	contents += "characteristic: "+element+"\n";
	contents += "limit: "+value+"\n";
	contents += "From:\n";
	contents += "RDF Source: "+threshold+"\n";
	
      $(data).find('result').each(function(){
	
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="csv_document")
	  {
	    csv=($(this).find("uri").text());
	  }

	  if($(this).attr("name")=="original_document")
	  {
	    original=($(this).find("uri").text());
	  }
	     
	   });
	   });
	   if(csv!=""){
		contents+= "CSV Source: "+csv+"\n";
	   }
	   if(original!=""){
	    contents+= "Original Source: "+original+"\n";
	   }
	   alert(contents);
	   //icon.openInfoWindow(contents);
	   };
	   var source=null;
   if(data_source["USGS"]==1)  
      source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start=0&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
    $.ajax({type: "GET",
	    url: thisserviceagent, // SPARQL service URI		    
	    data: parameter, // query parameter
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });	
	
}
function showwaterdatapml(){
	//need pml file
	var contents="";
	//var stateCode=thisStateCode.replace(":","-");
	if(countyCode.length==2)
		countyCode="0"+countyCode;
	else if(countyCode.length==1)
		countyCode="00"+countyCode;
	contents += "Water Data Provenance: http://tw2.tw.rpi.edu/zhengj3/pml/US-"+stateCode+"-"+countyCode+"-result.zip.pml.ttl\n";
	contents += "From:\n";
	contents += "RDF Source: http://tw2.tw.rpi.edu/zhengj3/demo/waterData.php?state="+state+"&county="+countyCode+"&start="+start+"&limit="+limit+"&source=USGS\r\n";
	contents +="Original Source: http://qwwebservices.usgs.gov/Result/search?statecode=US:44&countycode=US:44:001&mimeType=csv&zip=yes";
	alert(contents);
}
function showfacilitypml(permit,element, value, operator){

    var contents="";
	contents += "Facility Regulation & Data Provenance: http://tw2.tw.rpi.edu/zhengj3/pml/"+permit+".csv.pml.ttl\n";
	contents += "characteristic: "+element+"\n";
	contents += "limit: "+value+"\n";
	contents += "Comparator: "+operator+"\n";
	contents += "From:\n";
	contents += "RDF Source: http://tw2.tw.rpi.edu/zhengj3/demo/facilityData.php?state"+state+"&county="+countyCode+"&start="+start+"&limit="+limit+"\n";
	
	contents += "Original Source: http://www.epa-echo.gov/cgi-bin/effluentdata.cgi permit="+permit+" & hits = 1\n";
	alert(contents);
}

function queryForWaterPollution(site, justQuery, icon) {
  
  

  var query =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "SELECT DISTINCT ?site ?unitlimiter ?measure ?element ?value ?unit ?time ?valuelimit ?threshold WHERE {\r\n"+
	//"Describe ?threshold WHERE{"+
	
    (justQuery ? "# Find measurements that exceed threshold\r\n" : "")+
    "<"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "?measure rdf:type epa:ExceededThreshold .\r\n"+
    "?measure epa:hasElement ?element .\r\n"+	
    (justQuery ? "# Get element label for pretty-printing\r\n" : "")+
    "?measure epa:hasValue ?value .\r\n"+
    "?measure epa:hasUnit ?unit .\r\n"+  
    (justQuery ? "# Retrieve threshold information from regulation ontology\r\n" : "")+
    "?measure rdf:type ?threshold .\r\n"+	
    "?threshold owl:intersectionOf ?desc .\r\n"+
    "?desc list:member ?restriction .\r\n"+
    "?restriction owl:onProperty epa:hasValue .\r\n"+
    "?restriction owl:someValuesFrom ?valueRestriction .\r\n"+
     "?valueRestriction owl:withRestrictions ?desc2 .\r\n"+
    "?desc2 list:member ?limiter .\r\n"+
    "?limiter xsd:minInclusive ?valuelimit .\r\n"+
	"?threshold owl:intersectionOf ?desc4 .\r\n"+
    "?desc4 list:member ?restriction2. "+
	"?restriction2 owl:onProperty epa:hasUnit. "+
	"?restriction2 owl:hasValue ?unitlimiter. "+
    "?measure time:inXSDDateTime ?time .\r\n"+
    "} ORDER BY DESC(?time)";

  //var contents="";
  alert(site.label);
  var contents="<p> hello"+site.label+"</p>";
  if(site.isPolluted) {
    
    var success = function(data) {
	contents += "<table border=1><tr><th>Pollutant</th><th>Measured Value</th><th>Limit Value</th><th>Time</th><th>Health</th></tr>";
      $(data).find('result').each(function(){
	var th="",time="",unit="",limit="",label="",value="",page="",element="";
	$(this).find("binding").each(function(){

	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="unit")
	  {
	    unit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="valuelimit")
	  {
	    limit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="unitlimiter")
	  {
	    unitlimit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="time")
	  {
	    time=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="element")
	  {
	    element=($(this).find("uri").text());
		element=element.replace("http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#","");
	  }
	  if($(this).attr("name")=="threshold"){
		th = ($(this).find("uri").text());
		//alert(th+"222");
	  }	  
	  if(element!=""&&value!=""&&(unit!=""||value=="0")&&limit!=""&&time!=""&&th!=""){

		contents += "<tr><td>"+element+"</td><td>"+value+" "+unit+"<a href=\"javascript:showwaterdatapml()\">?</a></td><td>"+limit+" "+unitlimit+"<a href=\"javascript:showwaterregulationpml('"+th+"','"+element+"','"+limit+"')\">?</a></td><td>"+time+"</td><td>";
		//		contents += "<a href=\"javascript:healthEffectDesc(\""+element+"\")\">Health Effect?</a>";
		contents += healthEffectDesc(element);
	      contents +="</td></tr>";

	    //alert(th+"333");
		//threshold="http://tw2.tw.rpi.edu/zhengj3/owl/EPA-regulation.owl#ExcessiveGiardialambliaMeasurement";

		
	  }
	});
      });
	  contents +="</table>";
	  contents+="<p><a href='"+visualizeUSGSBaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize Characteristics</a></p>";
      icon.openInfoWindow(contents);
    };
	   var source=null;
   if(data_source["USGS"]==1)  
      source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start=0&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
    $.ajax({type: "GET",
	    url: thisserviceagent, // SPARQL service URI		    
	    data: parameter, // query parameter
		async: false,
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });
	   /*
	      var success2 = function(data) {
      $(data).find('result').each(function() {
	$(this).find("binding").each(function() {
	  if($(this).attr("name")=="lastTime") {
	    mostRecent.removeChild(mostRecentSpinner);
	    mostRecent.appendChild(document.createTextNode($(this).find("literal").text()));
	  }
	});
      });
    };
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent",
	    data: "session="+window.sessionID+
	    "&query="+encodeURIComponent(timeQuery),
	    beforeSend: function(xhr) {
	      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
	    dataType: "xml",
	    error: function(xhr, text, err) {
	      if(xhr.status == 200) {
		success2(xhr.responseXML);
	      }
	    },
	    success: success2
	   });
	   */
    return contents;
  }
  else {
    contents += "<p>According to all current regulations, this water supply is not polluted</p>";
	  contents+="<p><a href='"+visualizeUSGSBaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize Characteristics</a></p>";
    return contents;
  }
}
function healthEffectDesc(element){

	      var healthsparql = "prefix epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#> prefix owl:<http://www.w3.org/2002/07/owl#> PREFIX list: <http://jena.hpl.hp.com/ARQ/list#> select distinct ?effect where{?healthEffect epa:hasCause ?cause. ?cause owl:intersectionOf ?restrictions. ?restrictions list:member ?restriction. ?restriction owl:onProperty epa:hasElement. ?restriction owl:hasValue <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#"+element+">. ?healthEffect epa:hasEffect ?effect.}";

	      var params = "data=water&state=RI&countyCode=3&start=0&limit=1&source=USGS&regulation=EPA-regulation&query="+encodeURIComponent(healthsparql);


	      var healthString ="";

	      $.ajax({type:"GET",
			  url:thisserviceagent,
			  data:params,
			  async: false,
			  beforeSend: function(xhr) {
			  xhr.setRequestHeader("Accept", "application/sparql-results+xml");
		      },
			  dataType: "xml",
			  error: function(xhr, text, err) {
			  if(xhr.status == 200) {
			      success(xhr.responseXML);
			  }
		      },
			  success: function(data){
			  $(data).find('result').each(function(){
				  $(this).find("binding").each(function(){
					  if($(this).attr("name")=="effect"){
					      var effectUri =  $(this).find("uri").text();
					      healthString += effectUri.substring(effectUri.indexOf("#")+1)+", ";
					      
					  }
				      }
				      )}
			      )}			     				     
		  });
	      return healthString;

}

function queryForFlood(site, justQuery, icon) {
  
  

  var query =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "SELECT DISTINCT ?measure ?value ?valuelimit WHERE {\r\n"+
	//"Describe ?threshold WHERE{"+
	
    (justQuery ? "# Find measurements that exceed threshold\r\n" : "")+
    "<"+site.uri+"> epa:hasLevelMeasurement ?measure .\r\n"+
    "?measure rdf:type epa:FloodThreshold .\r\n"+
	"?measure epa:hasValue ?value .\r\n"+	
    (justQuery ? "# Retrieve threshold information from regulation ontology\r\n" : "")+
    "?measure rdf:type ?threshold .\r\n"+	
    "?threshold owl:intersectionOf ?desc .\r\n"+
    "?desc list:member ?restriction .\r\n"+
    "?restriction owl:onProperty epa:hasValue .\r\n"+
    "?restriction owl:someValuesFrom ?valueRestriction .\r\n"+
    "?valueRestriction owl:withRestrictions ?desc2 .\r\n"+
    "?desc2 list:member ?limiter .\r\n"+
    "?limiter xsd:minInclusive ?valuelimit .\r\n"+
    "} ORDER BY DESC(?time)";

	
	
	//document.getElementById("test").innerHTML=query.replace(/</g,"&lt;").replace(/>/g,"&gt;");
  if(justQuery) return query;
  var timeQuery =
    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
    "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
    "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
    "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
    "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
    "PREFIX pmlp: <http://inferenceweb.stanford.edu/2006/06/pml-provenance.owl#>\r\n"+
    "\r\n"+
    "SELECT DISTINCT ?lastTime WHERE {\r\n"+
    "  <"+site.uri+"> epa:hasMeasurement ?measure .\r\n"+
    "  ?measure time:inXSDDateTime ?lastTime .\r\n"+
    "}\r\n"+
    "ORDER BY DESC(?lastTime)\r\n"+
    "LIMIT 1";

  //var contents="";
  alert(site.label);
  var contents="<p> hello"+site.label+"</p>";
 
 
  if(site.isPolluted) {
    
    var success = function(data) {
	contents += "<table border=1><tr><th>Current Level</th><th>Limited Level</th>></tr>";
      $(data).find('result').each(function(){
	var time="",unit="",limit="",label="",value="",page="",element="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text());
	  }

	  if($(this).attr("name")=="valuelimit")
	  {
	    limit=($(this).find("literal").text());
	  }
	  
	  if(value!=""&&limit!=""){
		
		contents += "<tr><td>"+value+"</td><td>"+limit+"</td></tr>";
	  }
	});
      });
	  contents +="</table>";
	  contents+="<p><a href='"+visualizeUSGSBaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize contaminants</a></p>";
      icon.openInfoWindow(contents);
    };
	   var source=null;
   if(data_source["USGS"]==1)  
      source="USGS";
  var parameter="data=water&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start=0&limit="+limit+"&source="+source;
  if(regulation!=""){
  parameter+="&regulation="+regulation;
  }
    $.ajax({type: "GET",
	    url: thisserviceagent, // SPARQL service URI		    
	    data: parameter, // query parameter
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });
	   /*
	      var success2 = function(data) {
      $(data).find('result').each(function() {
	$(this).find("binding").each(function() {
	  if($(this).attr("name")=="lastTime") {
	    mostRecent.removeChild(mostRecentSpinner);
	    mostRecent.appendChild(document.createTextNode($(this).find("literal").text()));
	  }
	});
      });
    };
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent",
	    data: "session="+window.sessionID+
	    "&query="+encodeURIComponent(timeQuery),
	    beforeSend: function(xhr) {
	      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
	    dataType: "xml",
	    error: function(xhr, text, err) {
	      if(xhr.status == 200) {
		success2(xhr.responseXML);
	      }
	    },
	    success: success2
	   });
	   */
    return contents;
  }
  else {
    contents += "<p>According to all current regulations, this water supply is not polluted</p>";
	  contents+="<p><a href='"+visualizeEPABaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize contaminants</a></p>";
    return contents;
  }
}

function queryForFacilityInfo(site, justQuery, icon) {
  
   var source=null;
   if(data_source["EPA"]==0)
   {
    contents+="<p>To get data about facilities, you need to choose EPA as one of your data sources.</p>";
    return contents;
    }
    //EPA is selected as a data source
    source="EPA";
    var UINBegin=site.uri.lastIndexOf("-")+1;
    var facUIN=site.uri.substring(UINBegin);
    var sparqlFacilityInfo="select distinct ?companyuri \r\n"+
	"where{\r\n"+
	"graph <http://tw.rpi.edu/orgpedia/source/epa-gov/facility-registry-system/version/2011-Jul-05>\r\n"+
	"{ <http://tw.rpi.edu/orgpedia/source/epa-gov/id/facility/"+facUIN+"> owl:sameAs ?companyuri.}\r\n"+
	"}";

   alert(sparqlFacilityInfo);

   var success = function(data) {
        var facUri="";
	$(data).find('result').each(function(){
	   $(this).find("binding").each(function(){
	   if($(this).attr("name")=="companyuri")
	   {
	    	facUri=($(this).find("uri").text());
                alert(facUri);
	   }
	   });
	});
       queryForFacilityPollution(site, facUri, justQuery, icon);
   };

       $.ajax({type: "GET",
          url: orgpediaAgent,
          data: "query="+encodeURIComponent(sparqlFacilityInfo),
          dataType: "xml", 
          error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
            success: success
     });
}

function queryForFacilityPollution(site, orgpediaUri, justQuery, icon) {
  alert("In queryForFacilityPollution");
  var query =
   "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
   "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
   "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"+
   "PREFIX time: <http://www.w3.org/2006/time#>\r\n"+
   "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"+
   "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\r\n"+
   "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"+
   "PREFIX list: <http://jena.hpl.hp.com/ARQ/list#>\r\n"+
   "PREFIX dcterms: <http://purl.org/dc/terms/>\r\n"+
   "select distinct * WHERE {\r\n"+
   "<"+site.uri+"> epa:hasMeasurement ?measurement.\r\n"+
   "<"+site.uri+"> epa:hasPermit ?permit.\r\n"+
   "?measurement rdf:type epa:Violation.\r\n"+
   "?measurement epa:hasLimitOperator ?operator.\r\n"+
   "?measurement rdf:value ?value.\r\n"+
   "?measurement epa:hasLimitValue ?limit.\r\n"+
   "?measurement epa:hasElement ?element.\r\n"+
   "?measurement epa:hasUnit ?unit.\r\n"+
   "?measurement dcterms:date ?date.\r\n"+
   "} ORDER BY DESC(?time)\r\n";


  var contents = "";
  //contents+="<p>facility id "+site.label+"</p>";
  if(orgpediaUri!="")
   contents+="<p>Facility: <a href=\""+orgpediaUri+"\">"+site.label+"</a></p>";
  else 
   contents+="<p>Facility:  "+site.label+"</p>";

 
  if(site.isPolluted) {

    
    var success = function(data) {
      contents += "<table border=1><tr><th>Pollutant</th><th>Measured Value</th><th>Operator</th><th>Limit Value</th><th>Time</th></tr>"
      $(data).find('result').each(function(){
	var time="",unit="",limit="",label="",value="",operator="",permit="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="operator")
	  {
	    label=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="value")
	  {
	    value=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="unit")
	  {
	    unit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="limit")
	  {
	    limit=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="date")
	  {
	    time=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="operator")
	  {
	    operator=($(this).find("literal").text());
	  }
	  if($(this).attr("name")=="element") {
	    label=($(this).find("uri").text());
		label=label.replace("http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#","");
	  }
	  if($(this).attr("name")=="permit") {
	    permit=($(this).find("uri").text());
		permit=permit.replace("http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#FacilityPermit-","");
		//alert(permit);
	  }
	  
	  
	  if(label!=""&&value!=""&&unit!=""&&limit!=""&&time!=""&&permit!=""){
		contents += "<tr><td>"+label+"</td><td>"+value+" "+unit+"<a href=\"javascript:showfacilitypml('"+permit+"','"+label+"','"+limit+"','"+operator+"')\">?</a></td><td>"+operator+"</td><td>"+limit+" "+unit+"<a href=\"javascript:showfacilitypml('"+permit+"','"+label+"','"+limit+"','"+operator+"')\">?</a></td><td>"+time+"</td></tr>";
		time="",unit="",limit="",label="",value="",operator="";
	  }
	});
      });
	  contents+="</table>";
	  contents+="<p><a href='"+visualizeEPABaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize contaminants</a></p>";
          contents+="<p>facility id "+site.label+"</p>";
      icon.openInfoWindow(contents);
    };
	   var source=null;
   if(data_source["EPA"]==1)  
      source="EPA";
 var parameter="data=facility&state="+state+"&countyCode="+countyCode+"&query="+encodeURIComponent(query)+"&start=0&limit="+limit+"&type=measurement&source="+source;
    $.ajax({type: "GET",
	    url: thisserviceagent, // SPARQL service URI
	    data: parameter, // query parameter
	    beforeSend: function(xhr) {
              xhr.setRequestHeader("Accept", "application/sparql-results+xml");
            },
	    dataType: "xml",
	    error: function(xhr, text, err) {
              if(xhr.status == 200) {
                success(xhr.responseXML);
              }
            },
	    success: success
	   });
	   /*
    $.ajax({type: "GET",
	    url: "http://was.tw.rpi.edu/water/service/agent",
	    data: "session="+window.sessionID+
	    "&query="+encodeURIComponent(timeQuery),
	    beforeSend: function(xhr) {
	      xhr.setRequestHeader("Accept", "application/sparql-results+xml");
	    },
	    dataType: "xml",
	    error: function(xhr, text, err) {
	      if(xhr.status == 200) {
		success2(xhr.responseXML);
	      }
	    },
	    success: success2
	   });*/
    return contents;
  }
  else {
    contents+="<p>This facility has not violated the Clean Water Act..</p>";
	  contents+="<p><a href='"+visualizeEPABaseUrl+"?state="+state+"&county="+countyCode+"&site="+encodeURIComponent(site.uri)+"'>Visualize contaminants</a></p>";
    return contents;
  }
}

function queryForWaterDataProvider(site, justQuery) {
  
}

function queryForWaterRegulationProvider(site, justQuery) {
  
}

function createMapPopup(site) {
  if(site.isWaterSource) {
    var elem = document.createElementNS(XHTML,"div");
    
  }
}

function submitZip(zip) {


  var xhttp = null;
  if(xhttp==null && window.XMLHttpRequest)
    xhttp = new XMLHttpRequest();
  if(xhttp==null)
    xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  if(xhttp==null) {
    window.alert("Your browser does not support JavaScript XML requests");
    return;
  }
  var elem = document.getElementById("zip");
  var p = parent(elem);
  var spinner = document.getElementById("spinner");
  spinner.style.display = "block";
  xhttp.open("GET",zipagent+"?code="+zip,true);
  
  xhttp.onreadystatechange = function() {
    if(xhttp.readyState==4) {
      spinner.style.display = "none";document.getElementById("test").innerHTML=xhttp.status;
      if(xhttp.status==200) {
	  document.getElementById("test").innerHTML=xhttp.responseText;
        var data = JSON.parse(xhttp.responseText);
        if(data.error!=undefined) {
		document.getElementById("test").innerHTML="error";
        }
        else if(data.session!=undefined) {
	
          spinner.style.display = "block";
          p = document.getElementById("display");
          var mapContainer = document.createElementNS(XHTML,"div");
          var mapContent = document.createElementNS(XHTML,"div");
          var mapLegend = document.createElementNS(XHTML,"div");
          var tbl = document.createElementNS(XHTML,"table");
          var tbody = document.createElementNS(XHTML,"tbody");
          tbl.appendChild(tbody);
          tbl.style.width="100%";
          mapLegend.appendChild(tbl);
          var tr = document.createElementNS(XHTML,"tr");
          tbody.appendChild(tr);
          var td = document.createElementNS(XHTML,"td");
          td.appendChild(document.createTextNode("Legend: "));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          var img = document.createElementNS(XHTML,"img");
          mapContent.style.width="600px";
          mapContent.style.height="400px";
          mapContent.style.marginLeft="auto";
          mapContent.style.marginRight="auto";
          mapContainer.appendChild(mapContent);
          mapLegend.style.width="600px";
          mapLegend.style.marginLeft="auto";
          mapLegend.style.marginRight="auto";
          mapLegend.style.backgroundColor="white";
          mapLegend.style.fontSize="9pt";
          mapContainer.appendChild(mapLegend);
          img.setAttribute("src","image/cleanwater2.png");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Water"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/pollutedwater.png");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Polluted Water"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/facility.jpg");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Facility"));
          tr.appendChild(td);
          td = document.createElementNS(XHTML,"td");
          img = document.createElementNS(XHTML,"img");
          img.setAttribute("src","image/facilitypollute.jpg");
          img.setAttribute("height","12");
          td.appendChild(img);
          td.appendChild(document.createTextNode(" Polluting Facility"));
          tr.appendChild(td);
          parent(p).replaceChild(mapContainer,p);
          mapContainer.setAttribute("id","display");
          window.map = new GMap2(mapContent);
          window.map.setUI(new GMapUIOptions(new GSize(600,400)));
          mapContent.style.borderStyle = "double";
          mapContent.style.borderWidth = "1px";
          mapContent.style.borderColor = "black";
          window.map.setCenter(new GLatLng(data.result.lat, data.result.lng), 12);
          window.map.enableScrollWheelZoom();
          window.geocoder = new GClientGeocoder();
          showPollutedWater();
	  //showViolatedFacility();
        }
      }
      else {
        
      }
    }
  };
  xhttp.send(null);
}

function colorize() {
  var zip = document.getElementById("zip");
  var span = document.getElementById("instruct");
  if(zip.value.length==5) {
    zip.style.borderColor = "#229922";
    zip.style.backgroundColor = "#ddffdd";
    span.style.visibility = "visible";
  }
  else {
    zip.style.borderColor = "#999922";
    zip.style.backgroundColor = "#ffffdd";
    span.style.visibility = "hidden";
  }
}

function verify(e) {
  e = e ? e : window.event;
  var key = e.which ? e.which : e.keyCode;
  var keychar = String.fromCharCode(key);
  if(key==13) {
    submitZip(document.getElementById("zip").value);
    return false;
  }
  if(e.ctrlKey || e.altKey || e.metaKey) return true;
  if((key==null) || (key==0) || (key==8) || (key==9) || (key==27)) {
    return true;
  }
  else if((("0123456789").indexOf(keychar) > -1)) {
    return true;
  }
  return false;
}
