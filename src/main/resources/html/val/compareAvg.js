var usgsElementArr;

function startCompare(){
requestUSGSElementsPerCounty("RI", 44, 1);
}

function requestUSGSElementsPerCounty(stateAbbr, curStateCode, curCountyCode){
	//alert("In requestUSGSElementsPerCounty");

var sparqlUSGSElements ="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"+
        "PREFIX epa: <http://tw2.tw.rpi.edu/zhengj3/owl/epa.owl#>\r\n"+
        "\r\n"+
        "SELECT DISTINCT ?element\r\n"+
        "WHERE {\r\n"+
        //"graph <http://tw2.tw.rpi.edu/water/RI/USGS>\r\n"+
        "graph <http://tw2.tw.rpi.edu/water/USGS/"+stateAbbr+">\r\n"+
        "{\r\n"+
        "?site rdf:type epa:MeasurementSite .\r\n"+
        //"?site epa:hasStateCode "44" .\r\n"+
        //"?site epa:hasCountyCode "3" .\r\n"+
        "?site epa:hasStateCode " + curStateCode + " .\r\n"+
        "?site epa:hasCountyCode " + curCountyCode + " .\r\n"+
        "?site epa:hasUSGSSiteId ?siteId.\r\n"+
        "?measure rdf:type epa:WaterMeasurement .\r\n"+
        "?measure epa:hasUSGSSiteId ?siteId.\r\n"+
        "?measure epa:hasElement ?element .\r\n"+
        "}}";

	alert(sparqlUSGSElements);
 
       $.ajax({type: "GET",
          url: tripleStoreAgent,
          data: "query="+encodeURIComponent(sparqlUSGSElements),
          dataType: "xml", 
          success: processUSGSElementDataPerCounty,
         error: function (jqXHR, textStatus, errorThrown){
					alert(jqXHR.status+", "+textStatus+", "+ errorThrown);
         }
     });
} 

function processUSGSElementDataPerCounty(data) {
	//alert("In processUSGSElementDataPerCounty");
	usgsElementArr=new Array();
	
	$(data).find('result').each(function(){
	var elementUri="", elementName="";
	$(this).find("binding").each(function(){
	  if($(this).attr("name")=="element")
	  {
	    elementUri=($(this).find("uri").text());
			elementName=elementUri.substring(elementUri.indexOf("#")+1)
			//alert(elementName);
			usgsElementArr.push(elementName);
	  }
	});
	});

		for (var i = 0; i < usgsElementArr.length; i++){ 
			document.getElementById("test").innerHTML +=  usgsElementArr[i].replace("<", "").replace(">", "")+" <br>";	
		}

}

